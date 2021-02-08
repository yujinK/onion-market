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

        fun readUser(context: Context) : User? {
            val sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
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

        // 대화중인 채팅방 시간 계산
        fun getSaleChatDiff(updatedAt: String?) : String {
            if (!updatedAt.isNullOrEmpty()) {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                val pos = ParsePosition(0)
                val then = formatter.parse(updatedAt, pos).time
                val now = Date().time

                val seconds = (now - then) / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val weeks = days / 7
                val months = weeks / 30
                val years = months / 12

                var lapse = ""
                var num: Long = 0
                if (years > 0) {
                    lapse = updatedAt.toDate().formatTo("yyyy년 MM월 dd일")
                }
                else if (months > 0 || weeks > 0) {
                    lapse = updatedAt.toDate().formatTo("MM월 dd일")
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
            val kst = createdAt.toDate().formatTo("yyyy년 MM월 dd일")
            return kst
        }

        // 시간만 추출하기 (00:00 오전/00:00 오후)
        fun getTime(createdAt: String) : String {
            val kst = createdAt.toDate().formatTo("HH:mm").split(":")
            val intHour = kst[0].toInt()
            val intMinute = kst[1].toInt()

            return if (intHour == 12) {
                String.format("%02d:%02d 오후", intHour, intMinute)
            } else if (intHour < 12) {
                String.format("%02d:%02d 오전", intHour, intMinute)
            } else {
                String.format("%02d:%02d 오후", intHour-12, intMinute)
            }
        }

        // 현재 시각 가져오기
        fun getCurrentTime() : String {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date())
        }

        private fun String.toDate(dateFormat: String="yyyy-MM-dd'T'HH:mm:ss.SSS", timeZone: TimeZone = TimeZone.getTimeZone("UTC")) : Date {
            val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
            parser.timeZone = timeZone
            return parser.parse(this)
        }

        private fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()) : String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }
    }
}