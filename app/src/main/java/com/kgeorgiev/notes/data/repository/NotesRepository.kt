package com.kgeorgiev.notes.data.repository

import com.kgeorgiev.notes.data.dao.NoteDao
import com.kgeorgiev.notes.data.entity.Note
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Singleton
class NotesRepository {
    private var notesDao: NoteDao

    @Inject
    constructor(notesDao: NoteDao) {
        this.notesDao = notesDao
    }

    suspend fun getAllNotes(): List<Note> = notesDao.getAllNotes()

    suspend fun insertNote(note: Note) = notesDao.insert(note)

    suspend fun deleteNote(note: Note) = notesDao.delete(note)

    suspend fun updateNote(note: Note) = notesDao.update(note)

    suspend fun getNote(noteId: Int): Note = notesDao.get(noteId)

    suspend fun updateNoteReminderTime(noteId: Int, dateOfReminder: Long) =
        notesDao.updateNoteReminderTime(noteId, dateOfReminder)
}