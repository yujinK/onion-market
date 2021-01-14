package com.yujin.onionmarket

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.yujin.onionmarket.data.User
import com.yujin.onionmarket.view.LoginActivity

class Util {
    companion object {
        private val gson = GsonBuilder().create()

        // 유저 정보 가져오기
        fun readUser(activity: Activity) : User? {
            val sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            val strUser = sharedPref.getString("strUser", "")
            return if (strUser == "") {
                null
            } else {
                gson.fromJson(strUser, User::class.java)
            }
        }

        // 토큰 정보 가져오기
        fun readToken(activity: Activity) : String {
            val sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            return sharedPref.getString("token", "")!!
        }

        fun readToken(context: Context) : String {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            return sharedPref.getString("token", "")!!
        }

        // 유저 정보 저장
        fun saveUserInfo(activity: Activity, user: User, token: String) {
            val strUser = gson.toJson(user, User::class.java)
            val sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("strUser", strUser)
                putString("token", token)
                commit()
            }
        }

        // 모든 정보 삭제
        fun clearUserInfo(activity: Activity) {
            val sharedPref = activity.getSharedPreferences(activity.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                commit()
            }
        }

        // 로그인 요청 Dialog
        fun requireLogin(context: Context, positiveListener: DialogInterface.OnClickListener) {
            MaterialAlertDialogBuilder(context)
                .setMessage(context.getString(R.string.require_login_message))
                .setPositiveButton(context.getString(R.string.login_sign_up), positiveListener)
                .setNegativeButton(context.getString(R.string.cancel)) { _, _ ->

                }
                .show()
        }
    }
}