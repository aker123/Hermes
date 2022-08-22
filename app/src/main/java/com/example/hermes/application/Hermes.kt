package com.example.hermes.application

import android.app.Application
import com.example.hermes.di.AppComponent

import androidx.appcompat.app.AppCompatDelegate
import com.example.hermes.di.AppModule
import com.example.hermes.di.DaggerAppComponent



class Hermes: Application() {
    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(applicationContext))
            .build()
    }

}