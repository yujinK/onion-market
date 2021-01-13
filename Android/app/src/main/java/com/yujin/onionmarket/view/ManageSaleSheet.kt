package com.yujin.onionmarket.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yujin.onionmarket.R
import com.yujin.onionmarket.ResponseCode
import com.yujin.onionmarket.Util
import com.yujin.onionmarket.data.Sale
import com.yujin.onionmarket.network.RetrofitClient
import com.yujin.onionmarket.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class ManageSaleSheet(private val sale: Sale, private val position: Int) : BottomSheetDialogFragment() {
    private lateinit var retrofit: Retrofit
    private lateinit var manageService: RetrofitService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.dialog_manage_sale, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initRetrofit()
        initView()
    }

    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        manageService = retrofit.create(RetrofitService::class.java)
    }

    private fun initView() {
        val delete = requireView().findViewById<TextView>(R.id.tv_delete)
        delete.setOnClickListener { alertDelete() }
    }

    private fun alertDelete() {
        MaterialAlertDialogBuilder(context)
                .setMessage(getString(R.string.delete_message))
                .setPositiveButton(getString(R.string.delete)) { _, _ ->
                    delete()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ ->

                }
                .show()

    }

    private fun delete() {
        val token = Util.readToken(requireActivity())
        val callDelete = manageService.deleteSale(token, sale.id)
        callDelete.enqueue(object: Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful && response.code() == ResponseCode.SUCCESS_POST) {
                    finish()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ManageSaleSheet", "delete(): $t")
            }
        })
    }

    private fun finish() {
        setFragmentResult("position", bundleOf("position" to position))
        dismiss()
    }
}