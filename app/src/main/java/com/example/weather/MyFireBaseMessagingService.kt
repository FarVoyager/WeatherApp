package com.example.weather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFireBaseMessagingService : FirebaseMessagingService() {

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(p0: RemoteMessage) {
        val remoteMessageData = p0.data
        if (remoteMessageData.isNotEmpty()) {
            val title = remoteMessageData.toMap()[PUSH_KEY_TITLE]
            val message = remoteMessageData.toMap()[PUSH_KEY_MESSAGE]
            if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
                showNotification(title, message)
                defineFirebaseToken()
            }
        }
    }


    private fun showNotification(title: String, message: String) {
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                setContentTitle(title)
                setContentText(message)
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    private fun defineFirebaseToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    // Не удалось получить токен, произошла ошибка
                    return@OnCompleteListener
                }

                // Получить токен
                val token = task.result!!.token
                // Сохранить токен...
                println("BEB $token")
            })
    }

    companion object {
        private const val PUSH_KEY_TITLE = "title"
        private const val PUSH_KEY_MESSAGE = "message"
        private const val CHANNEL_ID = "channel_id"
        private const val NOTIFICATION_ID = 37
    }

}