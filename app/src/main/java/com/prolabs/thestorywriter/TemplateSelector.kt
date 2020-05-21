package com.prolabs.thestorywriter

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.ViewAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TemplateSelector : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var animation:AnimationDrawable
    lateinit var waitAnimationDrawable: AnimationDrawable
    lateinit var templateRecyclerView: RecyclerView
    lateinit var waiting:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_selector)
        waiting=findViewById(R.id.waiting)

        relativeLayout=findViewById(R.id.templatelayout)
        animation=relativeLayout.background as AnimationDrawable
        animation.setEnterFadeDuration(2500)
        animation.setExitFadeDuration(2500)
        animation.start()

        templateRecyclerView=findViewById(R.id.TemplateRecyclerView)
        templateRecyclerView.layoutManager= LinearLayoutManager(this)
        showImages()
    }

    public fun showImages(){
        try {
            var client:RetrofitClient = RetrofitClient()
            client.build()
            var call:Call<ArrayList<Template>> = client.fetchTemplates()
            call.enqueue(object : Callback<ArrayList<Template>> {
                override fun onFailure(call: Call<ArrayList<Template>>, t: Throwable) {
                    Toast.makeText(this@TemplateSelector,t.message,Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ArrayList<Template>>,
                    response: Response<ArrayList<Template>>
                ) {
                    waiting.visibility= View.GONE
                    var templates=response.body()
                    templateRecyclerView.adapter=TemplateRecyclerAdapter(templates,this@TemplateSelector)
                }

            })
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
        }
    }
}
