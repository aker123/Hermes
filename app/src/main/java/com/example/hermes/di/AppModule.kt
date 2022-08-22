package com.example.hermes.di

import android.content.Context
import com.example.hermes.database.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AppModule(
    private val context: Context
) {

    @Provides
    @Singleton
    fun provideContext(): Context {
        return context
    }

    @Provides
    @Singleton
    @Named("provideDbInstance")
    fun provideDbInstance():AppDatabase{
        return AppDatabase.getAppDatabaseInstance(context)
    }

}