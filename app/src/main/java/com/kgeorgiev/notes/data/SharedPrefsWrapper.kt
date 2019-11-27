package com.kgeorgiev.notes.data

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 10/7/2019.
 */
@Singleton
class SharedPrefsWrapper @Inject constructor(context: Context) {
    private val SHARED_PREFS_NAME = "shared_prefs_notes"
    private val prefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)


    fun saveFirstStartedApp(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun isFirstStartedApp(key: String): Boolean = prefs.getBoolean(key, true)

    companion object {
        const val FIRST_STARTED_APP_KEY = "is_app_first_started"
    }
}