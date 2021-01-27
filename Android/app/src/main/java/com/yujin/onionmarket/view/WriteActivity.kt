package com.yujin.onionmarket.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.yujin.onionmarket.data.*
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
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class WriteActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var writeService: RetrofitService

    private lateinit var token: String

    private lateinit var spinner: Spinner
    private lateinit var rvImage: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var tvImageCount: TextView

    private var isProposal: Boolean = false
    private var editSale: Sale? = null

    private var pickerImages = mutableListOf<Image>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            pickerImages = ImagePicker.getImages(data)
            addImageThumbnail()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun init() {
        token = Util.readToken(this)
        editSale = intent.getParcelableExtra("sale")
        initRetrofit()
        initToolbar()
        initCategory(editSale?.category?.id)
        initPrice()
        initProposal()
        initContentHint()
        initAddImage()

        if (editSale != null) {
            setEdit()
        }
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
                    postContent()
                    true
                }
                else -> { super.onOptionsItemSelected(it) }
            }
        }
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initCategory(categoryId: Int?) {
        getCategory(categoryId)
    }

    private fun getCategory(categoryId: Int?) {
        val callCategory = writeService.getCategory(token)
        callCategory.enqueue(object: Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                val categories = response.body()!!.category
                setCategory(categories)

                // edit 모드일 경우
                if (categoryId != null) {
                    spinner.setSelection(categoryId)
                }
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

    private fun initPrice() {
        val won = findViewById<TextView>(R.id.tv_won)
        val price = findViewById<EditText>(R.id.et_price)
        price.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    won.setTextColor(getColor(R.color.divider_gray))
                } else {
                    won.setTextColor(getColor(R.color.black))
                }
            }
        })

        // focus in: comma(x), focus out: comma(O)
        price.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val commaPrice = price.text.toString().replace(",", "")
                price.text = commaPrice.toEditable()
            } else {
                if (price.text.toString().isNotEmpty()) {
                    val intPrice = price.text.toString().toInt()
                    price.text = NumberFormat.getNumberInstance(Locale.KOREA).format(intPrice).toEditable()
                }
            }
        }
    }

    private fun initProposal() {
        val proposal = findViewById<LinearLayout>(R.id.ll_proposal)
        proposal.setOnClickListener {
            isProposal = !isProposal
            setProposal(isProposal)
        }
    }

    private fun setProposal(state: Boolean) {
        val ivProposal = findViewById<ImageView>(R.id.iv_proposal)
        val tvProposal = findViewById<TextView>(R.id.tv_proposal)
        if (state) {
            // 가격제안 받기
            ivProposal.isSelected = true
            tvProposal.setTextColor(getColor(R.color.black))
        } else {
            ivProposal.isSelected = false
            tvProposal.setTextColor(getColor(R.color.divider_gray))
        }
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
        imageAdapter = ImageAdapter(this, arrayListOf())
        rvImage.adapter = imageAdapter
    }

    private fun addImage() {
        ImagePicker.create(this).start()
    }
    
    // 추가된 사진 thumbnail 추가
    private fun addImageThumbnail() {
        for (i in pickerImages.indices) {
            imageAdapter.addItem(pickerImages[i])
        }
    }

    // 수정모드
    private fun setEdit() {
        val title = findViewById<EditText>(R.id.et_title)
        title.text = editSale!!.title.toEditable()
        
        val price = findViewById<EditText>(R.id.et_price)
        price.text = editSale!!.price.toString().toEditable()
        val won = findViewById<TextView>(R.id.tv_won)
        won.setTextColor(getColor(R.color.black))

        val ivProposal = findViewById<ImageView>(R.id.iv_proposal)
        val tvProposal = findViewById<TextView>(R.id.tv_proposal)
        if (editSale!!.priceProposal) {
            ivProposal.isSelected = true
            tvProposal.setTextColor(getColor(R.color.black))
        } else {
            ivProposal.isSelected = false
            tvProposal.setTextColor(getColor(R.color.divider_gray))
        }

        val content = findViewById<EditText>(R.id.et_content)
        content.text = editSale!!.content.toEditable()

        //TODO: image
        val editImages = editSale!!.images
        for (saleImage in editImages) {
            pickerImages.add(Image(-1, saleImage.path, saleImage.path))
        }
        addImageThumbnail()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    // 게시글 업로드
    private fun postContent() {
        val title = findViewById<EditText>(R.id.et_title).text.toString()
        val content = findViewById<EditText>(R.id.et_content).text.toString()
        val price = findViewById<EditText>(R.id.et_price).text.toString().replace(",", "").toInt()
        val writer = Util.readUser(this)!!.id
        val categoryId = spinner.selectedItemPosition
        var proposal = if (isProposal) { 1 } else { 0 }

        // 게시글 등록
        if (editSale == null) {
            val callPost = writeService.writeSale(token, title, content, price, proposal, writer, categoryId)
            callPost.enqueue(object : Callback<WriteSaleResponse> {
                override fun onResponse(call: Call<WriteSaleResponse>, response: Response<WriteSaleResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                        if (pickerImages.size > 0) {
                            uploadImage(response.body()!!.id)
                        } else {
                            showToast()
                            setResult(RESULT_OK)
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<WriteSaleResponse>, t: Throwable) {
                    Log.e("WirteActivity", "writeSale()-[onFailure] 등록 실패 : $t")
                }
            })
        } else {
            // 게시글 수정
            val callEdit = writeService.editSale(token, editSale!!.id, title, content, price, proposal, categoryId)
            callEdit.enqueue(object: Callback<WriteSaleResponse> {
                override fun onResponse(call: Call<WriteSaleResponse>, response: Response<WriteSaleResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                        if (pickerImages.size > 0) {
                            uploadImage(editSale!!.id)
                        } else {
                            showToast()
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<WriteSaleResponse>, t: Throwable) {
                    Log.e("WirteActivity", "writeSale()-[onFailure] 수정 실패 : $t")
                }
            })
        }
    }
    
    // 첨부 이미지 업로드
    private fun uploadImage(saleId: Int) {
        var part = mutableListOf<MultipartBody.Part>()
        val images = imageAdapter.getItems()
        for (i in images.indices) {
            // 새로 추가된 이미지
            if (images[i].id != -1L) {
                part.add(i, prepareFilePart("img", Uri.parse(images[i].path)))
            }
        }

        // 새로 추가된 사진이 있는 경우
        if (part.size > 0) {
            val callImage = writeService.uploadImage(token, part)
            callImage.enqueue(object : Callback<ImageUploadResponse> {
                override fun onResponse(call: Call<ImageUploadResponse>, response: Response<ImageUploadResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                        val pImages = arrayListOf<String>()
                        var index = 0
                        for (i in images.indices) {
                            if (images[i].id == -1L) {
                                pImages.add(images[i].path)
                            } else {
                                pImages.add(response.body()!!.uploadImage[index].filename)
                                index += 1
                            }
                        }

                        postImage(saleId, pImages)
                    }
                }

                override fun onFailure(call: Call<ImageUploadResponse>, t: Throwable) {
                    Log.e("WriteActivity", "uploadImage()-[onFailure] 실패 : $t")
                }
            })
        } else {
            // 추가된 사진이 없는 경우
            val pImages = arrayListOf<String>()
            for (i in images.indices) {
                pImages.add(images[i].path)
            }
            postImage(saleId, pImages)
        }
    }

    // 첨부 이미지 DB 업로드
    private fun postImage(saleId: Int, pImages: ArrayList<String>) {
        val callImage = writeService.writeImage(token, saleId, pImages.size, pImages)
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

    class ImageAdapter(private val context: Context, private val dataSet: ArrayList<Image>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
        private lateinit var retrofit: Retrofit
        private lateinit var deleteService: RetrofitService

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
            retrofit = RetrofitClient.getInstance()
            deleteService = retrofit.create(RetrofitService::class.java)
            return ViewHolder(view).listen { position, _ ->
                removeImage(position)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (dataSet[position].id == -1L) {
                Glide.with(context)
                        .load(context.getString(R.string.img_url) + dataSet[position].path)
                        .into(holder.image)
            } else {
                Glide.with(context)
                        .load(dataSet[position].uri)
                        .into(holder.image)
            }
        }

        override fun getItemCount(): Int = dataSet.size

        fun getItems() = dataSet

        fun addItem(item: Image) {
            dataSet.add(item)
            (context as WriteActivity).tvImageCount.text = dataSet.size.toString()
            context.tvImageCount.setTextColor(context.getColor(R.color.greenery))
            notifyDataSetChanged()
        }

        fun getNewPriority() : Int {
            for (index in dataSet.indices) {
                if (dataSet[index].id != -1L) {
                    return index
                }
            }
            return -1
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