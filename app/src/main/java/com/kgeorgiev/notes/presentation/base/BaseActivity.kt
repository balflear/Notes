package com.kgeorgiev.notes.presentation.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    protected fun showToastMsg(message: String) {
        runOnUiThread(Runnable {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        })
    }
}
