package com.prolabs.thestorywriter

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.alterac.blurkit.BlurLayout
import io.realm.Realm
import io.realm.kotlin.where

class PreviousActivity : AppCompatActivity() {
    lateinit var historyRecyclerView:RecyclerView
    lateinit var realm:Realm
    lateinit var blurBackground: BlurLayout
    lateinit var clearHistory:ImageButton

    override fun onStart() {
        super.onStart()
        blurBackground.startBlur()
    }

    override fun onDestroy() {
        blurBackground.pauseBlur()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous)
        clearHistory=findViewById(R.id.clearHistory)
        realm= RealmInit.getInstance()
        historyRecyclerView=findViewById(R.id.historyRecyclerView)
        blurBackground=findViewById(R.id.prevBlur)
        historyRecyclerView.layoutManager=LinearLayoutManager(this)

        showHistory()
    }

    fun showHistory(){
        try {

            val results = realm.where<StoryModel>().findAll()

            historyRecyclerView.adapter = PreviousRecyclerAdapter(results, this)
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }

        clearHistory.setOnClickListener(){v ->
            realm.beginTransaction()
            val realmResult=realm.where<StoryModel>().findAll()
            realmResult.deleteAllFromRealm()
            realm.commitTransaction()
            Toast.makeText(this,"History Cleared",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,TemplateSelector::class.java))
            finish()
        }
    }
}

