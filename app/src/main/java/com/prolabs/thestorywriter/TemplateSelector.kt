package com.prolabs.thestorywriter

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class TemplateSelector : AppCompatActivity() {
    lateinit var relativeLayout: RelativeLayout
    lateinit var animation:AnimationDrawable
    lateinit var templateRecyclerView: RecyclerView
    lateinit var waiting:ProgressBar
    @Volatile
    lateinit var standardAdapter:TemplateRecyclerAdapter
    @Volatile
    lateinit var seasonalAdapter: TemplateRecyclerAdapter
    @Volatile
    lateinit var festiveAdapter: TemplateRecyclerAdapter
    @Volatile
    lateinit var specialAdapter: TemplateRecyclerAdapter
    var client:RetrofitClient=RetrofitClient().build()

    lateinit var standardButton:Button
    lateinit var specialButton:Button
    lateinit var festiveButton: Button
    lateinit var seasonalButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_selector)
        waiting=findViewById(R.id.waiting)

        standardButton=findViewById(R.id.standardButton)
        specialButton=findViewById(R.id.specialButton)
        seasonalButton=findViewById(R.id.seasonalButton)
        festiveButton=findViewById(R.id.FestiveButton)

        relativeLayout=findViewById(R.id.templatelayout)
        animation=relativeLayout.background as AnimationDrawable
        animation.setEnterFadeDuration(2500)
        animation.setExitFadeDuration(2500)
        animation.start()

        try {
            runThreads()
            waiting.visibility= View.GONE
            templateRecyclerView = findViewById(R.id.TemplateRecyclerView)
            templateRecyclerView.layoutManager = LinearLayoutManager(this)
            templateRecyclerView.adapter = standardAdapter

            buttonFunctionsInit()
        }
        catch (e:Exception)
        {
            Toast.makeText(this,e.message,Toast.LENGTH_LONG).show()
        }
    }

    fun runThreads(){
        var standatdThread=Thread(){
            var call:Call<ArrayList<Template>> = client.fetchTemplates("standard")
            standardAdapter= TemplateRecyclerAdapter(call.execute().body(),this,"standard")
        }
        var specialThread=Thread(){
            var call:Call<ArrayList<Template>> = client.fetchTemplates("special")
            specialAdapter= TemplateRecyclerAdapter(call.execute().body(),this,"special")
        }
        var seasonalThread=Thread(){
            var call:Call<ArrayList<Template>> = client.fetchTemplates("seasonal")
            seasonalAdapter= TemplateRecyclerAdapter(call.execute().body(),this,"seasonal")
        }
        var festiveThread=Thread(){
            var call:Call<ArrayList<Template>> = client.fetchTemplates("festive")
            festiveAdapter= TemplateRecyclerAdapter(call.execute().body(),this,"festive")
        }

        standatdThread.start()
        specialThread.start()
        seasonalThread.start()
        festiveThread.start()

        standatdThread.join()
        specialThread.join()
        seasonalThread.join()
        festiveThread.join()

    }

    private fun buttonFunctionsInit(){
        standardButton.setOnClickListener(){v->
            templateRecyclerView.adapter=standardAdapter
        }
        seasonalButton.setOnClickListener(){v->
            templateRecyclerView.adapter=seasonalAdapter
        }
        specialButton.setOnClickListener(){v->
            templateRecyclerView.adapter=specialAdapter
        }
        festiveButton.setOnClickListener(){v->
            templateRecyclerView.adapter=festiveAdapter
        }
    }


}
