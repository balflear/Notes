package com.kgeorgiev.notes.presentation.di

import com.kgeorgiev.notes.presentation.receivers.NotificationsReceiver
import com.kgeorgiev.notes.presentation.ui.activities.HomeActivity
import com.kgeorgiev.notes.presentation.ui.activities.NoteActivity
import com.kgeorgiev.notes.presentation.ui.activities.SplashScreenActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 8/22/2019.
 */

@Singleton
@Component(modules = [AppModule::class, ViewModelsModule::class, DatabaseModule::class, RepositoryModule::class])
interface AppComponent {

    fun inject(homeActivity: HomeActivity)
    fun inject(noteActivity: NoteActivity)
    fun inject(splashScreenActivity: SplashScreenActivity)
    fun inject(notesReceiver: NotificationsReceiver)
}