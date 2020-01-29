package com.kgeorgiev.notes.presentation.di

import com.kgeorgiev.notes.data.dao.NoteDao
import com.kgeorgiev.notes.data.repository.NotesRepositoryImpl
import com.kgeorgiev.notes.domain.repository.NotesRepositroy
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 11/27/2019.
 */
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideNotesRepository(notesDao: NoteDao): NotesRepositroy = NotesRepositoryImpl(notesDao)
}