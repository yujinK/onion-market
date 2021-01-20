package com.yujin.onionmarket.view

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.asksira.loopingviewpager.LoopingViewPager
import com.asksira.loopingviewpager.indicator.CustomShapePagerIndicator
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Image
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.data.User
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailSaleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sale)
        init()
    }

    private fun init() {
        makeStatusBarTransparent()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_detail)) { _, insets ->
            toolbar.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
        toolbar.setNavigationOnClickListener { finish() }

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
        val loopingViewPager = findViewById<LoopingViewPager>(R.id.pager_image)
        val indicator = findViewById<CustomShapePagerIndicator>(R.id.indicator)
        if (!images.isNullOrEmpty()) {
            loopingViewPager.visibility = View.VISIBLE
            indicator.visibility = View.VISIBLE

            val adapter = ImageAdapter(this, images, false)
            loopingViewPager.adapter = adapter

            indicator.highlighterViewDelegate = {
                val highlighter = View(this)
                highlighter.layoutParams = FrameLayout.LayoutParams(16.dp(), 2.dp())
                highlighter.setBackgroundColor(getColorCompat(R.color.white))
                highlighter
            }
            indicator.unselectedViewDelegate = {
                val unselected = View(this)
                unselected.layoutParams = LinearLayout.LayoutParams(16.dp(), 2.dp())
                unselected.setBackgroundColor(getColorCompat(R.color.white))
                unselected.alpha = 0.4f
                unselected
            }
            loopingViewPager.onIndicatorProgress = { selectingPosition, progress -> indicator.onPageScrolled(selectingPosition, progress) }
            indicator.updateIndicatorCounts(loopingViewPager.indicatorCount)
        }
    }

    private fun setGoods(sale: Sale?) {
        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale?.title

        val categoryAndDate = findViewById<TextView>(R.id.tv_category_and_date)
        val calDate = Util.timeDifferentiation(sale?.createdAt)   // TODO: 시간 계산 함수 개발
        categoryAndDate.text = getString(R.string.str_dot_str, sale?.category?.name, calDate)

        val content = findViewById<TextView>(R.id.tv_content)
        content.text = sale?.content

        val chatFavView = findViewById<TextView>(R.id.tv_chat_fav_view)
        chatFavView.text = getString(R.string.chat_fav_view, sale?.chatCount, sale?.favoriteCount, sale?.viewCount)

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale?.price))

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

    private fun Int.dp(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    private fun Context.getColorCompat(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }
}