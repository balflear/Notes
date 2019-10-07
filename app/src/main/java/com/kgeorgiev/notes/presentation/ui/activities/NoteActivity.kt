package com.kgeorgiev.notes.presentation.ui.activities

import android.media.MediaPlayer
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.data.entity.Note
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
        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_add_edit, menu)
        val itemDelete = menu.findItem(R.id.action_delete)
        val itemLock = menu.findItem(R.id.action_lock)
        val itemUnlock = menu.findItem(R.id.action_unlock)

        if (selectedNote == null) {// Means it's a new note case
            itemDelete.isVisible = false
            itemLock.isVisible = false
            itemUnlock.isVisible = false
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
                if (selectedNote != null) {
                    updateNote(selectedNote)
                } else {
                    insertNote()
                }
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onPositiveButtonClicked() {
        deleteNote(selectedNote!!)
    }

    override fun onNegativeButtonClicked() {
        // Does nothing for now
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
    }

    private fun initViews() {
        if (selectedNote == null) {
            toolbar.toolbar_title.text = getString(R.string.title_activity_add_note)
        } else {
            toolbar.toolbar_title.text = getString(R.string.title_activity_modify_note)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

    }
}