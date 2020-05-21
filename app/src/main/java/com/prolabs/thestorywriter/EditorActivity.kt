package com.prolabs.thestorywriter

import android.content.ContentValues
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.set
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import io.alterac.blurkit.BlurLayout
import io.realm.Realm
import io.realm.exceptions.RealmException
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_editor.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class EditorActivity : AppCompatActivity() {
    lateinit var url:String
    @Volatile
    lateinit var vanillaImage:Bitmap
    lateinit var verticalSeekBar: SeekBar
    lateinit var horizontalSeekBar: SeekBar
    lateinit var textSizeSeekBar:SeekBar
    lateinit var verticalAdjustment:ImageButton
    lateinit var horizontalAdjustment:ImageButton
    lateinit var fontSelect:ImageButton
    lateinit var signature:ImageButton
    lateinit var textSize:ImageButton
    lateinit var save:ImageButton
    lateinit var blurBackground:BlurLayout
    lateinit var editedImage:ImageView
    lateinit var contentLayout:RelativeLayout
    var content:String="This is a Sample Text, Tap on the image"
    lateinit var editContent:EditText
    lateinit var contentSubmit:Button
    var global_x:Float=100f
    var global_y:Float=150f
    lateinit var finalImage:Bitmap
    lateinit var date:Date
    lateinit var fonts:ArrayList<String>
    var usedFont:String?=null
    lateinit var fontRecyclerView: RecyclerView
    var usedSize:Float=40F
    var userSignature:String=""
    lateinit var realm: Realm
    var loadModel: StoryModel?=null
    var isModel:Boolean=false

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
        setContentView(R.layout.activity_editor)
        //Realm
        realm= Realm.getDefaultInstance()
        //IntentExtras
        url=intent.getStringExtra("url")!!
        isModel=intent.getBooleanExtra("Model",false)
        //Main ImageView
        editedImage=findViewById(R.id.editedImage)
        //Toolbar items
        verticalAdjustment=findViewById(R.id.verticalAdjustment)
        horizontalAdjustment=findViewById(R.id.horizontalAdjustment)
        fontSelect=findViewById(R.id.fontSelect)
        save=findViewById(R.id.save)
        signature=findViewById(R.id.signature)
        textSize=findViewById(R.id.textSize)
        //SeekBars
        verticalSeekBar=findViewById(R.id.verticalAdjustBar)
        horizontalSeekBar=findViewById(R.id.horizontalAdjustBar)
        textSizeSeekBar=findViewById(R.id.textSizeAdjust)
        textSizeSeekBar.setOnSeekBarChangeListener(textSizeSeekChangeListener)
        //Content
        contentLayout=findViewById(R.id.contentLayout)
        editContent=findViewById(R.id.editContent)
        contentSubmit=findViewById(R.id.contentSubmit)
        //Blur Layout
        blurBackground=findViewById(R.id.blurbackground)
        //Recycler View
        fontRecyclerView=findViewById(R.id.fontReCyclerView)
        fontRecyclerView.layoutManager= LinearLayoutManager(this)

        fetchFontsFromAsset()
        fontRecyclerView.adapter=FontRecyclerAdapter(fonts,this)
        getImage()

    }

    fun getImage(){
        Picasso.get()
                .load(url)
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .error(R.drawable.errorimage)
                .into(target)
        editedImage.tag = target
    }

    var target:Target=object :Target{
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            editedImage.setImageDrawable(placeHolderDrawable)
        }

        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
            editedImage.setImageDrawable(errorDrawable)
            Toast.makeText(this@EditorActivity,e!!.message,Toast.LENGTH_SHORT).show()
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            vanillaImage=bitmap!!
            finalImage=vanillaImage.copy(vanillaImage.config,true)
            editedImage.setImageBitmap(vanillaImage)
            verticalSeekBar.max=vanillaImage.width
            horizontalSeekBar.max=vanillaImage.height
            FunctionsInit()
            if (isModel)
            {
                //Toast.makeText(this@EditorActivity,"got class",Toast.LENGTH_SHORT).show()
                loadModel=realm.where<StoryModel>().equalTo("date",intent.getStringExtra("date")!!).findFirst()
                loadFromModel(loadModel!!)
            }
        }
    }
    var verticalSeekChangeListener=object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            global_y=progress.toFloat()
            draw()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    var horizontalSeekChangeLister= object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            global_x=progress.toFloat()
            draw()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }
    var textSizeSeekChangeListener=object:SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            usedSize= progress.toFloat()
            draw()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    }

    fun loadFromModel(model:StoryModel){
        try {
            this.content=model.content
            this.global_y=model.global_y
            this.global_x=model.global_x
            this.usedSize=model.usedSize
            this.usedFont=model.usedFont
            this.userSignature=model.userSignature
            editContent.setText(model.content)
            draw()
        }
        catch (e:Exception)
        {
            Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
        }

    }

    fun FunctionsInit(){
        editedImage.setOnClickListener(){v ->
            blurBackground.visibility=View.VISIBLE
            contentLayout.visibility=View.VISIBLE
        }
        contentSubmit.setOnClickListener(){v ->
            try {
                content = if (editContent.text==null){
                    ""
                } else {
                    editContent.text.toString()
                }

                contentLayout.visibility=View.GONE
                blurBackground.visibility=View.GONE
                draw()
            }
            catch (e:Exception)
            {
                Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
            }

        }
        verticalSeekBar.setOnSeekBarChangeListener(verticalSeekChangeListener)
        horizontalSeekBar.setOnSeekBarChangeListener(horizontalSeekChangeLister)

        verticalAdjustment.setOnClickListener{v ->
            horizontalSeekBar.visibility=View.GONE
            if (verticalSeekBar.visibility==View.GONE){
                verticalSeekBar.visibility=View.VISIBLE
            }
            else{
                verticalSeekBar.visibility=View.GONE
            }
        }

        horizontalAdjustment.setOnClickListener{v ->
            verticalSeekBar.visibility=View.GONE
            if (horizontalSeekBar.visibility==View.GONE){
                horizontalSeekBar.visibility=View.VISIBLE
            }
            else{
                horizontalSeekBar.visibility=View.GONE
            }
        }

        fontSelect.setOnClickListener(){v ->
            try {
                blurBackground.visibility=View.VISIBLE
                fontRecyclerView.visibility=View.VISIBLE
            }
            catch (e:Exception)
            {
                Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
            }

        }

        save.setOnClickListener(){v ->
            saveImage()
        }

        signature.setOnClickListener(){v->
            blurBackground.visibility=View.VISIBLE
            val alertDialog=AlertDialog.Builder(this)
            val alertView=layoutInflater.inflate(R.layout.signature_layout,null)
            alertDialog.setView(alertView)
            alertDialog.setCancelable(false)
            alertDialog.setPositiveButton("Done") { dialog, which ->
                userSignature=alertView.findViewById<EditText>(R.id.signatureName).text.toString()
                userSignature="By: @$userSignature"
                blurBackground.visibility=View.GONE
                draw()
            }
            alertDialog.setNegativeButton("Cancel"){dialog, which ->
                blurbackground.visibility=View.GONE
                dialog.dismiss()
            }
            alertDialog.show()

        }

        textSize.setOnClickListener(){v->
            textSizeSeekBar.visibility=if (textSizeSeekBar.visibility==View.GONE) View.VISIBLE else View.GONE
        }
    }

    fun addSignatureToImage(userSignature:String){
        if (userSignature==""){return}
        try {
            finalImage = finalImage.copy(finalImage.config, true)
            var canvas: Canvas = Canvas(finalImage)
            val paint = Paint()
            paint.setColor(Color.WHITE)
            paint.textSize = 30f
            val bounds: Rect = Rect()
            val local_y = finalImage.height*0.98f
            val local_x = finalImage.width*0.65f
            if (usedFont!=null)
            {
                val typeface:Typeface=Typeface.createFromAsset(assets, "fonts/$usedFont")
                paint.typeface=typeface
            }
                paint.getTextBounds(userSignature, 0, userSignature.length, bounds)
                canvas.drawText(userSignature, local_x, local_y, paint)
            editedImage.setImageBitmap(finalImage)
        }
        catch (e:Exception)
        {
            Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImage(){
        val contentResolver=contentResolver
        var outputStream:OutputStream?=null
        var uri:Uri?=null
        try {
            date = Date()
            val fileName: String = "TSW_" + SimpleDateFormat("yyyy_mm_dd_HH_mm_ss", Locale.getDefault()).format(date).toString()
            var filePath = Environment.DIRECTORY_PICTURES + "/TSW/"
            val contentValues=ContentValues()
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, filePath)
                val contentUri:Uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                uri = contentResolver.insert(contentUri, contentValues)

                if (uri == null)
                {
                    throw Exception("Failed to create new MediaStore record.");
                }

                outputStream = contentResolver.openOutputStream(uri);

                if (outputStream == null)
                {
                    throw  Exception("Failed to get output stream.");
                }
            }
            else{
                filePath=Environment.getExternalStorageDirectory().absolutePath+File.separator+Environment.DIRECTORY_PICTURES+"/TSW/"
                val tempFile=File(filePath)
                if(!tempFile.exists())
                    tempFile.mkdirs()
                outputStream=FileOutputStream(File(tempFile, "$fileName.png"))
            }


            if (!finalImage.compress(Bitmap.CompressFormat.PNG,100 , outputStream))
            {
                throw  Exception("Failed to save bitmap.");
            }

            Toast.makeText(this@EditorActivity,"Saved",Toast.LENGTH_SHORT).show()
            outputStream.flush()
            outputStream.close()

            saveToRealm()
            val intent:Intent= Intent(this,FinalActivity::class.java)
            intent.putExtra("image_name","$fileName.png")
            startActivity(intent)
        }
        catch (e:Exception)
        {
            Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
            if (uri != null)
            {
                // Don't leave an orphan entry in the MediaStore
                contentResolver.delete(uri, null, null);
            }

        }
        finally
        {
            outputStream?.close()
        }
    }
    fun getFormattedContent(text:String):ArrayList<String>{
        var texts=ArrayList(text.split('\n'))
        //Toast.makeText(this@EditorActivity,texts.size.toString(),Toast.LENGTH_SHORT).show()
        return texts
    }
    fun draw(){
        try {
            finalImage = vanillaImage.copy(vanillaImage.config, true)
            var canvas: Canvas = Canvas(finalImage)
            val paint = Paint()
            paint.color = Color.WHITE
            paint.textSize = usedSize
            val textSegment = getFormattedContent(content)
            val bounds: Rect = Rect()
            var local_y = global_y
            var local_x = global_x
            if (usedFont!=null)
            {
                val typeface:Typeface=Typeface.createFromAsset(assets, "fonts/$usedFont")
                paint.typeface=typeface
            }
            for (part in textSegment) {
                paint.getTextBounds(part, 0, part.length, bounds)
                canvas.drawText(part, local_x, local_y, paint)
                local_y += usedSize/0.7f
            }
            editedImage.setImageBitmap(finalImage)
        }
        catch (e:Exception)
        {
            Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
        }
        addSignatureToImage(userSignature)
    }

    fun fetchFontsFromAsset(){
        try {
            fonts = assets.list("fonts")!!.toCollection(ArrayList())
        }
        catch (e:Exception) {
            Toast.makeText(this@EditorActivity,e.message,Toast.LENGTH_SHORT).show()
        }

    }

    fun saveToRealm(){
        realm.beginTransaction()
        var tempModel=realm.createObject<StoryModel>()
        tempModel.date=LocalDateTime.now().toString()
        tempModel.template=this.url
        tempModel.global_y=this.global_y
        tempModel.global_x=this.global_x
        tempModel.content=this.content
        tempModel.userSignature=this.userSignature
        tempModel.usedSize=this.usedSize
        tempModel.usedFont=this.usedFont
        realm.commitTransaction()
    }

}