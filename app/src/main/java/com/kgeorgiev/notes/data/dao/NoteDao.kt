package com.kgeorgiev.notes.data.dao

import androidx.room.*
import com.kgeorgiev.notes.domain.entity.Note

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Dao
interface NoteDao {

    @Query("SELECT * from notes_table")
    suspend fun getAllNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM notes_table WHERE id=:noteId")
    fun get(noteId: Int): Note

    @Query("UPDATE notes_table SET reminder_date=:dateOfReminder WHERE id=:noteId")
    fun updateNoteReminderTime(noteId: Int, dateOfReminder: Long)

    @Query("SELECT * from notes_table WHERE reminder_date != '0'")
    fun getScheduledNotes(): List<Note>
}