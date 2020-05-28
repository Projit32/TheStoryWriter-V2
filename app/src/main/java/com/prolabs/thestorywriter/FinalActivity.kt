package com.prolabs.thestorywriter

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.squareup.picasso.Picasso
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.net.URLConnection


class FinalActivity : AppCompatActivity() {
    lateinit var imageName:String
    lateinit var finalImage:ImageView
    lateinit var shareButton:ImageButton
    lateinit var historyButton:ImageButton
    lateinit var editButton:ImageButton
    lateinit var selectTemplateButton:ImageButton
    lateinit var imageUri:Uri


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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                imageUri=getImage()
                Picasso.get()
                        .load(imageUri)
                        .placeholder(R.drawable.wait)
                        .into(finalImage)
            }
            else
            {
                finalImage.setImageDrawable(getDrawable(R.drawable.wait))
                val file=File(Environment.getExternalStorageDirectory().absolutePath+ File.separator+Environment.DIRECTORY_PICTURES+"/TSW/"+imageName)
                if (file.exists())
                {
                    finalImage.setImageBitmap(BitmapFactory.decodeFile(file.absolutePath))
                    imageUri= FileProvider.getUriForFile(
                            this,
                            applicationContext
                                    .packageName + ".provider", file)
                }
            }
            initButtonFunctions()
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }

    }

    fun initButtonFunctions(){
        shareButton.setOnClickListener(){v->
            share(imageUri)
        }
        shareButton.setOnLongClickListener(){v ->
            Toast.makeText(this,"Share Your Image",Toast.LENGTH_SHORT).show()
            true
        }
        historyButton.setOnClickListener(){v->
            startActivity(Intent(this,PreviousActivity::class.java))
            finish()
        }
        historyButton.setOnLongClickListener(){v ->
            Toast.makeText(this,"Go To History",Toast.LENGTH_SHORT).show()
            true
        }
        editButton.setOnClickListener(){v ->
            finish()
        }
        editButton.setOnLongClickListener(){v->
            Toast.makeText(this,"Back To Editing",Toast.LENGTH_SHORT).show()
            true
        }
        selectTemplateButton.setOnClickListener(){v ->
            Toast.makeText(this,"Loading Templates, Please Wait...",Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,TemplateSelector::class.java))
            finish()
        }
        selectTemplateButton.setOnLongClickListener(){v ->
            Toast.makeText(this,"Select template",Toast.LENGTH_SHORT).show()
            true
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
        cursor=contentResolver.query(uri,projections,condition, args,null)
        while (cursor!!.moveToNext())
        {
            val columnIndex=cursor.getLong(cursor.getColumnIndexOrThrow(projections[0]))
            //Toast.makeText(this,"$uri/$columnIndex",Toast.LENGTH_LONG).show()
            listOfImages.add(Uri.parse("$uri/$columnIndex"))
        }
        cursor.close()
        //Toast.makeText(this,listOfImages[0].toString(),Toast.LENGTH_LONG).show()
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
