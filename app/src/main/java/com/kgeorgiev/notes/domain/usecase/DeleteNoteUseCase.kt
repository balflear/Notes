package com.kgeorgiev.notes.domain.usecase

import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
class DeleteNoteUseCase @Inject constructor(private val notesRepositroy: NotesRepositroy) : BaseUseCase() {
    suspend fun invoke(note: Note) {
        return super.invoke { notesRepositroy.deleteNote(note) } as Unit
    }
}