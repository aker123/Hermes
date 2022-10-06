package com.example.hermes.application

import android.app.Application
import com.example.hermes.di.AppComponent

import androidx.appcompat.app.AppCompatDelegate
import com.example.hermes.di.AppModule
import com.example.hermes.di.DaggerAppComponent
import com.yandex.mapkit.MapKitFactory


class Hermes: Application() {
    var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("f066846e-4ee8-49e8-ada2-822d968ebbf1")

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(applicationContext))
            .build()
    }

}