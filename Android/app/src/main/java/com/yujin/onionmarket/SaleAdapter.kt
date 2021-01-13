package com.yujin.onionmarket

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.view.HomeFragment
import com.yujin.onionmarket.view.ManageSaleSheet
import com.yujin.onionmarket.view.ReactionView

// state: 0(HomeFragment), 1(MySaleFragment)
class SaleAdapter(private val context: Context, private val dataSet: List<Sale>, private val state: Int) : RecyclerView.Adapter<SaleAdapter.ViewHolder>(), View.OnClickListener {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = dataSet[position]
        holder.title.text = sale.title

        if (sale.images.isEmpty()) {
            holder.thumbnail.setImageDrawable(context.getDrawable(R.drawable.ic_mood))
        } else {
            Glide.with(holder.itemView.context)
                    .load(holder.itemView.context.getString(R.string.thumbnail_url) + sale.images[0].path)
                    .into(holder.thumbnail)
        }

        //채팅 아이콘 & 채팅수
        holder.chat.setIconSrc(ReactionView.TYPE_CHAT)
        holder.chat.setCountNum(sale.chatCount)
        
        //관심 아이콘 & 관심수
        holder.favorite.setIconSrc(ReactionView.TYPE_FAVORITE)
        holder.favorite.setCountNum(sale.favoriteCount)

        //게시글 관리 설정
        when(state) {
            0 -> holder.more.visibility = View.GONE
            1 -> holder.more.visibility = View.VISIBLE
        }

        holder.more.tag = position
        holder.more.setOnClickListener(this)
    }

    override fun getItemCount(): Int = dataSet.size

    private fun showManage(sale: Sale, position: Int) {
        val manageSheet = ManageSaleSheet(sale, position)
        val fm = (context as AppCompatActivity).supportFragmentManager
        manageSheet.show(fm, "showManage()")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnail: ImageView = view.findViewById(R.id.iv_thumbnail)
        val title: TextView = view.findViewById(R.id.tv_title)
        val chat: ReactionView = view.findViewById(R.id.v_chat)
        val favorite: ReactionView = view.findViewById(R.id.v_favorite)
        val more: ImageButton = view.findViewById(R.id.ib_more)
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.ib_more -> {
                val position = v!!.tag as Int
                showManage(dataSet[position], position)
            }
        }
    }
}