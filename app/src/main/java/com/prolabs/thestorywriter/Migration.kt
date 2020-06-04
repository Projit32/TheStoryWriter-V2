package com.prolabs.thestorywriter

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration

class Migration: RealmMigration {
    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion=oldVersion
        var schema=realm.schema


        /*
        //version 0
        Story Model
        date
        global x
        global y
        content
        url
        font
        size
        sign

        //version 1
        + Favorite Model
        url

         */
        if (oldVersion==0L)
        {
            schema.create("FavoriteModel")
                    .addField("url",String::class.java, FieldAttribute.REQUIRED)
            oldVersion++
        }


    }

    override fun hashCode(): Int {
        return 37
    }

    override fun equals(other: Any?): Boolean {
        return (other is Migration)
    }
}