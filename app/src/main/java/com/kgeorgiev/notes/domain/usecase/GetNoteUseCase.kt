package com.kgeorgiev.notes.domain.usecase

import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
class GetNoteUseCase @Inject constructor(private val notesRepositroy: NotesRepositroy) : BaseUseCase() {
    suspend fun invoke(noteId: Int): Note {
        return super.invoke { notesRepositroy.getNote(noteId) } as Note
    }
}