package com.kgeorgiev.notes.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kgeorgiev.notes.data.dao.NoteDao
import com.kgeorgiev.notes.domain.entity.Note

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Database(entities = [Note::class], version = 4, exportSchema = true)
@TypeConverters(com.kgeorgiev.notes.domain.converters.DateConverter::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        const val NOTES_DATABASE: String = "notes_database"
    }
}