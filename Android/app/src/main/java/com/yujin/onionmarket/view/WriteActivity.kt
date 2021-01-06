package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.CategoryResponse
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class WriteActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var categoryService: RetrofitService

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
        categoryService = retrofit.create(RetrofitService::class.java)
    }

    private fun initToolbar() {
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initCategory() {
        val callCategory = categoryService.requestCategory()
        callCategory.enqueue(object: Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {

            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {

            }
        })
    }

    private fun initContentHint() {
        val user = Util.readUser(this)
        val dongmyeon = user!!.location[0].dongmyeon
        val content = findViewById<EditText>(R.id.et_content)
        content.hint = getString(R.string.content_hint, dongmyeon)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_write_toolbar, menu)
        return true
    }
}