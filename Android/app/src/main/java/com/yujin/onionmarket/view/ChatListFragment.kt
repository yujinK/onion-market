package com.yujin.onionmarket.view

import android.content.Context
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.*
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

    private lateinit var user: User

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        user = Util.readUser(requireContext())!!
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

        adapter = ChatAdapter(requireContext(), user.id)
        rvChat.adapter = adapter
    }

    private fun initChat() {
        val token = Util.readToken(requireContext())
        val callChat = chatService.loadUserChat(token, user.id)
        callChat.enqueue(object: Callback<UserChatsResponse> {
            override fun onResponse(call: Call<UserChatsResponse>, response: Response<UserChatsResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    val chats = response.body()!!.chats
                    setChat(chats)
                }
            }

            override fun onFailure(call: Call<UserChatsResponse>, t: Throwable) {
                Log.e(TAG, "setChat()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun setChat(chats: ArrayList<UserChat>) {
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

    inner class ChatAdapter(private val context: Context, private val myId: Int) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
        private val chatList: ArrayList<UserChat> = arrayListOf()

        fun addChat(chat: UserChat) {
            chatList.add(chat)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            var location: String
            if (myId == chatList[position].buyUser.id) { // 내가 구매자
                if (chatList[position].sale.user.img.isNullOrEmpty()) {
                    holder.profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
                } else {
                    Glide.with(context)
                            .load(chatList[position].sale.user.img)
                            .into(holder.profile)
                }

                holder.nick.text = chatList[position].sale.user.nick
                location = context.getString(R.string.str_location, chatList[position].sale.user.location.dongmyeon, chatList[position].sale.user.location.li)
            } else {    // 내가 판매자
                if (chatList[position].buyUser.img.isNullOrEmpty()) {
                    holder.profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
                } else {
                    Glide.with(context)
                            .load(chatList[position].buyUser.img)
                            .into(holder.profile)
                }

                holder.nick.text = chatList[position].buyUser.nick
                location = context.getString(R.string.str_location, chatList[position].buyUser.location.dongmyeon, chatList[position].buyUser.location.li)
            }

            holder.lastMessage.text = chatList[position].lastMessage

            val date = Util.getSaleChatDiff(chatList[position].updatedAt)
            holder.locationDate.text = context.getString(R.string.str_dot_str, location, date)

            if (chatList[position].sale.images.size == 0) {
                holder.saleImage.visibility = View.GONE
            } else {
                Glide.with(context)
                        .load(context.getString(R.string.img_url) + chatList[position].sale.images[0].path)
                        .into(holder.saleImage)
            }

            holder.itemView.setOnClickListener {
                val cSale = chatList[position].sale
                val sale = Sale(cSale.id, cSale.title, cSale.content, cSale.price, cSale.priceProposal, cSale.chatCount, cSale.favoriteCount, cSale.viewCount, cSale.createdAt, Category(-1, ""), cSale.user, cSale.images)
                val chat = Chat(chatList[position].id, chatList[position].lastMessage, chatList[position].createdAt, chatList[position].updatedAt, chatList[position].buyUser, chatList[position].sale.id)
                startChat(sale, chat)
            }
        }

        override fun getItemCount(): Int = chatList.size

        private fun startChat(sale: Sale, chat: Chat) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("sale", sale)
            intent.putExtra("chat", chat)
            startActivity(intent)
        }

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val profile: ImageView = view.findViewById(R.id.iv_profile)
            val nick: TextView = view.findViewById(R.id.tv_nick)
            val locationDate: TextView = view.findViewById(R.id.tv_location_date)
            val lastMessage: TextView = view.findViewById(R.id.tv_last_message)
            val saleImage: ImageView = view.findViewById(R.id.iv_image)
        }
    }
}