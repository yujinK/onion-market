package com.yujin.onionmarket.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.*
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.IllegalArgumentException
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var chatService: RetrofitService

    private lateinit var adapter: MessageAdapter
    private lateinit var socket: Socket

    private lateinit var token: String
    private lateinit var sale: Sale
    private var chatId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        token = Util.readToken(this)
        init()
    }

    private fun init() {
        initSocket()
        initRetrofit()
        initToolbar()
        initSendMessage()
        initSale()
        initMessages()
        initChat()
    }

    private fun initSocket() {
        socket = IO.socket(getString(R.string.socket_io))
        socket.connect()
        socket.on("chat", onMessageReceived)
    }

    private val onMessageReceived = Emitter.Listener { args ->
        val receiveData = args[0]

    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(RetrofitService::class.java)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "사쿠란보"
        toolbar.setNavigationOnClickListener { finishChat() }
    }

    override fun onBackPressed() {
        finishChat()
    }

    private fun finishChat() {
        socket.close()
        finish()
    }

    private fun initSendMessage() {
        val btnSend = findViewById<ImageButton>(R.id.ib_send)
        btnSend.setOnClickListener { sendMessage() }
        val inputMessage = findViewById<EditText>(R.id.et_chat)
        inputMessage.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isEmpty()) {
                    btnSend.setColorFilter(getColor(R.color.btn_send_gray))
                } else {
                    btnSend.setColorFilter(getColor(R.color.greenery))
                }
            }
        })
    }

    private fun initSale() {
        sale = intent.getParcelableExtra("sale")!!

        val thumbnail = findViewById<ImageView>(R.id.iv_thumbnail)
        val url = getString(R.string.img_url) + sale.images[0].path
        Glide.with(this)
            .load(url)
            .into(thumbnail)

        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale.title

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale.price))
    }

    private fun initChat() {
        checkChat()
    }

    private fun initMessages() {
        val rvMessage = findViewById<RecyclerView>(R.id.rv_message)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val myNick = Util.readUser(this)!!.nick
        adapter = MessageAdapter(this, myNick)
        rvMessage.layoutManager = layoutManager
        rvMessage.adapter = adapter
    }

    private fun checkChat() {
        val user = Util.readUser(this)!!
        val callChat = chatService.existingChat(token, sale.id, user.id)
        callChat.enqueue(object: Callback<ChatIdResponse> {
            override fun onResponse(
                call: Call<ChatIdResponse>,
                response: Response<ChatIdResponse>
            ) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    chatId = response.body()!!.chatId
                    if (chatId != -1) {
                        getMessages(chatId)
                    }
                }
            }

            override fun onFailure(call: Call<ChatIdResponse>, t: Throwable) {
                Log.e("ChatActivity", "checkChat()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun getMessages(chatId: Int) {
        val callChat = chatService.readChat(token, chatId)
        callChat.enqueue(object: Callback<ReadChatResponse> {
            override fun onResponse(call: Call<ReadChatResponse>, response: Response<ReadChatResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    setMessages(response.body()!!.messages)
                }
            }

            override fun onFailure(call: Call<ReadChatResponse>, t: Throwable) {
                Log.e("ChatActivity", "getMessages()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun setMessages(messages: ArrayList<Message>) {
        adapter.setMessages(messages)
    }

    private fun sendMessage() {
        val user = Util.readUser(this)!!
        val input = findViewById<EditText>(R.id.et_chat)
        val message = input.text.toString()
        if (chatId == -1) {
            val callChat = chatService.newChat(token, message, user.id, sale.id)
            callChat.enqueue(object: Callback<ChatIdResponse> {
                override fun onResponse(call: Call<ChatIdResponse>, response: Response<ChatIdResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                        chatId = response.body()!!.chatId
                    }
                }

                override fun onFailure(call: Call<ChatIdResponse>, t: Throwable) {
                    Log.e("ChatActivity", "sendMessage()-[newChat onFailure] 실패 : $t")
                }
            })
        } else {
            val callChat = chatService.sendMessage(token, chatId, message, user.id)
            callChat.enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e("ChatActivity", "sendMessage()-[sendMessage onFailure] 실패 : $t")
                }
            })
        }
        input.text = null
    }

    class MessageAdapter(private val context: Context, private val myNick: String) : RecyclerView.Adapter<MessageAdapter.BaseViewHolder<*>>() {
        private var adapterMessageList: ArrayList<Message> = arrayListOf()

        companion object {
            private const val TYPE_SEND = 0
            private const val TYPE_RECEIVE = 1
        }

        fun addMessage(message: Message) {
            adapterMessageList.add(message)
            notifyDataSetChanged()
        }

        fun setMessages(messages: ArrayList<Message>) {
            adapterMessageList = messages
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
            return when (viewType) {
                TYPE_SEND -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.item_send_message, parent, false)
                    SendViewHolder(view)
                }
                TYPE_RECEIVE -> {
                    val view = LayoutInflater.from(context).inflate(R.layout.item_receive_message, parent, false)
                    ReceiveViewHolder(view)
                }
                else -> throw IllegalArgumentException("Invalid view type")
            }
        }

        override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
            val message = adapterMessageList[position]
            when (holder) {
                is SendViewHolder -> holder.bind(message)
                is ReceiveViewHolder -> holder.bind(message)
                else -> throw IllegalArgumentException()
            }
        }

        override fun getItemViewType(position: Int): Int {
            val nick = adapterMessageList[position].user.nick
            return if (myNick == nick) {
                TYPE_SEND
            } else {
                TYPE_RECEIVE
            }

            return TYPE_SEND
        }

        override fun getItemCount(): Int = adapterMessageList.size

        inner class SendViewHolder(itemView: View) : BaseViewHolder<Message>(itemView) {
            override fun bind(item: Message) {
                val time = itemView.findViewById<TextView>(R.id.tv_time)
                time.text = item.createdAt
                val message = itemView.findViewById<TextView>(R.id.tv_message)
                message.text = item.message
            }
        }

        inner class ReceiveViewHolder(itemView: View) : BaseViewHolder<Message>(itemView) {
            override fun bind(item: Message) {
                val profile = itemView.findViewById<ImageView>(R.id.iv_profile)
                if (item.user.img.isNullOrEmpty()) {
                    profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
                } else {
                    Glide.with(context)
                            .load(item.user.img)
                            .into(profile)
                }

                val message = itemView.findViewById<TextView>(R.id.tv_message)
                message.text = item.message

                val time = itemView.findViewById<TextView>(R.id.tv_time)
                time.text = item.createdAt
            }
        }

        abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
            abstract fun bind(item: T)
        }
    }
}