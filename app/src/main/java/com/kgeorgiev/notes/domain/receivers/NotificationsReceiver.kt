package com.kgeorgiev.notes.domain.receivers

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.data.entity.Note
import com.kgeorgiev.notes.data.repository.NotesRepository
import com.kgeorgiev.notes.domain.AlarmHelper
import com.kgeorgiev.notes.presentation.ui.activities.SplashScreenActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by kostadin.georgiev on 10/7/2019.
 */
class NotificationsReceiver : BroadcastReceiver() {
    private val TAG = NotificationsReceiver::class.java.name
    private val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_channel"
    private val NOTIFICATION_CHANNEL_ID = "101"
    private val DEFAULT_INTENT_REQUEST_CODE = 100

    @Inject
    lateinit var notesRepository: NotesRepository

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.Main + job)
    private lateinit var context: Context


    override fun onReceive(context: Context?, intent: Intent?) {
        (context?.applicationContext as App).appComponent.inject(this)
        this.context = context

        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            // Re-start alarms in case of device reboot
            reScheduleAlarms()
            return
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val splashScreenIntent = Intent(context, SplashScreenActivity::class.java)
        splashScreenIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        splashScreenIntent.putExtras(intent!!)

        // Create the TaskStackBuilder get current intent stack to add 'splashScreenIntent' intent into it
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addNextIntentWithParentStack(splashScreenIntent)

        val pendingIntent =
            stackBuilder.getPendingIntent(
                DEFAULT_INTENT_REQUEST_CODE,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val notification = buildNotification(context, pendingIntent, intent.extras!!)
        val notificationId = intent.extras!!.getInt(NOTIFICATION_ID_PARAM)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                importance
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        Log.e(TAG, "Making notification")
        notificationManager.notify(notificationId, notification)
        resetNoteReminderInDB(notificationId)
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

    /**
     * Reset note reminder time in database
     */
    private fun resetNoteReminderInDB(noteId: Int) {
        ioScope.launch {
            notesRepository.updateNoteReminderTime(noteId, 0)
        }
    }

    /**
     * This should be called after device gets rebooted
     */
    private fun reScheduleAlarms() {
        ioScope.launch {
            val scheduledNotes = notesRepository.getScheduledNotes()
            if (scheduledNotes.isNotEmpty()) {
                Log.e(TAG, "Rechedule alarms after reboot")
                for (note: Note in scheduledNotes) {
                    AlarmHelper.scheduleAlarm(note, context)
                }
            }
        }
    }

    companion object {
        const val NOTIFICATION_TITLE_PARAM = "title_param"
        const val NOTIFICATION_TEXT_PARAM = "text_param"
        const val NOTIFICATION_ID_PARAM = "note_id_param" // equals to note id
    }
}