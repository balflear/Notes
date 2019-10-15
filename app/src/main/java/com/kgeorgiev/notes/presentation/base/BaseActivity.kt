package com.kgeorgiev.notes.presentation.base

import android.media.MediaPlayer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kgeorgiev.notes.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    protected fun showToastMsg(message: String) {
        runOnUiThread(Runnable {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        })
    }

    protected fun playSuccessSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.completed)
        mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            mediaPlayer.start()
        })
    }
}
