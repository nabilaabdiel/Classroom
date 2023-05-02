package com.abdiel.classroom.fcm

import android.util.Log
import com.crocodic.core.model.AppNotification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.greenrobot.eventbus.EventBus

class FirebaseMsgService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("firebase-token", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("notification", "${message.data}")

        val title : String? = message.data["title"]
        val content : String? = message.data["content"]

        val appNotification = AppNotification(
            title = title,
            content = content
        )

        EventBus.getDefault().post(appNotification)
    }
}