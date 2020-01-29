package com.kgeorgiev.notes.domain.repository

import com.kgeorgiev.notes.domain.entity.Note

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
interface NotesRepositroy {
    suspend fun getAllNotes(): List<Note>

    suspend fun insertNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun getNote(noteId: Int): Note

    suspend fun updateNoteReminderTime(noteId: Int, dateOfReminder: Long)

    suspend fun getScheduledNotes(): List<Note>
}
