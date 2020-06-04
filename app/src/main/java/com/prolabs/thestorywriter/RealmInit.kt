package com.prolabs.thestorywriter

import io.realm.Realm
import io.realm.RealmConfiguration




class RealmInit {
    companion object{
        @JvmStatic
        fun getInstance(): Realm
        {
            var config= RealmConfiguration.Builder()
                    .schemaVersion(1)
                    .migration(Migration())
                    .build()

            return  Realm.getInstance(config)
        }
    }
}