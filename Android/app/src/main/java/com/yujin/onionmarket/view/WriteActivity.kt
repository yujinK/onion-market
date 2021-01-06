package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Category
import com.yujin.onionmarket.data.CategoryResponse
import com.yujin.onionmarket.data.EmptyResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var writeService: RetrofitService

    private lateinit var spinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write)
        init()
    }

    private fun init() {
        initRetrofit()
        initToolbar()
        initCategory()
        initContentHint()
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
        val token = Util.readToken(this)
        val callCategory = writeService.requestCategory(token)
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
        spinner = findViewById<Spinner>(R.id.spin_category)
        val names = Array(categories.size + 1) {""}
        for (index in categories.indices) {
            names[index] = categories[index].name
        }
        names[categories.size] = getString(R.string.category)

//        val adapter = CategoryAdapter(this, R.layout.item_category, names)
//        val adapter = CategoryAdapter(this, android.R.layout.simple_spinner_dropdown_item, names)
        val adapter = ArrayAdapter(this, R.layout.item_category, names)
        spinner.adapter = adapter
    }

    private fun initContentHint() {
        val user = Util.readUser(this)
        val dongmyeon = user!!.location[0].dongmyeon
        val content = findViewById<EditText>(R.id.et_content)
        content.hint = getString(R.string.content_hint, dongmyeon)
    }

    private fun writeSale() {
        val token = Util.readToken(this)
        val title = findViewById<EditText>(R.id.et_title).text.toString()
        val content = findViewById<EditText>(R.id.et_content).text.toString()
        val price = findViewById<EditText>(R.id.et_price).text.toString().toInt()
        val writer = Util.readUser(this)!!.id
        val categoryId = spinner.selectedItemPosition
        val callPost = writeService.requestWriteSale(token, title, content, price, 0, writer, categoryId)
        callPost.enqueue(object: Callback<EmptyResponse> {
            override fun onResponse(call: Call<EmptyResponse>, response: Response<EmptyResponse>) {
                showToast()
                finish()
            }

            override fun onFailure(call: Call<EmptyResponse>, t: Throwable) {
                Log.e("WirteActivity", "writeSale()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun showToast() {
        Toast.makeText(this, "거래글 올리기 성공", Toast.LENGTH_SHORT).show()
    }

//    class CategoryAdapter(context: Context, layoutResource: Int, categories: Array<String>) : ArrayAdapter<String>(context, layoutResource, categories) {
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//            val view = super.getView(position, convertView, parent)
//            if (position == count) {
//                view.findViewById<TextView>(android.R.id.text1).text = ""
//                view.findViewById<TextView>(android.R.id.text1).hint = getItem(count)
//            }
//
//            return view
//        }
//
//        override fun getCount(): Int = super.getCount() - 1
//    }
}