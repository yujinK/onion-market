package com.yujin.onionmarket.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.yujin.onionmarket.R

class ReactionView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var src: ImageView
    private var count: TextView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.view_reaction, this, true)

        src = view.findViewById(R.id.iv_icon)
        count = view.findViewById(R.id.tv_count)
    }

    fun setIconSrc(type: Int) {
        when (type) {
            TYPE_CHAT -> src.setImageDrawable(resources.getDrawable(R.drawable.ic_reaction_chat, null))
            TYPE_FAVORITE -> src.setImageDrawable(resources.getDrawable(R.drawable.ic_reaction_favorite, null))
        }
        invalidate()
        requestLayout()
    }

    fun setCountNum(countNum: Int) {
        count.text = countNum.toString()
        invalidate()
        requestLayout()
    }

    companion object {
        val TYPE_CHAT = 0
        val TYPE_FAVORITE = 1
    }
}