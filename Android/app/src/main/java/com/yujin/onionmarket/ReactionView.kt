package com.yujin.onionmarket

import android.content.Context
import android.util.AttributeSet
import android.view.View

class ReactionView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var src: Int
    private var count: Int

    init {
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ReactionView,
                0, 0).apply {
            try {
                src = getInteger(R.styleable.ReactionView_iconSrc, 0)
                count = getInteger(R.styleable.ReactionView_countNum, 0)
            } finally {
                recycle()
            }
        }
    }

    fun setIconSrc(iconSrc: Int) {
        src = iconSrc
        invalidate()
        requestLayout()
    }

    fun setCountNum(countNum: Int) {
        count = countNum
        invalidate()
        requestLayout()
    }
}