package com.prolabs.thestorywriter

import android.app.Application
import io.realm.Realm

open class TheStoryWriter: Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}