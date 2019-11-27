package com.kgeorgiev.notes.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
abstract class BaseUseCase {
    suspend fun invoke(someJob: suspend () -> Any): Any {
        return withContext(Dispatchers.IO) {
            someJob()
        }
    }
}