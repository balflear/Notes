package com.kgeorgiev.notes.presentation.di

import android.content.Context
import com.kgeorgiev.notes.data.SharedPrefsWrapper
import com.kgeorgiev.notes.presentation.AdsManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by kostadin.georgiev on 8/22/2019.
 */

@Module
class AppModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun provideContext(): Context = appContext

    @Provides
    @Singleton
    fun provideSharedPrefsWrapper(context: Context): SharedPrefsWrapper {
        return SharedPrefsWrapper(context)
    }

    @Provides
    @Singleton
    fun provideAdsManager(appContext: Context): AdsManager {
        return AdsManager(appContext)
    }
}