package com.yujin.onionmarket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yujin.onionmarket.view.ReactionView

class SaleAdapter(private val dataSet: Array<Sale>) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = dataSet[position].title
        holder.chat.setIconSrc(ReactionView.TYPE_CHAT)
        holder.chat.setCountNum(10)
        holder.favorite.setIconSrc(ReactionView.TYPE_FAVORITE)
        holder.favorite.setCountNum(20)
    }

    override fun getItemCount(): Int = dataSet.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val chat: ReactionView
        val favorite: ReactionView

        init {
            title = view.findViewById(R.id.tv_title)
            chat = view.findViewById(R.id.v_chat)
            favorite = view.findViewById(R.id.v_favorite)
        }
    }
}