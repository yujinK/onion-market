package com.yujin.onionmarket.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.yujin.onionmarket.R
import com.yujin.onionmarket.view.ChatActivity

class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            showNotification(remoteMessage)
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: ${remoteMessage.notification!!.body}")
        }
    }

    private fun showNotification(remoteMessage: RemoteMessage) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            this.putExtra("saleId", remoteMessage.data["saleId"]!!.toInt())
            this.putExtra("chatId", remoteMessage.data["chatId"]!!.toInt())
            this.putExtra("otherNick", remoteMessage.data["nick"])
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val channelId = getString(R.string.default_notification_channel_name)
        var notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_onion)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.chat_notification_title))
                .setContentText("${remoteMessage.data["nick"]} : ${remoteMessage.data["message"]}")
                .setColor(getColor(R.color.greenery))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                        getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "refreshed token: $token")
    }

    companion object {
        private const val TAG = "MessagingService"
    }
}