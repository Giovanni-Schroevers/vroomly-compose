package com.fsa_profgroep_4.vroomly

import android.app.Application
import com.fsa_profgroep_4.vroomly.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class VroomlyApp: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin{
            androidLogger()
            androidContext(this@VroomlyApp)
            modules(appModule)
        }
    }
}