package com.yujin.onionmarket.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Chat

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val chat = view?.findViewById<RecyclerView>(R.id.rv_chat)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(chat?.context, layoutManager.orientation)
        val chatSet = ArrayList<Chat>()
        chat?.layoutManager = layoutManager
        chat?.addItemDecoration(dividerItemDecoration)
        val adapter = ChatAdapter(requireContext(), chatSet)
        chat?.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    class ChatAdapter(private val context: Context, private val chatSet: ArrayList<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//            // 프로필 사진
//            if (chatSet[position].profile.isNullOrEmpty()) {
//                holder.profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
//            } else {
//                Glide.with(context)
//                        .load(chatSet[position].profile)
//                        .into(holder.profile)
//            }
//
//            // 닉네임
//            holder.nick.text = chatSet[position].nick
//
//            // info
//            // TODO: Chat 시간 경과 다시 계산 (초, 분, 시간 전, 어제, 이후 날짜)
//            holder.info.text = context.getString(R.string.receive_info, chatSet[position].location, "어제")
//
//            // 최근 메시지
//            holder.lastMessage.text = chatSet[position].message
        }

        override fun getItemCount(): Int = chatSet.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val profile: ImageView = view.findViewById(R.id.iv_profile)
            val nick: TextView = view.findViewById(R.id.tv_nick)
            val info: TextView = view.findViewById(R.id.tv_info)
            val lastMessage: TextView = view.findViewById(R.id.tv_last_message)
        }
    }
}