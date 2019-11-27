package com.kgeorgiev.notes.domain.usecase

import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
class InsertNoteUseCase @Inject constructor(private var notesRepositroy: NotesRepositroy) {

    suspend fun invoke(note: Note) {
        withContext(Dispatchers.IO) {
            notesRepositroy.insertNote(note)
        }
    }
}