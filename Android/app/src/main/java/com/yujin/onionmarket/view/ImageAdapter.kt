package com.yujin.onionmarket.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.data.Image

class ImageAdapter(
        context: Context,
        itemList: ArrayList<Image>,
        isInfinite: Boolean
) : LoopingPagerAdapter<Image>(context, itemList, isInfinite) {
    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutInflater.from(context).inflate(R.layout.item_image_pager, container, false)
    }

    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        val image = convertView.findViewById<ImageView>(R.id.iv_image)
        val url = context.getString(R.string.img_url) + itemList?.get(listPosition)?.path
        Glide.with(context)
                .load(url)
                .into(image)
    }
}