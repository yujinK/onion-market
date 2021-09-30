package com.yujin.onionmarket.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.data.Image

class ImageAdapter(
        itemList: ArrayList<Image>,
        isInfinite: Boolean
) : LoopingPagerAdapter<Image>(itemList, isInfinite) {
    override fun inflateView(viewType: Int, container: ViewGroup, listPosition: Int): View {
        return LayoutInflater.from(container.context).inflate(R.layout.item_image_pager, container, false)
    }

    override fun bindView(convertView: View, listPosition: Int, viewType: Int) {
        val image = convertView.findViewById<ImageView>(R.id.iv_image)
        val url = convertView.context.getString(R.string.img_url) + itemList?.get(listPosition)?.path
        Glide.with(convertView.context)
                .load(url)
                .into(image)
    }
}