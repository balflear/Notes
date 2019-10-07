package com.kgeorgiev.notes.domain

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor


/**
 * Created by kostadin.georgiev on 10/3/2019.
 */
class MainThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())

    override fun execute(r: Runnable) {
        handler.post(r)
    }
}