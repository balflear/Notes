package com.kgeorgiev.notes.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by kostadin.georgiev on 8/28/2019.
 */
open class BaseViewModel : ViewModel() {
    /**
     * Function that executes some job at background thread using Coroutine
     */
    fun startJob(
        dispatcher: CoroutineContext? = Dispatchers.IO,
        someJob: suspend () -> Any
    ) {
        viewModelScope.launch(dispatcher!!) {
            someJob()
        }
    }
}