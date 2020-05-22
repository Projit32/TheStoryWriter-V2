package com.prolabs.thestorywriter

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.tomer.fadingtextview.FadingTextView
import io.realm.Realm
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var relativeLayout:RelativeLayout
    lateinit var animation:AnimationDrawable
    lateinit var proceed:Button
    lateinit var history:Button
    lateinit var fadingTextView: FadingTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        relativeLayout=findViewById(R.id.introLayout)
        proceed=findViewById(R.id.createNew)
        fadingTextView=findViewById(R.id.introText)
        history=findViewById(R.id.history)


        animation=relativeLayout.background as AnimationDrawable
        animation.setEnterFadeDuration(2000)
        animation.setExitFadeDuration(2000)
        animation.start()

        Realm.init(this)

        val texts= arrayOf<String>(
            "All you have to do is follow the damn thought.",
            "Spread memes, not legs!",
            "Thou art thy greatest inspiration"
        )
        fadingTextView.setTexts(texts)
        fadingTextView.setTimeout(5,TimeUnit.SECONDS)



        proceed.setOnClickListener{v->
            Toast.makeText(this,"Loading Templates, Please Wait...",Toast.LENGTH_SHORT).show()
            val intent=Intent(this,TemplateSelector::class.java)
            startActivity(intent)
            finish()
        }
        requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        history.setOnClickListener{v ->
            startActivity(Intent(this,PreviousActivity::class.java))
            finish()
        }


    }
}
