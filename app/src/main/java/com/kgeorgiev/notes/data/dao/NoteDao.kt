package com.kgeorgiev.notes.data.dao

import androidx.room.*
import com.kgeorgiev.notes.data.entity.Note

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
}