package com.kgeorgiev.notes

import android.app.Application
import com.kgeorgiev.notes.presentation.di.AppComponent
import com.kgeorgiev.notes.presentation.di.AppModule
import com.kgeorgiev.notes.presentation.di.DaggerAppComponent

/**
 * Created by kostadin.georgiev on 9/17/2019.
 */
class App : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = initDagger(this)
    }

    private fun initDagger(app: App): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}