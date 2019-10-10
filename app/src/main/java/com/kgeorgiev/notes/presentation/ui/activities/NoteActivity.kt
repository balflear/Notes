package com.kgeorgiev.notes.presentation.ui.activities

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.data.entity.Note
import com.kgeorgiev.notes.domain.DateFormatter
import com.kgeorgiev.notes.domain.receivers.NotificationsReceiver
import com.kgeorgiev.notes.presentation.base.BaseActivity
import com.kgeorgiev.notes.presentation.di.ViewModelFactoryProvider
import com.kgeorgiev.notes.presentation.ui.dialogs.BiometricsHelper
import com.kgeorgiev.notes.presentation.ui.dialogs.MessageDialogFragment
import com.kgeorgiev.notes.presentation.viewmodels.NotesViewModel
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_add_note.view.*
import java.util.*
import javax.inject.Inject


class NoteActivity : BaseActivity(), MessageDialogFragment.DialogClickListener {
    @Inject
    lateinit var viewModelFactoryProvider: ViewModelFactoryProvider

    private lateinit var notesViewModel: NotesViewModel
    private var selectedNote: Note? = null
    private lateinit var mediaPlayer: MediaPlayer
    private val myCalendar = Calendar.getInstance()
    private lateinit var menuAdd: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        (application as App).appComponent.inject(this)
        setupActionBar()

        mediaPlayer = MediaPlayer.create(this, R.raw.completed)
        notesViewModel =
            ViewModelProviders.of(this, viewModelFactoryProvider).get(NotesViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (intent.extras != null) { // If user wants to edit/delete note, logic goes here
            selectedNote = intent.extras!!.get("note") as Note

            etTitle.setText(selectedNote?.title)
            etDescription.setText(selectedNote?.description)
        }

        startAnimations()
        initViewsAndListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        menuAdd = menu.findItem(R.id.action_add)
        (menuAdd.actionView as ImageView).setImageResource(android.R.drawable.ic_menu_save)
        val itemDelete = menu.findItem(R.id.action_delete)
        val itemLock = menu.findItem(R.id.action_lock)
        val itemUnlock = menu.findItem(R.id.action_unlock)
        val itemReminder = menu.findItem(R.id.action_reminder)

        if (selectedNote == null) {// Means it's a new note case
            itemDelete.isVisible = false
            itemLock.isVisible = false
            itemUnlock.isVisible = false
            itemReminder.isVisible = false
        }

        // Handle lock/unlock button state if device has hardware-fingerprint
        if (BiometricsHelper.canAuthenticateWithBiometrics(this) && selectedNote != null) {
            if (selectedNote!!.isLocked) {
                itemUnlock.isVisible = true
                itemLock.isVisible = false
            } else {
                itemUnlock.isVisible = false
                itemLock.isVisible = true
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_add -> {
                insertOrUpdateNote()
                true
            }

            R.id.action_delete -> {
                showDeleteNoteDialog()
                true
            }

            R.id.action_lock -> {
                selectedNote?.let {
                    it.isLocked = true
                    updateNote(selectedNote)
                }
                true
            }

            R.id.action_unlock -> {
                selectedNote?.let {
                    it.isLocked = false
                    updateNote(selectedNote)
                }
                true
            }

            R.id.action_reminder -> {
                showDatePickerDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPositiveButtonClicked() {
        //TODO: Check if there is reminder for this note, and remove it too
        deleteNote(selectedNote!!)
    }

    override fun onNegativeButtonClicked() {
        // Does nothing for now
    }


    private fun insertOrUpdateNote() {
        if (selectedNote != null) {
            updateNote(selectedNote)
        } else {
            insertNote()
        }
    }

    private fun insertNote() {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.msg_please_fill_fields), Toast.LENGTH_LONG)
                .show()
            return
        }

        notesViewModel.insertNote(Note(title, description, Date()))
        mediaPlayer.start()
        Toast.makeText(this, getString(R.string.msg_note_added), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun updateNote(noteToUpdate: Note?) {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.msg_please_fill_fields), Toast.LENGTH_LONG)
                .show()
            return
        }

        noteToUpdate?.title = title
        noteToUpdate?.description = description

        mediaPlayer.start()
        notesViewModel.updateNote(noteToUpdate!!)
        Toast.makeText(this, getString(R.string.msg_note_updated), Toast.LENGTH_LONG).show()
        finish()
    }

    private fun showDeleteNoteDialog() {
        val messageDialogFragment =
            MessageDialogFragment.newInstance(
                titleResId = R.string.dialog_title_delete_note,
                dialogClickListener = this
            )

        messageDialogFragment.show(supportFragmentManager, "")
    }

    private fun deleteNote(noteToDelete: Note) {
        notesViewModel.deleteNote(noteToDelete)
        Toast.makeText(this, getString(R.string.msg_note_deleted), Toast.LENGTH_LONG).show()
        finish()
    }

    private var date: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            showTimePickerDialog()
        }

    private var timePickerListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hour)
            myCalendar.set(Calendar.MINUTE, minute)
            myCalendar.set(Calendar.SECOND, 0)

            scheduleNotification(myCalendar.timeInMillis)

            showToastMsg(getString(R.string.msg_reminder_set) + " ${DateFormatter.formatDate(myCalendar.time)}")
        }

    private fun showDatePickerDialog() {
        DatePickerDialog(
            this, date,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePickerDialog() {
        TimePickerDialog(
            this, timePickerListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun startAnimations() {
        etTitle.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.fade_left_to_right_animation
            )
        )

        etDescription.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.fade_left_to_right_animation
            )
        )

        if (selectedNote == null && menuAdd.actionView != null) {
            // Start animation only if it's a new note
            startSaveBtnAnimation()
        }
    }

    private fun startSaveBtnAnimation() {
        // Repeat Save btn bounce animation
        val handler = Handler()
        var counter = 0

        val runnable = object : Runnable {
            override fun run() {
                counter++
                menuAdd.actionView.startAnimation(AnimationUtils.loadAnimation(application, R.anim.bounce_animation))
                handler.postDelayed(this, 5000)
                if (counter == 3) {
                    handler.removeCallbacks(this)
                }
            }
        }

        handler.postDelayed(runnable, 2000)
    }

    private fun scheduleNotification(alarmTime: Long) {
        val notificationIntent = Intent(this, NotificationsReceiver::class.java)
        val bundle = Bundle()
        bundle.putString(NotificationsReceiver.NOTIFICATION_TITLE_PARAM, selectedNote?.title)
        bundle.putString(NotificationsReceiver.NOTIFICATION_TEXT_PARAM, selectedNote?.description)
        bundle.putInt(NotificationsReceiver.NOTE_ID_PARAM, selectedNote!!.id)
        notificationIntent.putExtras(bundle)
        notificationIntent.action = "android.intent.action.NOTIFY"

        val requestCode = selectedNote?.id
        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                requestCode!!,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            pendingIntent
        )

        selectedNote?.dateOfReminder = myCalendar.timeInMillis
        notesViewModel.updateNote(selectedNote!!)
    }

    private fun initViewsAndListeners() {
        if (selectedNote == null) {
            toolbar.toolbar_title.text = getString(R.string.title_activity_add_note)
        } else {
            toolbar.toolbar_title.text = getString(R.string.title_activity_modify_note)
        }

        etDescription.setOnEditorActionListener { textView, actionId, keyEvent ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    insertOrUpdateNote()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }
}