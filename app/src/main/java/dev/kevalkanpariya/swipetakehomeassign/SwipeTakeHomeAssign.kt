package dev.kevalkanpariya.swipetakehomeassign

import android.app.Application
import dev.kevalkanpariya.swipetakehomeassign.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SwipeTakeHomeAssign: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SwipeTakeHomeAssign)
            androidLogger()
            modules(appModule)
        }
    }
}