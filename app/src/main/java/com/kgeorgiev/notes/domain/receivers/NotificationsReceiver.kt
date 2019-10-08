package com.kgeorgiev.notes.domain.receivers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.presentation.ui.activities.SplashScreenActivity


/**
 * Created by kostadin.georgiev on 10/7/2019.
 */
class NotificationsReceiver : BroadcastReceiver() {
    private val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_channel"
    private val NOTIFICATION_CHANNEL_NAME = "My Notes"
    private val NOTIFICATION_CHANNEL_ID = "101"
    private val NOTIFICATION_ID = 1000

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        //val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        val splashScreenIntent = Intent(context, SplashScreenActivity::class.java)
        splashScreenIntent.putExtras(intent!!)

        val pendingIntent =
            PendingIntent.getActivity(context, 100, splashScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = buildNotification(context, pendingIntent, intent.extras!!)


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                importance
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(context: Context, pendingItent: PendingIntent, intentData: Bundle): Notification {
        val notificationTitle = intentData.getString(NOTIFICATION_TITLE_PARAM)
        val notificationText = intentData.getString(NOTIFICATION_TEXT_PARAM)


        val builder = NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
        builder.setContentIntent(pendingItent)
        builder.setContentTitle(notificationTitle)
        builder.setContentText(notificationText)
        builder.setSmallIcon(R.mipmap.ic_launcher_foreground)
        builder.setDefaults(Notification.DEFAULT_ALL)
        builder.setAutoCancel(true)
        builder.setChannelId(NOTIFICATION_CHANNEL_ID)
        return builder.build()

    }

    companion object {
        val NOTIFICATION_TITLE_PARAM = "title_param"
        val NOTIFICATION_TEXT_PARAM = "text_param"
        val NOTE_ID_PARAM = "note_id_param"
    }
}