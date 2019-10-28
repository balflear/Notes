package com.kgeorgiev.notes.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.data.entity.Note
import com.kgeorgiev.notes.domain.AlarmHelper
import com.kgeorgiev.notes.domain.DateFormatter
import com.kgeorgiev.notes.presentation.base.BaseActivity
import com.kgeorgiev.notes.presentation.di.ViewModelFactoryProvider
import com.kgeorgiev.notes.presentation.ui.dialogs.BiometricsHelper
import com.kgeorgiev.notes.presentation.ui.dialogs.MessageDialogFragment
import com.kgeorgiev.notes.presentation.ui.dialogs.MessageDialogFragment.DialogClickListener
import com.kgeorgiev.notes.presentation.viewmodels.NotesViewModel
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_add_note.view.*
import java.util.*
import javax.inject.Inject


class NoteActivity : BaseActivity() {
    @Inject
    lateinit var viewModelFactoryProvider: ViewModelFactoryProvider

    private lateinit var notesViewModel: NotesViewModel
    private var selectedNote: Note? = null

    private val myCalendar = Calendar.getInstance()
    private lateinit var menuAdd: MenuItem
    private lateinit var menuReminder: MenuItem
    private lateinit var menuRemoveReminder: MenuItem
    private val DATE_PICKER_DIALOG_TAG = "date_picker_tag"
    private val TIME_PICKER_DIALOG_TAG = "time_picker_tag"
    private val MAX_SAVE_BTN_ANIMATIONS_COUNT = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        (application as App).appComponent.inject(this)
        setupActionBar()

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
        val itemShare = menu.findItem(R.id.action_share)
        val itemReminder = menu.findItem(R.id.action_reminder)
        val itemRemoveReminder = menu.findItem(R.id.action_remove_reminder)
        menuReminder = itemReminder
        menuRemoveReminder = itemRemoveReminder

        if (selectedNote == null) {// Means it's a new note case
            itemDelete.isVisible = false
            itemLock.isVisible = false
            itemUnlock.isVisible = false
            itemReminder.isVisible = false
            itemRemoveReminder.isVisible = false
            itemShare.isVisible = false
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

        // Handle reminder button visibility
        if (selectedNote != null) {
            if (selectedNote!!.dateOfReminder > 0) {
                itemReminder.isVisible = false
                itemRemoveReminder.isVisible = true
            } else {
                itemReminder.isVisible = true
                itemRemoveReminder.isVisible = false
            }
        }


        // Set listener on the ImageView of the menu, then invoke the all menu
        menuAdd.actionView.setOnClickListener {
            this.onOptionsItemSelected(menuAdd)
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

            R.id.action_share -> {
                startShareIntent()
                true
            }

            R.id.action_remove_reminder -> {
                showRemoveReminderDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // This is needed, because there is a bug with biometric api, and we need to destroy this activity
    // https://stackoverflow.com/questions/58286606/biometricprompt-executor-and-or-callback-was-null
    override fun onBackPressed() {
        openHomeActivity()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
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
        playSuccessSound()
        Toast.makeText(this, getString(R.string.msg_note_added), Toast.LENGTH_LONG).show()
        openHomeActivity()
    }

    private fun updateNote(noteToUpdate: Note?, updateMsg: String = getString(R.string.msg_note_updated)) {
        val title = etTitle.text.toString()
        val description = etDescription.text.toString()

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, getString(R.string.msg_please_fill_fields), Toast.LENGTH_LONG)
                .show()
            return
        }

        noteToUpdate?.title = title
        noteToUpdate?.description = description

        playSuccessSound()
        notesViewModel.updateNote(noteToUpdate!!)
        Toast.makeText(this, updateMsg, Toast.LENGTH_LONG).show()
        openHomeActivity()
    }

    private fun deleteNote(noteToDelete: Note) {
        notesViewModel.deleteNote(noteToDelete)
        Toast.makeText(this, getString(R.string.msg_note_deleted), Toast.LENGTH_LONG).show()
        openHomeActivity()
    }

    private var datePickerListener: DatePickerDialog.OnDateSetListener =
        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            showTimePickerDialog()
        }

    private var timePickerListener: TimePickerDialog.OnTimeSetListener =
        TimePickerDialog.OnTimeSetListener { timePicker, hour, minute, seconds ->
            myCalendar.set(Calendar.HOUR_OF_DAY, hour)
            myCalendar.set(Calendar.MINUTE, minute)
            myCalendar.set(Calendar.SECOND, 0)

            scheduleAlarm(myCalendar.timeInMillis, selectedNote!!)
            playSuccessSound()
        }

    private fun showDeleteNoteDialog() {
        val currentContext = this
        val messageDialogFragment =
            MessageDialogFragment.newInstance(
                titleResId = R.string.dialog_title_delete_note,
                dialogClickListener = object : DialogClickListener {
                    override fun onPositiveButtonClicked() {
                        AlarmHelper.removeAlarm(selectedNote!!, currentContext)
                        deleteNote(selectedNote!!)
                        playSuccessSound()
                    }

                    override fun onNegativeButtonClicked() {
                        // No impl needed here
                    }
                }
            )

        messageDialogFragment.show(supportFragmentManager, "")
    }

    private fun showRemoveReminderDialog() {
        val descriptionMsg = String.format(
            getString(R.string.dialog_desc_remove_reminder),
            DateFormatter.formatDate(selectedNote!!.dateOfReminder)
        )

        val currentContext = this
        val messageDialogFragment =
            MessageDialogFragment.newInstance(
                titleResId = R.string.dialog_title_remove_reminder,
                descriptionText = descriptionMsg,
                dialogClickListener = object : DialogClickListener {
                    override fun onPositiveButtonClicked() {
                        AlarmHelper.removeAlarm(selectedNote!!, currentContext)
                        val note = selectedNote
                        note?.dateOfReminder = 0 // reset reminder
                        updateNote(note, getString(R.string.msg_reminder_removed))

                        menuRemoveReminder.isVisible = false
                        menuReminder.isVisible = true
                    }

                    override fun onNegativeButtonClicked() {
                        //TODO does nothing
                    }

                }
            )

        messageDialogFragment.show(supportFragmentManager, "")
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog.newInstance(
            datePickerListener,
            myCalendar.get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.minDate = myCalendar
        datePickerDialog.setOkText(R.string.button_ok)
        datePickerDialog.setCancelText(R.string.button_cancel)
        datePickerDialog.setOkColor(ContextCompat.getColor(this, R.color.mdtp_white))
        datePickerDialog.setCancelColor(ContextCompat.getColor(this, R.color.mdtp_white))
        datePickerDialog.show(supportFragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog.newInstance(
            timePickerListener,
            myCalendar.get(Calendar.HOUR_OF_DAY),
            myCalendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.setOkText(R.string.button_ok)
        timePickerDialog.setCancelText(R.string.button_cancel)
        timePickerDialog.setOkColor(ContextCompat.getColor(this, R.color.mdtp_white))
        timePickerDialog.setCancelColor(ContextCompat.getColor(this, R.color.mdtp_white))
        timePickerDialog.show(supportFragmentManager, TIME_PICKER_DIALOG_TAG)
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

        if (selectedNote == null) {
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
                if (::menuAdd.isInitialized) {
                    counter++
                    menuAdd.actionView.startAnimation(
                        AnimationUtils.loadAnimation(
                            application,
                            R.anim.bounce_animation
                        )
                    )
                    handler.postDelayed(this, 5000)
                    if (counter == MAX_SAVE_BTN_ANIMATIONS_COUNT) {
                        handler.removeCallbacks(this)
                    }
                }
            }
        }

        handler.postDelayed(runnable, 2000)
    }

    private fun scheduleAlarm(alarmTime: Long, note: Note) {
        note.dateOfReminder = alarmTime
        AlarmHelper.scheduleAlarm(note, this)

        updateNote(
            note,
            getString(R.string.msg_reminder_set) + " ${DateFormatter.formatDate(myCalendar.time)}"
        )
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

    private fun openHomeActivity() {
        finish()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun startShareIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, selectedNote?.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, selectedNote?.description)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share) + "..."))
    }
}