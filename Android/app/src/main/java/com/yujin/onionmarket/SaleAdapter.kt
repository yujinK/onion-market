package com.yujin.onionmarket

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import com.yujin.onionmarket.view.DetailSaleActivity
import com.yujin.onionmarket.view.ReactionView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

// state: 0(HomeFragment), 1(MySaleFragment)
class SaleAdapter(private val context: Context, private val dataSet: ArrayList<Sale>, private val state: Int) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    private lateinit var retrofit: Retrofit
    private lateinit var manageService: RetrofitService

    private lateinit var manageSheet: BottomSheetDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        initRetrofit()
        return ViewHolder(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        manageService = retrofit.create(RetrofitService::class.java)
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
        holder.more.setOnClickListener {
            showManage(position)
        }

        holder.container.setOnClickListener {
            moveDetail()
        }
    }

    override fun getItemCount(): Int = dataSet.size

    private fun showManage(position: Int) {
        manageSheet = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_manage_sale, null, false)
        manageSheet.setContentView(view)
        manageSheet.show()
        manageSheet.findViewById<TextView>(R.id.tv_delete)?.setOnClickListener { alertDelete(position) }
    }

    private fun moveDetail() {
        val intent = Intent(context, DetailSaleActivity::class.java)
        context.startActivity(intent)
    }

    private fun alertDelete(position: Int) {
        MaterialAlertDialogBuilder(context)
                .setMessage(context.getString(R.string.delete_message))
                .setPositiveButton(context.getString(R.string.delete)) { _, _ ->
                    deleteSale(position)
                }
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->

                }
                .show()
    }

    private fun deleteSale(position: Int) {
        val token = Util.readToken(context)
        val callDelete = manageService.deleteSale(token, dataSet[position].id)
        callDelete.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    manageSheet.dismiss()
                    removeAt(position)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ManageSaleSheet", "delete(): $t")
            }
        })
    }

    private fun removeAt(position: Int) {
        dataSet.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: ConstraintLayout = view.findViewById(R.id.ll_sale)
        val thumbnail: ImageView = view.findViewById(R.id.iv_thumbnail)
        val title: TextView = view.findViewById(R.id.tv_title)
        val chat: ReactionView = view.findViewById(R.id.v_chat)
        val favorite: ReactionView = view.findViewById(R.id.v_favorite)
        val more: ImageButton = view.findViewById(R.id.ib_more)
    }
}