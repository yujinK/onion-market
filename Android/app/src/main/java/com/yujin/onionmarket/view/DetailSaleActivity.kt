package com.yujin.onionmarket.view

import android.app.Activity
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.asksira.loopingviewpager.LoopingViewPager
import com.asksira.loopingviewpager.indicator.CustomShapePagerIndicator
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.data.Image
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.data.User

class DetailSaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sale)
        init()
    }

    private fun init() {
        makeStatusBarTransparent()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_detail)) { _, insets ->
            findViewById<Toolbar>(R.id.toolbar).setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }

        setView()
    }

    private fun setView() {
        val sale = intent.getParcelableExtra<Sale>("sale")

        setUser(sale?.user)
        setImages(sale?.images)
        setGoods(sale)
    }

    private fun setUser(user: User?) {
        val profile = findViewById<ImageView>(R.id.iv_profile)
        if (user!!.img.isNullOrEmpty()) {
            profile.setImageDrawable(getDrawable(R.drawable.ic_profile))
        } else {
            Glide.with(this)
                    .load(user?.img)
                    .into(profile)
        }

        val nick = findViewById<TextView>(R.id.tv_nick)
        nick.text = user.nick

        val location = findViewById<TextView>(R.id.tv_location)
        location.text = "${user.location.sigun} ${user.location.dongmyeon} ${user.location.li}"
    }

    private fun setImages(images: ArrayList<Image>?) {
        if (!images.isNullOrEmpty()) {
            val adapter = ImageAdapter(this, images, false)
            val loopingViewPager = findViewById<LoopingViewPager>(R.id.pager_image)
            loopingViewPager.adapter = adapter

            val indicator = findViewById<CustomShapePagerIndicator>(R.id.indicator)
            indicator.highlighterViewDelegate = {
                val highlighter = View(this)
                highlighter.layoutParams = FrameLayout.LayoutParams(16, 2)
                highlighter.setBackgroundColor(getColor(R.color.white))
                highlighter
            }
            indicator.unselectedViewDelegate = {
                val unselected = View(this)
                unselected.layoutParams = LinearLayout.LayoutParams(16, 2)
                unselected.setBackgroundColor(getColor(R.color.white))
                unselected.alpha = 0.4f
                unselected
            }
        }
    }

    private fun setGoods(sale: Sale?) {
        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale?.title

        val categoryAndDate = findViewById<TextView>(R.id.tv_category_and_date)
        val calDate = "10분 전"   // TODO: 시간 계산 함수 개발
        categoryAndDate.text = getString(R.string.category_and_date, sale?.category?.name, calDate)

        val content = findViewById<TextView>(R.id.tv_content)
        content.text = sale?.content

        val chatFavView = findViewById<TextView>(R.id.tv_chat_fav_view)
        chatFavView.text = getString(R.string.chat_fav_view, sale?.chatCount, sale?.favoriteCount, sale?.viewCount)

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = sale?.price.toString() // TODO: 가격 콤마 처리

        val proposal = findViewById<TextView>(R.id.tv_proposal)
        if (sale?.priceProposal == true) {
            proposal.text = getString(R.string.ok_proposal)
        } else {
            proposal.text = getString(R.string.no_proposal)
        }
    }

    private fun Activity.makeStatusBarTransparent() {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }
}