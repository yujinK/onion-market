package com.yujin.onionmarket.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.model.Image
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Category
import com.yujin.onionmarket.data.CategoryResponse
import com.yujin.onionmarket.data.WriteSaleResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.File

class WriteActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var writeService: RetrofitService

    private lateinit var token: String

    private lateinit var spinner: Spinner
    private lateinit var rvImage: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var tvImageCount: TextView

    private var images = mutableListOf<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            images = ImagePicker.getImages(data)
            addImageThumbnail()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        token = Util.readToken(this)
        initRetrofit()
        initToolbar()
        initCategory()
        initContentHint()
        initAddImage()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        writeService = retrofit.create(RetrofitService::class.java)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.finish -> {
                    writeSale()
                    true
                }
                else -> { super.onOptionsItemSelected(it) }
            }
        }
    }

    private fun initCategory() {
        getCategory()
    }

    private fun getCategory() {
        val callCategory = writeService.getCategory(token)
        callCategory.enqueue(object: Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                val categories = response.body()!!.category
                setCategory(categories)
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.e("WirteActivity", "getCategory()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun setCategory(categories: List<Category>) {
        spinner = findViewById(R.id.spin_category)
        val names = Array(categories.size + 1) {""}
        for (index in categories.indices) {
            names[index] = categories[index].name
        }
        names[categories.size] = getString(R.string.category)

        val adapter = CategoryAdapter(this, R.layout.item_category, names)
        spinner.adapter = adapter
        spinner.setSelection(adapter.count)
    }

    private fun initContentHint() {
        val user = Util.readUser(this)
        val dongmyeon = user!!.location.dongmyeon
        val content = findViewById<EditText>(R.id.et_content)
        content.hint = getString(R.string.content_hint, dongmyeon)
    }

    private fun initAddImage() {
        tvImageCount = findViewById(R.id.tv_image_count)

        val addImageView = findViewById<ConstraintLayout>(R.id.cl_add_image)
        addImageView.setOnClickListener {
            addImage()
        }

        rvImage = findViewById(R.id.rv_image)
        imageAdapter = ImageAdapter(this, mutableListOf())
        rvImage.adapter = imageAdapter
    }

    private fun addImage() {
        ImagePicker.create(this)
            .start()
    }
    
    // 추가된 사진 thumbnail 추가
    private fun addImageThumbnail() {
        for (i in images.indices) {
            imageAdapter.addItem(images[i])
        }
    }

    private fun writeSale() {
        postContent()
    }
    
    // 게시글 업로드
    private fun postContent() {
        val title = findViewById<EditText>(R.id.et_title).text.toString()
        val content = findViewById<EditText>(R.id.et_content).text.toString()
        val price = findViewById<EditText>(R.id.et_price).text.toString().toInt()
        val writer = Util.readUser(this)!!.id
        val categoryId = spinner.selectedItemPosition
        val callPost = writeService.writeSale(token, title, content, price, 0, writer, categoryId)
        callPost.enqueue(object: Callback<WriteSaleResponse> {
            override fun onResponse(call: Call<WriteSaleResponse>, response: Response<WriteSaleResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    if (images.size > 0) {
                        postImage(response.body()!!.id)
                    } else {
                        showToast()
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<WriteSaleResponse>, t: Throwable) {
                Log.e("WirteActivity", "writeSale()-[onFailure] 실패 : $t")
            }
        })
    }
    
    // 첨부 이미지 업로드
    private fun postImage(saleId: Int) {
        val name = RequestBody.create(MediaType.parse("text/plain"), "img")
        var part = mutableListOf<MultipartBody.Part>()
        for (i in images.indices) {
            part.add(i, prepareFilePart("img", Uri.parse(images[i].path)))
        }
        val callImage = writeService.writeSaleImage(token, saleId, part, name)
        callImage.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    showToast()
                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("WriteActivity", "postImage()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun prepareFilePart(partName: String, fileUri: Uri) : MultipartBody.Part {
        Log.d("prepareFilePart()", fileUri.toString())
        val file = File(fileUri.path)
        val requestBody = RequestBody.create(MediaType.parse("image/*"), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun showToast() {
        Toast.makeText(this, "거래글 올리기 성공", Toast.LENGTH_SHORT).show()
    }

    class CategoryAdapter(context: Context, layoutResource: Int, categories: Array<String>) : ArrayAdapter<String>(context, layoutResource, categories) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            if (position == count) {
                view.findViewById<TextView>(android.R.id.text1).text = ""
                view.findViewById<TextView>(android.R.id.text1).hint = getItem(count)
            }

            return view
        }

        override fun getCount(): Int = super.getCount() - 1
    }

    class ImageAdapter(private val context: Context, private val dataSet: MutableList<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            return ViewHolder(view).listen { position, type ->
                removeImage(position)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                    .load(dataSet[position].uri)
                    .into(holder.image)
        }

        override fun getItemCount(): Int = dataSet.size

        fun addItem(item: Image) {
            dataSet.add(item)
            (context as WriteActivity).tvImageCount.text = dataSet.size.toString()
            context.tvImageCount.setTextColor(context.getColor(R.color.greenery))
            notifyDataSetChanged()
        }

        private fun removeImage(position: Int) {
            dataSet.removeAt(position)
            if (dataSet.size > 0) {
                (context as WriteActivity).tvImageCount.text = dataSet.size.toString()
                context.tvImageCount.setTextColor(context.getColor(R.color.greenery))
            } else {
                (context as WriteActivity).tvImageCount.text = "0"
                context.tvImageCount.setTextColor(context.getColor(R.color.gray))
            }
            notifyDataSetChanged()
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.iv_image)
        }

        private fun <T: RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {
            itemView.setOnClickListener {
                event.invoke(adapterPosition, itemViewType)
            }
            return this
        }
    }
}