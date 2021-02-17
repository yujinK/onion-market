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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.gson.Gson
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.*
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.ChatService
import com.yujin.onionmarket.network.SaleService
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
    private lateinit var chatService: ChatService
    private lateinit var saleService: SaleService

    private lateinit var rvMessage: RecyclerView
    private lateinit var adapter: MessageAdapter
    private lateinit var socket: Socket

    private lateinit var etChat: EditText

    private lateinit var token: String
    private lateinit var sale: Sale
    private var chatId: Int = -1

    private val gson: Gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        initRetrofit()

        token = Util.readToken(this)
        setSale()
    }

    private fun setSale() {
        if (intent.hasExtra("sale")) {
            if (intent.hasExtra("chatId")) {
                chatId = intent.getIntExtra("chatId", -1)
            }
            sale = intent.getParcelableExtra("sale")!!
            init()
        } else {
            // 알림에서 넘어온 경우
            chatId = intent.getIntExtra("chatId", -1)
            val saleId = intent.getIntExtra("saleId", -1)
            getSale(saleId)
        }
    }

    private fun getSale(saleId: Int) {
        if (saleId != -1) {
            val callSale = saleService.readSaleWithId(token, saleId)
            callSale.enqueue(object: Callback<ReadSaleResponse> {
                override fun onResponse(call: Call<ReadSaleResponse>, response: Response<ReadSaleResponse>) {
                    if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                        sale = response.body()!!.sales[0]
                        init()
                    }
                }

                override fun onFailure(call: Call<ReadSaleResponse>, t: Throwable) {
                    Log.e(TAG, "getSale()-[onFailure] 실패 : $t")
                }
            })
        }
    }

    private fun init() {
        initToolbar()
        initSendMessage()
        initSale()
        initMessages()
        initSocket()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
        saleService = retrofit.create(SaleService::class.java)
    }

    private fun initToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        if (intent.hasExtra("otherNick")) {
            val otherNick = intent.getStringExtra("otherNick")
            toolbar.title = otherNick
        } else {
            toolbar.title = sale.user.nick
        }
//        if (chat != null) {
//            // 내가 판매자, 상대방이 구매자
//            toolbar.title = chat.buyUser.nick
//            chatId = chat.id
//        } else {
//            // 내가 구매자, 상대방이 판매자
//            toolbar.title = sale.user.nick
//        }
        toolbar.setNavigationOnClickListener { finishChat() }
    }

    override fun onBackPressed() {
        finishChat()
    }

    private fun finishChat() {
        checkNullChat()
        socket.disconnect()
        finish()
    }

    private fun checkNullChat() {
        if (adapter.itemCount == 0) {
            val callChat = chatService.deleteChat(token, chatId, sale.id)
            callChat.enqueue(object: Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {

                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.e(TAG, "checkNullChat()-[onFailure] 실패 : $t")
                }
            })
        }
    }

    private fun initSendMessage() {
        etChat = findViewById(R.id.et_chat)
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
        val thumbnail = findViewById<ImageView>(R.id.iv_thumbnail)
        if (sale.images.isNotEmpty()) {
            val url = getString(R.string.img_url) + sale.images[0].path
            Glide.with(this)
                    .load(url)
                    .into(thumbnail)
        }

        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale.title

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale.price))
    }

    private fun initMessages() {
        rvMessage = findViewById(R.id.rv_message)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvMessage.layoutManager = layoutManager

        val myNick = Util.readUser(this)!!.nick
        adapter = MessageAdapter(this, myNick)
        rvMessage.adapter = adapter

        if (chatId == -1) {
            checkChat()
        } else {
            getMessages()
        }
    }

    // 기존 채팅 여부 확인
    private fun checkChat() {
        val user = Util.readUser(this)!!
        val callChat = chatService.existingBuyChat(token, sale.id, user.id)
        callChat.enqueue(object: Callback<ChatIdResponse> {
            override fun onResponse(call: Call<ChatIdResponse>, response: Response<ChatIdResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    chatId = response.body()!!.chatId
                    if (chatId == -1) {
                        startNewChat()
                    } else {
                        getMessages()
                    }
                }
            }

            override fun onFailure(call: Call<ChatIdResponse>, t: Throwable) {
                Log.e("ChatActivity", "getMessages()-[onFailure] 실패 : $t")
            }
        })
    }

    // 새로운 채팅 시작
    private fun startNewChat() {
        val user = Util.readUser(this)!!
        val callChat = chatService.newChat(token, user.id, sale.id)
        callChat.enqueue(object: Callback<ChatIdResponse> {
            override fun onResponse(call: Call<ChatIdResponse>, response: Response<ChatIdResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    chatId = response.body()!!.chatId
                    initSocket()
                }
            }

            override fun onFailure(call: Call<ChatIdResponse>, t: Throwable) {
                Log.e(TAG, "startNewChat()-[onFailure] 실패 : $t")
            }
        })
    }

    // 기존 채팅 가져오기
    private fun getMessages() {
        val callChat = chatService.loadChat(token, chatId)
        callChat.enqueue(object: Callback<LoadChatResponse> {
            override fun onResponse(call: Call<LoadChatResponse>, response: Response<LoadChatResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    val messages = response.body()!!.messages
                    for (message in messages) {
                        adapter.addMessage(message)
                    }
                    adapter.notifyDataSetChanged()
                    rvMessage.scrollToPosition(adapter.itemCount - 1)
                    initSocket()
                }
            }

            override fun onFailure(call: Call<LoadChatResponse>, t: Throwable) {
                Log.e(TAG, "getMessages()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun initSocket() {
        socket = IO.socket(getString(R.string.socket_io))
        socket.connect()
        socket.on(Socket.EVENT_CONNECT, onConnect)
        socket.on("updateChat", onUpdateChat)
    }

    var onConnect = Emitter.Listener {
        val jsonData = gson.toJson(chatId)
        socket.emit("subscribe", jsonData)
    }

    var onUpdateChat = Emitter.Listener {
        val chat: Message = gson.fromJson(it[0].toString(), Message::class.java)
        Log.d("", chat.toString())
        addItemToRecyclerView(chat)
    }

    private fun sendMessage() {
        val user = Util.readUser(this)!!
        val message = etChat.text.toString()
        val createdAt = Util.getCurrentTime()
        val sendData = Message(-1, message, createdAt, user, chatId)
        val jsonData = gson.toJson(sendData)
        socket.emit("newMessage", jsonData)
        addItemToRecyclerView(sendData)

        val callChat = chatService.sendMessage(token, chatId, message, user.id, sale.id)
        callChat.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {

            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "sendMessage()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun addItemToRecyclerView(message: Message) {
        runOnUiThread {
            adapter.addMessage(message)
            adapter.notifyItemInserted(adapter.itemCount)
            etChat.text = null
            rvMessage.scrollToPosition(adapter.itemCount - 1)
        }
    }

    class MessageAdapter(private val context: Context, private val myNick: String) : RecyclerView.Adapter<MessageAdapter.BaseViewHolder<*>>() {
        private val chatList: ArrayList<Message> = arrayListOf()

        companion object {
            private const val TYPE_SEND = 0
            private const val TYPE_RECEIVE = 1
        }

        fun addMessage(message: Message) {
            chatList.add(message)
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
            val message = chatList[position]
            // 채팅 첫 시작 || 채팅 날짜 변화 : true
            val visibleDate = position == 0 ||
                    Util.getDate(message.createdAt) != Util.getDate(chatList[position-1].createdAt)

            when (holder) {
                is SendViewHolder -> holder.bind(message, visibleDate)
                is ReceiveViewHolder -> holder.bind(message, visibleDate)
                else -> throw IllegalArgumentException()
            }
        }

        override fun getItemViewType(position: Int): Int {
            val nick = chatList[position].user.nick
            return if (myNick == nick) {
                TYPE_SEND
            } else {
                TYPE_RECEIVE
            }

            return TYPE_SEND
        }

        override fun getItemCount(): Int = chatList.size

        inner class SendViewHolder(itemView: View) : BaseViewHolder<Message>(itemView) {
            override fun bind(item: Message, visibleDate: Boolean) {
                val viewDate = itemView.findViewById<ConstraintLayout>(R.id.v_date)
                if (visibleDate) {
                    viewDate.visibility = View.VISIBLE
                    val date = viewDate.findViewById<TextView>(R.id.tv_date)
                    date.text = Util.getDate(item.createdAt)
                } else {
                    viewDate.visibility = View.GONE
                }

                val time = itemView.findViewById<TextView>(R.id.tv_time)
                time.text = Util.getTime(item.createdAt)
                val message = itemView.findViewById<TextView>(R.id.tv_message)
                message.text = item.message
            }
        }

        inner class ReceiveViewHolder(itemView: View) : BaseViewHolder<Message>(itemView) {
            override fun bind(item: Message, visibleDate: Boolean) {
                val viewDate = itemView.findViewById<ConstraintLayout>(R.id.v_date)
                if (visibleDate) {
                    viewDate.visibility = View.VISIBLE
                    val date = viewDate.findViewById<TextView>(R.id.tv_date)
                    date.text = Util.getDate(item.createdAt)
                } else {
                    viewDate.visibility = View.GONE
                }

                val profile = itemView.findViewById<ImageView>(R.id.iv_profile)
                if (item.user.img.isEmpty()) {
                    profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
                } else {
                    Glide.with(context)
                            .load(item.user.img)
                            .into(profile)
                }

                val message = itemView.findViewById<TextView>(R.id.tv_message)
                message.text = item.message

                val time = itemView.findViewById<TextView>(R.id.tv_time)
                time.text = Util.getTime(item.createdAt)
            }
        }

        abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
            abstract fun bind(item: T, visibleDate: Boolean)
        }
    }

    companion object {
        private const val TAG = "ChatActivity"
    }
}