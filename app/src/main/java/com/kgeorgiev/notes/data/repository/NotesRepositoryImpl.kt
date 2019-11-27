package com.kgeorgiev.notes.data.repository

import com.kgeorgiev.notes.data.dao.NoteDao
import com.kgeorgiev.notes.domain.entity.Note
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Singleton
class NotesRepositoryImpl : NotesRepositroy {
    private var notesDao: NoteDao

    @Inject
    constructor(notesDao: NoteDao) {
        this.notesDao = notesDao
    }

    override suspend fun getAllNotes(): List<Note> = notesDao.getAllNotes()

    override suspend fun insertNote(note: Note) = notesDao.insert(note)

    override suspend fun deleteNote(note: Note) = notesDao.delete(note)

    override suspend fun updateNote(note: Note) = notesDao.update(note)

    override suspend fun getNote(noteId: Int): Note = notesDao.get(noteId)

    override suspend fun updateNoteReminderTime(noteId: Int, dateOfReminder: Long) =
        notesDao.updateNoteReminderTime(noteId, dateOfReminder)

    override suspend fun getScheduledNotes(): List<Note> = notesDao.getScheduledNotes()
}