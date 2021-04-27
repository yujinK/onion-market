package com.yujin.onionmarket.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.asksira.loopingviewpager.LoopingViewPager
import com.asksira.loopingviewpager.indicator.CustomShapePagerIndicator
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.*
import com.yujin.onionmarket.network.ChatService
import com.yujin.onionmarket.network.FavoriteService
import com.yujin.onionmarket.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailSaleActivity : AppCompatActivity() {
    private lateinit var retrofit: Retrofit
    private lateinit var chatService: ChatService
    private lateinit var favoriteService: FavoriteService

    private lateinit var btnChat: Button
    private lateinit var chatSheet: BottomSheetDialog
    private lateinit var btnFavorite: ImageButton

    private lateinit var sale: Sale

    private var saleChatList: ArrayList<Chat> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_sale)
        init()
    }

    private fun init() {
        makeStatusBarTransparent()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_detail)) { _, insets ->
            toolbar.setMarginTop(insets.systemWindowInsetTop)
            insets.consumeSystemWindowInsets()
        }
        toolbar.setNavigationOnClickListener { finish() }

        sale = intent.getParcelableExtra("sale")!!

        initRetrofit()
        setView(sale)
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        chatService = retrofit.create(ChatService::class.java)
        favoriteService = retrofit.create(FavoriteService::class.java)
    }

    private fun setView(sale: Sale) {
        setUser(sale.user)
        setImages(sale.images)
        setGoods(sale)
        setChat(sale)
        setFavorite(sale)

        // 내가 올린 상품일 경우 채팅 개수 설정
        val myUser = Util.readUser(this)!!
        if (myUser.id == sale.user.id) {
            setChat(sale)
        }
    }

    private fun setUser(user: User?) {
        val profile = findViewById<ImageView>(R.id.iv_profile)
        if (user!!.img.isNullOrEmpty()) {
            profile.setImageDrawable(getDrawable(R.drawable.ic_profile))
        } else {
            Glide.with(this)
                    .load(user?.img)
                    .into(profile)
        }

        val nick = findViewById<TextView>(R.id.tv_nick)
        nick.text = user.nick

        val location = findViewById<TextView>(R.id.tv_location)
        location.text = getString(R.string.str_full_location, user.location.sigun, user.location.dongmyeon, user.location.li)
    }

    private fun setImages(images: ArrayList<Image>?) {
        val loopingViewPager = findViewById<LoopingViewPager>(R.id.pager_image)
        val indicator = findViewById<CustomShapePagerIndicator>(R.id.indicator)
        if (!images.isNullOrEmpty()) {
            loopingViewPager.visibility = View.VISIBLE
            indicator.visibility = View.VISIBLE

            val adapter = ImageAdapter(this, images, false)
            loopingViewPager.adapter = adapter

            indicator.highlighterViewDelegate = {
                val highlighter = View(this)
                highlighter.layoutParams = FrameLayout.LayoutParams(16.dp(), 2.dp())
                highlighter.setBackgroundColor(getColorCompat(R.color.white))
                highlighter
            }
            indicator.unselectedViewDelegate = {
                val unselected = View(this)
                unselected.layoutParams = LinearLayout.LayoutParams(16.dp(), 2.dp())
                unselected.setBackgroundColor(getColorCompat(R.color.white))
                unselected.alpha = 0.4f
                unselected
            }
            loopingViewPager.onIndicatorProgress = { selectingPosition, progress -> indicator.onPageScrolled(selectingPosition, progress) }
            indicator.updateIndicatorCounts(loopingViewPager.indicatorCount)
        }
    }

    private fun setGoods(sale: Sale?) {
        val title = findViewById<TextView>(R.id.tv_title)
        title.text = sale?.title

        val categoryAndDate = findViewById<TextView>(R.id.tv_category_and_date)
        val calDate = Util.timeDifferentiation(sale?.createdAt)
        categoryAndDate.text = getString(R.string.str_dot_str, sale?.category?.name, calDate)

        val content = findViewById<TextView>(R.id.tv_content)
        content.text = sale?.content

        val chatFavView = findViewById<TextView>(R.id.tv_chat_fav_view)
        chatFavView.text = getString(R.string.chat_fav_view, sale?.chatCount, sale?.favoriteCount, sale?.viewCount)

        val price = findViewById<TextView>(R.id.tv_price)
        price.text = getString(R.string.price_won, NumberFormat.getNumberInstance(Locale.KOREA).format(sale?.price))

        val proposal = findViewById<TextView>(R.id.tv_proposal)
        if (sale?.priceProposal == true) {
            proposal.text = getString(R.string.ok_proposal)
        } else {
            proposal.text = getString(R.string.no_proposal)
        }
    }

    private fun setChat(sale: Sale) {
        btnChat = findViewById(R.id.btn_chat_buy)
        val myUser = Util.readUser(this)!!
        if (sale.user.id == myUser.id) {
            findSaleChatList(sale)
        }
        btnChat.setOnClickListener { buyChat(sale) }
    }

    private fun setFavorite(sale: Sale) {
        val token = Util.readToken(this)
        val user = Util.readUser(this)!!
        val callFavorite = favoriteService.getFavorite(token, sale.id, user.id)
        callFavorite.enqueue(object: Callback<FavoriteResponse> {
            override fun onResponse(call: Call<FavoriteResponse>, response: Response<FavoriteResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    btnFavorite = findViewById(R.id.ib_favorite)
                    btnFavorite.isSelected = !(response.body()!!.favorites.isNullOrEmpty())
                    btnFavorite.setOnClickListener { clickFavorite() }
                }
            }

            override fun onFailure(call: Call<FavoriteResponse>, t: Throwable) {
                Log.e(TAG, "setFavorite()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun clickFavorite() {
        if (btnFavorite.isSelected) {
            deleteFavorite()
        } else {
            addFavorite()
        }
    }

    private fun addFavorite() {
        val token = Util.readToken(this)
        val user = Util.readUser(this)!!
        val callFavorite = favoriteService.addFavorite(token, sale.id, user.id)
        callFavorite.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    btnFavorite.isSelected = true
                    MaterialAlertDialogBuilder(this@DetailSaleActivity)
                        .setMessage(getString(R.string.add_favorite))
                        .setPositiveButton(getString(R.string.ok)) { _, _ -> }
                        .show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "addFavorite()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun deleteFavorite() {
        val token = Util.readToken(this)
        val user = Util.readUser(this)!!
        val callFavorite = favoriteService.deleteFavorite(token, sale.id, user.id)
        callFavorite.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    btnFavorite.isSelected = false
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e(TAG, "addFavorite()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun buyChat(sale: Sale) {
        val myUser = Util.readUser(this)!!
        if (sale.user.id == myUser.id) {
            // 내가 올린 게시글이면 대화중인 채팅방 목록 보여줌
            showChatSheet()
        } else {
            // 채팅 시작
            startBuyChat(sale)
        }
    }

    private fun findSaleChatList(sale: Sale) {
        // 채팅 1개 이상이면 showChatSheet()
        val token = Util.readToken(this)
        val callChat = chatService.existingSaleChat(token, sale.id)
        callChat.enqueue(object: Callback<ChatsResponse> {
            override fun onResponse(call: Call<ChatsResponse>, response: Response<ChatsResponse>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_GET) {
                    saleChatList = response.body()!!.chats
                    if (saleChatList.size > 0) {
                        btnChat.text = getString(R.string.chat_buy_num, saleChatList.size)
                    }
                }
            }

            override fun onFailure(call: Call<ChatsResponse>, t: Throwable) {
                Log.e(TAG, "findSaleChatList()-[onFailure] 실패 : $t")
            }
        })
    }

    private fun showChatSheet() {
        if (saleChatList.size > 0) {
            chatSheet = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.item_dialog_chat, null, false)
            chatSheet.setContentView(view)

            val tvChatNum = chatSheet.findViewById<TextView>(R.id.tv_chat_num)!!
            tvChatNum.text = saleChatList.size.toString()

            val rvChat = chatSheet.findViewById<RecyclerView>(R.id.rv_chat)!!
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = SaleChatAdapter(this, saleChatList)
            rvChat.layoutManager = layoutManager
            rvChat.adapter = adapter
            adapter.notifyDataSetChanged()

            chatSheet.show()
        } else {
            Toast.makeText(this, getString(R.string.no_sale_chat), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBuyChat(sale: Sale) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("sale", sale)
        startActivity(intent)
    }

    private fun startSaleChat(sale: Sale, chat: Chat) {
        chatSheet.dismiss()
        val intent = Intent(this, ChatActivity::class.java)
//        intent.putExtra("sale", sale)
//        intent.putExtra("chat", chat)
        intent.putExtra("sale", sale)
        intent.putExtra("chatId", chat.id)
        intent.putExtra("otherNick", chat.buyUser.nick)
        startActivity(intent)
    }

    private fun Activity.makeStatusBarTransparent() {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun View.setMarginTop(marginTop: Int) {
        val menuLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
        menuLayoutParams.setMargins(0, marginTop, 0, 0)
        this.layoutParams = menuLayoutParams
    }

    private fun Int.dp(): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics).toInt()
    }

    private fun Context.getColorCompat(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    inner class SaleChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>) : RecyclerView.Adapter<SaleChatAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.item_sale_chat, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (chatList[position].buyUser.img.isEmpty()) {
                holder.profile.setImageDrawable(context.getDrawable(R.drawable.ic_profile))
            } else {
                Glide.with(context)
                        .load(chatList[position].buyUser.img)
                        .into(holder.profile)
            }

            holder.nick.text = chatList[position].buyUser.nick

            val location = getString(R.string.str_location, chatList[position].buyUser.location.dongmyeon, chatList[position].buyUser.location.li)
            val date = Util.getSaleChatDiff(chatList[position].updatedAt)
            holder.locationDate.text = getString(R.string.str_dot_str, location, date)

            holder.lastMessage.text = chatList[position].lastMessage

            holder.itemView.setOnClickListener { startSaleChat(sale, chatList[position]) }
        }

        override fun getItemCount(): Int = chatList.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val profile: ImageView = view.findViewById(R.id.iv_profile)
            val nick: TextView = view.findViewById(R.id.tv_nick)
            val locationDate: TextView = view.findViewById(R.id.tv_location_date)
            val lastMessage: TextView = view.findViewById(R.id.tv_last_message)
        }
    }

    companion object {
        private const val TAG = "DetailSaleActivity"
    }
}