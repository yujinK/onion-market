package com.yujin.onionmarket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.data.Sale
import java.text.NumberFormat
import java.util.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()
    }

    private fun init() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "사쿠란보"

        initSale()
    }

    private fun initSale() {
        val sale = intent.getParcelableExtra<Sale>("sale")!!

        val thumbnail = findViewById<ImageView>(R.id.iv_thumbnail)
        val url = getString(R.string.img_url) + sale.images[0].path
        Glide.with(this)
            .load(url)
            .into(thumbnail)

        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale.title

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale.price))
    }
}