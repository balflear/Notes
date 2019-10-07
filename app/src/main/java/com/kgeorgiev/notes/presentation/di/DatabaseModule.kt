package com.kgeorgiev.notes.presentation.di

import android.content.Context
import androidx.room.Room
import com.kgeorgiev.notes.data.NotesDatabase
import com.kgeorgiev.notes.data.dao.NoteDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun providesNotesDatabase(context: Context): NotesDatabase {
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java, NotesDatabase.NOTES_DATABASE
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun providesNotesDao(notesDatabase: NotesDatabase): NoteDao {
        return notesDatabase.noteDao()
    }
}