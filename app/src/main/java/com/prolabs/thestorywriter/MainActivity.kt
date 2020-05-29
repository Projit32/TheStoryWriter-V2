package com.prolabs.thestorywriter

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        //Request Permission
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED)
        {
            requestStoragePermission()
        }


        val texts= arrayOf<String>(
                "All you have to do is follow the damn thought.",
                "There's a reason we call it the 'WordsPorn'.",
                "Thou art thy greatest inspiration.",
                "The heart desires, the finger respires.",
                "Type your hearts out...",
                "Who the DUCK says that a meme has to be pictures only?",
                "If you can read the world, then let the world read you too.",
                "Just don't use this app a tinder for heartbroken."
        )
        fadingTextView.setTexts(texts)
        fadingTextView.setTimeout(5,TimeUnit.SECONDS)



        proceed.setOnClickListener{v->
            Toast.makeText(this,"Loading Templates, Please Wait...",Toast.LENGTH_SHORT).show()
            val intent=Intent(this,TemplateSelector::class.java)
            startActivity(intent)
            finish()
        }
        history.setOnClickListener{v ->
            startActivity(Intent(this,PreviousActivity::class.java))
            finish()
        }
    }

    private fun requestStoragePermission(){
        if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to save the stories that came from your heart into your phone.")
                    .setPositiveButton("ok") { dialog, which ->
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        }else{
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode==2)
        {
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
