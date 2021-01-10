package com.yujin.onionmarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.view.ReactionView

class SaleAdapter(private val dataSet: List<Sale>) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = dataSet[position]
        holder.title.text = sale.title
        //TODO: 대표 사진 썸네일
        
        //채팅 아이콘 & 채팅수
        holder.chat.setIconSrc(ReactionView.TYPE_CHAT)
        holder.chat.setCountNum(sale.chatCount)
        
        //관심 아이콘 & 관심수
        holder.favorite.setIconSrc(ReactionView.TYPE_FAVORITE)
        holder.favorite.setCountNum(sale.favoriteCount)
    }

    override fun getItemCount(): Int = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_title)
        val chat: ReactionView = view.findViewById(R.id.v_chat)
        val favorite: ReactionView = view.findViewById(R.id.v_favorite)
    }
}