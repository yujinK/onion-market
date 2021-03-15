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
import com.yujin.onionmarket.network.SaleService
import com.yujin.onionmarket.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

// state: 0(HomeFragment), 1(MySaleFragment)
class SaleAdapter(private val context: Context, private val dataSet: ArrayList<Sale>, private val state: Int) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {
    private lateinit var retrofit: Retrofit
    private lateinit var saleService: SaleService

    private lateinit var manageSheet: BottomSheetDialog

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        initRetrofit()
        return ViewHolder(view)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        saleService = retrofit.create(SaleService::class.java)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sale = dataSet[position]
        holder.title.text = sale.title

        if (sale.images.isEmpty()) {
            holder.thumbnail.setImageDrawable(context.getDrawable(R.drawable.ic_mood))
        } else {
            Glide.with(holder.itemView.context)
                    .load(holder.itemView.context.getString(R.string.img_url) + sale.images[0].path)
                    .into(holder.thumbnail)
        }

        //지역 & 시간
        val location = "${dataSet[position].user.location.sigun} ${dataSet[position].user.location.dongmyeon} ${dataSet[position].user.location.li}"
        val timeDiff = Util.timeDifferentiation(dataSet[position].createdAt)
        holder.information.text = context.getString(R.string.str_dot_str, location, timeDiff)

        //가격
        holder.price.text = context.getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale?.price))

        //채팅 아이콘 & 채팅수
        if (sale.chatCount > 0) {
            holder.chat.setIconSrc(ReactionView.TYPE_CHAT)
            holder.chat.setCountNum(sale.chatCount)
        }
        
        //관심 아이콘 & 관심수
        if (sale.favoriteCount > 0) {
            holder.favorite.setIconSrc(ReactionView.TYPE_FAVORITE)
            holder.favorite.setCountNum(sale.favoriteCount)
        }

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
            moveDetail(position)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    private fun showManage(position: Int) {
        manageSheet = BottomSheetDialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_manage_sale, null, false)
        manageSheet.setContentView(view)
        manageSheet.show()
        manageSheet.findViewById<TextView>(R.id.tv_edit)?.setOnClickListener {
            manageSheet.dismiss()
            editSale(position)
        }
        manageSheet.findViewById<TextView>(R.id.tv_delete)?.setOnClickListener {
            manageSheet.dismiss()
            alertDelete(position)
        }
    }

    private fun moveDetail(position: Int) {
        val intent = Intent(context, DetailSaleActivity::class.java)
        intent.putExtra("sale", dataSet[position])
        context.startActivity(intent)
    }

    private fun editSale(position: Int) {
        val intent = Intent(context, WriteActivity::class.java)
        intent.putExtra("sale", dataSet[position])
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
        val callDelete = saleService.deleteSale(token, dataSet[position].id)
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
        val information: TextView = view.findViewById(R.id.tv_information)
        val price: TextView = view.findViewById(R.id.tv_price)
        val chat: ReactionView = view.findViewById(R.id.v_chat)
        val favorite: ReactionView = view.findViewById(R.id.v_favorite)
        val more: ImageButton = view.findViewById(R.id.ib_more)
    }
}