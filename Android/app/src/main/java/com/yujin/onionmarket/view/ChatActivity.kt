package com.yujin.onionmarket.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yujin.onionmarket.R
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Message
import com.yujin.onionmarket.data.Sale
import java.lang.IllegalArgumentException
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        init()
    }

    private fun init() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "사쿠란보"

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

        initSale()
        initMessages()
    }

    private fun initSale() {
        val sale = intent.getParcelableExtra<Sale>("sale")!!

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

    private fun initMessages() {
        val rvMessage = findViewById<RecyclerView>(R.id.rv_message)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val myNick = Util.readUser(this)!!.nick
        adapter = MessageAdapter(this, myNick)
        rvMessage.layoutManager = layoutManager
        rvMessage.adapter = adapter
    }

    private fun sendMessage() {
        val input = findViewById<EditText>(R.id.et_chat)
        val message = input.text.toString()
        val vMessage = Message(1, message, "5:10 오후", "", "석경", 1)
        adapter.addMessage(vMessage)
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
            val nick = adapterMessageList[position].nick
            return if (myNick == nick) {
                TYPE_SEND
            } else {
                TYPE_RECEIVE
            }
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
                if (item.profileImg.isNullOrEmpty()) {
                    profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
                } else {
                    Glide.with(context)
                            .load(item.profileImg)
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