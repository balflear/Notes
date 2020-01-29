package com.kgeorgiev.notes.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.presentation.receivers.NotificationsReceiver

class AlarmHelper {
    companion object {
        const val NOTIFY_ACTION = "android.intent.action.NOTIFY"

        fun scheduleAlarm(note: Note, context: Context) {
            Log.e("TAG", "Schedule Alarm Note:$note")
            val alarmTime = note.dateOfReminder
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                makeAlarmPendingIntent(note, context)
            )
        }

        fun removeAlarm(note: Note, context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            alarmManager.cancel(makeAlarmPendingIntent(note, context))
        }

        private fun makeAlarmPendingIntent(note: Note, context: Context): PendingIntent {
            val notificationIntent = Intent(context, NotificationsReceiver::class.java)
            val bundle = Bundle()
            bundle.putString(NotificationsReceiver.NOTIFICATION_TITLE_PARAM, note.title)
            bundle.putString(NotificationsReceiver.NOTIFICATION_TEXT_PARAM, note.description)
            bundle.putInt(NotificationsReceiver.NOTIFICATION_ID_PARAM, note.id)
            notificationIntent.putExtras(bundle)
            notificationIntent.action = NOTIFY_ACTION

            val requestCode = note.id
            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            return pendingIntent
        }
    }
}