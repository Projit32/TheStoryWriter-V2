package com.prolabs.thestorywriter

import io.realm.RealmObject

open class StoryModel(
        var date:String="",
        var global_x:Float=0.0f,
        var global_y: Float=0.0f,
        var content:String="",
        var userSignature:String="",
        var usedFont:String?="",
        var usedSize:Float=0.0f,
        var template:String=""
):RealmObject()