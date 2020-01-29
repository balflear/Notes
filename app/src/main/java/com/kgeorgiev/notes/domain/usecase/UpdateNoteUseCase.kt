package com.kgeorgiev.notes.domain.usecase

import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import javax.inject.Inject

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
class UpdateNoteUseCase @Inject constructor(private var notesRepositroy: NotesRepositroy) : BaseUseCase() {

    suspend fun invoke(note: Note) {
        super.invoke { notesRepositroy.updateNote(note) }
    }
}