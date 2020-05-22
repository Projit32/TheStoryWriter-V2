package com.prolabs.thestorywriter

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_template_selector.*
import java.io.ByteArrayOutputStream


class FinalActivity : AppCompatActivity() {
    lateinit var imageName:String
    lateinit var finalImage:ImageView
    lateinit var shareButton:ImageButton
    lateinit var historyButton:ImageButton
    lateinit var editButton:ImageButton
    lateinit var selectTemplateButton:ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)

        finalImage=findViewById(R.id.finalImage)
        //Toolbar buttons
        editButton=findViewById(R.id.editButton)
        historyButton=findViewById(R.id.historyButton)
        selectTemplateButton=findViewById(R.id.selectTemplateButton)
        shareButton=findViewById(R.id.shareButton)

        imageName=intent.getStringExtra("image_name")!!
        try {
            Picasso.get()
                    .load(getImage())
                    .placeholder(R.drawable.wait)
                    .into(finalImage)
            initButtonFunctions()
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }

    }

    fun initButtonFunctions(){
        shareButton.setOnClickListener(){v->
            share(getImage())
        }
        historyButton.setOnClickListener(){v->
            startActivity(Intent(this,PreviousActivity::class.java))
            finish()
        }
        editButton.setOnClickListener(){v ->
            finish()
        }
        selectTemplateButton.setOnClickListener(){v ->
            Toast.makeText(this,"Loading Templates, Please Wait...",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,TemplateSelector::class.java))
            finish()
        }

    }

    @SuppressLint("Recycle")
    private fun getImage():Uri
    {
        val cursor:Cursor?
        val listOfImages=ArrayList<Uri>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val projections= arrayOf(MediaStore.Images.Media._ID,MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val condition = MediaStore.Images.ImageColumns.DISPLAY_NAME+" = ?"
        val args:Array<String> = arrayOf(imageName)
        cursor=contentResolver.query(uri,projections,condition,args,null)
        while (cursor!!.moveToNext())
        {
            val columnIndex=cursor.getLong(cursor.getColumnIndexOrThrow(projections[0]))
            listOfImages.add(Uri.parse("$uri/$columnIndex"))
        }
        cursor.close()
        return listOfImages[0]
    }

    fun share(imageURI:Uri)
    {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "image/png"
        share.putExtra(Intent.EXTRA_STREAM, imageURI)
        startActivity(Intent.createChooser(share, "Share Image"))
    }
}
