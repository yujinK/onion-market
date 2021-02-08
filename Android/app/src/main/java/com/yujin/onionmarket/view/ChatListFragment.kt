package com.yujin.onionmarket.view

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Chat
import com.yujin.onionmarket.data.ChatsResponse
import com.yujin.onionmarket.network.ChatService
import com.yujin.onionmarket.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    private val TAG = "ChatListFragment"

    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService

    private lateinit var rvChat: RecyclerView
    private lateinit var adapter: ChatAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRetrofit()
        initRecyclerView()
        initChat()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
    }

    private fun initRecyclerView() {
        rvChat = requireView().findViewById(R.id.rv_chat)
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        rvChat.layoutManager = layoutManager
        rvChat.addItemDecoration(dividerItemDecoration)

        adapter = ChatAdapter(requireContext())
        rvChat.adapter = adapter
    }

    private fun initChat() {
        val token = Util.readToken(requireContext())
        val user = Util.readUser(requireContext())!!
        val callChat = chatService.loadUserChat(token, user.id)
        callChat.enqueue(object: Callback<ChatsResponse> {
            override fun onResponse(call: Call<ChatsResponse>, response: Response<ChatsResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    val chats = response.body()!!.chats
                    setChat(chats)
                }
            }

            override fun onFailure(call: Call<ChatsResponse>, t: Throwable) {
                Log.e(TAG, "setChat()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun setChat(chats: ArrayList<Chat>) {
        val noChat = requireView().findViewById<LinearLayout>(R.id.ll_no_chat)
        if (chats.size == 0) {
            noChat.visibility = View.VISIBLE
        } else {
            noChat.visibility = View.GONE

            for (chat in chats) {
                adapter.addChat(chat)
            }
            adapter.notifyDataSetChanged()
        }
    }

    class ChatAdapter(private val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
        private val chatList: ArrayList<Chat> = arrayListOf()

        fun addChat(chat: Chat) {
            chatList.add(chat)
        }

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

        override fun getItemCount(): Int = chatList.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val profile: ImageView = view.findViewById(R.id.iv_profile)
            val nick: TextView = view.findViewById(R.id.tv_nick)
            val info: TextView = view.findViewById(R.id.tv_info)
            val lastMessage: TextView = view.findViewById(R.id.tv_last_message)
        }
    }
}