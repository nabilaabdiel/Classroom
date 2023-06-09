package com.abdiel.classroom.fcm

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.abdiel.classroom.R
import com.abdiel.classroom.ui.home.HomeActivity
import com.crocodic.core.extension.createIntent
import com.crocodic.core.helper.DateTimeHelper
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
        val body : String? = message.data["body"]

        val appNotification = AppNotification(
            title = title,
            content = body
        )

        if (isBackground()) {
            //showNotification adalah notifikasi yang di luar
            showNotification( applicationContext, title ?: return, body ?: return)
        } else {
            //event bus adalah notifikasi yang di dalam
            EventBus.getDefault().post(appNotification)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(notificationManager : NotificationManager) {
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_DESC, NotificationManager.IMPORTANCE_HIGH).apply {
            description = CHANNEL_DESC
            enableLights(true)
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun showNotification(context: Context, title: String, message: String) {

        val homeIntent = createIntent<HomeActivity>()
//        val friendIntent = createIntent<FriendActivity> {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            putExtra(FriendActivity.FRIEND, userId.toInt())
//        }

        val stackBuilder = TaskStackBuilder.create(context).apply {
            addNextIntent(homeIntent)
//            addNextIntent(friendIntent)
        }

        // var resultPendingIntent = PendingIntent.getActivity(context, 1, detailIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_IMMUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)

        val resultPendingIntent = stackBuilder.getPendingIntent(1,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE
            else PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)

        val idNotification = DateTimeHelper().createAtLong().toInt()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(idNotification, builder.build())
        } else {
            notificationManager.notify(idNotification, builder.build())
        }
    }

    private fun isBackground(): Boolean {
        val myProcess = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true

        val km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        // app is in foreground, but if screen is locked show notification anyway
        return km.isKeyguardLocked
    }


    companion object {
        const val CHANNEL_ID = "Classroom"
        const val CHANNEL_DESC = "Classroom is class and room"
    }
}