package com.kgeorgiev.notes.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.kgeorgiev.notes.App
import com.kgeorgiev.notes.R
import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.presentation.AdsManager
import com.kgeorgiev.notes.presentation.base.BaseActivity
import com.kgeorgiev.notes.presentation.di.ViewModelFactoryProvider
import com.kgeorgiev.notes.presentation.receivers.NotificationsReceiver
import com.kgeorgiev.notes.presentation.ui.adapters.NotesAdapter
import com.kgeorgiev.notes.presentation.ui.dialogs.BiometricsHelper
import com.kgeorgiev.notes.presentation.ui.onboarding.OnBoardingActivity
import com.kgeorgiev.notes.presentation.viewmodels.NotesViewModel
import kotlinx.android.synthetic.main.activity_add_note.view.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_main.*
import javax.inject.Inject

class HomeActivity : BaseActivity(), NotesAdapter.OnClickListener {
    @Inject
    lateinit var viewModelFactoryProvider: ViewModelFactoryProvider

    @Inject
    lateinit var adsManager: AdsManager

    private lateinit var notesViewModel: NotesViewModel

    private lateinit var notesAdapter: NotesAdapter

    lateinit var biometricPrompt: BiometricPrompt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        (application as App).appComponent.inject(this)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        notesViewModel =
            ViewModelProviders.of(this, viewModelFactoryProvider).get(NotesViewModel::class.java)

        notesAdapter = NotesAdapter(ArrayList(), this, this)
        rvNotesList.layoutManager = LinearLayoutManager(this)
        rvNotesList.adapter = notesAdapter

        fabAddNote.setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        if (intent.extras != null && intent.extras!!.getInt(NotificationsReceiver.NOTIFICATION_ID_PARAM) > 0) {
            handleNoteFromNotification()
        } else {
            handleNormalNote()
        }

        initViews()
    }

    override fun onStop() {
        super.onStop()

        // clearing intent data
        // this is needed when app is opened from notification
        intent.replaceExtras(null)
    }

    override fun onNoteClicked(note: Note) {
        if (note.isLocked) {
            showBiometricsDialog(note)
        } else {
            openNote(note)
        }
    }


    private fun showBiometricsDialog(noteToOpen: Note) {
        biometricPrompt = BiometricsHelper.showBiometricsPrompt(
            this,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)

                    showToastMsg(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // No impl needed for now
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)

                    openNote(noteToOpen)
                }
            })
    }

    private fun openNote(note: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("note", note)
        finish() // This is needed, because there is a bug with biometric api, and we need to destroy this activity
        //https://stackoverflow.com/questions/58286606/biometricprompt-executor-and-or-callback-was-null
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_help -> {
                openOnboardingScreen()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Handle populations notes in normal case
     */
    private fun handleNormalNote() {
        // Normal opening of the screen
        notesViewModel.getNotes().observe(this, Observer {
            it?.let {
                notesAdapter.updateValues(it as ArrayList<Note>)

                if (it.isNotEmpty()) {
                    stopEmptyStateAnimation()
                } else {
                    startEmptyStateAnimation()
                }
            }
        })
    }

    /**
     * Handle populating note whenever application is started from notification
     */
    private fun handleNoteFromNotification() {
        // When open app from notification
        val noteId = intent!!.extras!!.getInt(NotificationsReceiver.NOTIFICATION_ID_PARAM)
        notesViewModel.getNote(noteId).observe(this, Observer {
            it?.let {
                val currentNote = it[0]

                currentNote.let {
                    currentNote.dateOfReminder = 0 // Reset scheduled date
                    notesViewModel.updateNote(currentNote)
                }
                notesAdapter.updateValues(it as ArrayList<Note>)
            }
        })
    }

    private fun initViews() {
        toolbar.toolbar_title.text = getString(R.string.title_my_notes)

        fabAddNote.startAnimation(
            AnimationUtils.loadAnimation(
                this,
                R.anim.show_btn_animation
            )
        )
    }

    private fun startEmptyStateAnimation() {
        tvFirstNote.visibility = View.VISIBLE
        tvFirstNote.animation =
            AnimationUtils.loadAnimation(this, R.anim.fade_left_to_right_animation)
        lottieView.visibility = View.VISIBLE
        lottieView.playAnimation()
    }

    private fun stopEmptyStateAnimation() {
        tvFirstNote.visibility = View.GONE
        lottieView.visibility = View.GONE
        if (lottieView.isAnimating) {
            lottieView.cancelAnimation()
        }
    }

    private fun openOnboardingScreen() {
        startActivity(Intent(this, OnBoardingActivity::class.java))
    }
}
