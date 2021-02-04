package com.yujin.onionmarket

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.yujin.onionmarket.data.User
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

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

        // 게시글 시간 경과 표시
        fun timeDifferentiation(createdAt: String?) : String {
            if (!createdAt.isNullOrEmpty()) {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val pos = ParsePosition(0)
                val then = formatter.parse(createdAt, pos).time
                val now = Date().time

                val seconds = (now - then) / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val weeks = days / 7
                val months = weeks / 30

                var lapse = ""
                var num: Long = 0
                if (months > 0) {
                    lapse = "${months}달 전"
                }
                else if (weeks > 0) {
                    lapse = "${weeks}주 전"
                }
                else if (days > 0) {
                    lapse = "${days}일 전"
                }
                else if (hours > 0) {
                    lapse = "${hours}시간 전"
                }
                else if (minutes > 0) {
                    lapse = "${minutes}분 전"
                }
                else if (seconds > 0) {
                    lapse = "${seconds}초 전"
                }
                else {
                    lapse = "방금 전"
                }

                return lapse
            }
            return ""
        }

        // 날짜만 추출하기 (yyyy년 mm월 dd일)
        fun getDate(createdAt: String) : String {
            val listDate = createdAt.split("T")[0].split("-")
            return listDate[0] + "년 " + listDate[1] + "월 " + listDate[2] + "일"
        }

        // 시간만 추출하기 (00:00 오전/00:00 오후)
        fun getTime(createdAt: String) : String {
            val listTime = createdAt.split("T")[1].split(":")
            val intHour = listTime[0].toInt()
            val intMinute = listTime[1].toInt()

            return if (intHour == 12) {
                "${intHour}:${intMinute} 오후"
            } else if (intHour < 12) {
                "${intHour}:${intMinute} 오전"
            } else {
                "${intHour-12}:${intMinute} 오후"
            }
        }

        // 현재 시각 가져오기
        fun getCurrentKST() : String {
            val kstFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.KOREA)
            kstFormat.timeZone = TimeZone.getTimeZone("KST")
            return kstFormat.format(Date())
        }
    }
}