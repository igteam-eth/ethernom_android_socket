package com.ig.socket.aplication

import android.app.Application
import android.util.Log
import androidx.work.Configuration


class Application : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }
}