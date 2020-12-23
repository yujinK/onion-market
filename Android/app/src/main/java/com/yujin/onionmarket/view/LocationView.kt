package com.yujin.onionmarket.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.yujin.onionmarket.R

class LocationView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var myLocation: TextView
    private var arrow: ImageView
    var isOpen = false
    private var openAnimation: Animation
    private var closeAnimation: Animation

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_location, this, true)

        myLocation = view.findViewById(R.id.tv_my_location)
        arrow = view.findViewById(R.id.iv_arrow)

        openAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_open_location)
        closeAnimation = AnimationUtils.loadAnimation(context, R.anim.anim_close_location)
    }

    fun setLocation(location: String) {
        myLocation.text = location
        invalidate()
        requestLayout()
    }

    fun setOpen() {
        openAnimation()
        isOpen = true
        invalidate()
        requestLayout()
    }

    fun setClose() {
        closeAnimation()
        isOpen = false
        invalidate()
        requestLayout()
    }

    private fun openAnimation() {
        arrow.startAnimation(openAnimation)
        openAnimation.fillAfter = true
        arrow.setImageDrawable(resources.getDrawable(R.drawable.ic_down, null))
        Toast.makeText(context, "open", Toast.LENGTH_SHORT).show()
    }

    private fun closeAnimation() {
        arrow.startAnimation(closeAnimation)
        closeAnimation.fillAfter = true
        arrow.setImageDrawable(resources.getDrawable(R.drawable.ic_up, null))
        Toast.makeText(context, "close", Toast.LENGTH_SHORT).show()
    }
}