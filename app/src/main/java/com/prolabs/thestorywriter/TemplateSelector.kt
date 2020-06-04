package com.prolabs.thestorywriter

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import io.realm.kotlin.where
import retrofit2.Call
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
    lateinit var favoriteAdapter:FavoriteRecyclerAdapter
    var client:RetrofitClient=RetrofitClient().build()

    lateinit var standardButton:Button
    lateinit var specialButton:Button
    lateinit var festiveButton: Button
    lateinit var seasonalButton: Button
    lateinit var realm: Realm
    lateinit var favoriteButton: ImageButton
    lateinit var deleteFavorite:ImageButton
    lateinit var titleSelectTemplate:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template_selector)
        waiting=findViewById(R.id.waiting)
        realm= RealmInit.getInstance()
        standardButton=findViewById(R.id.standardButton)
        specialButton=findViewById(R.id.specialButton)
        seasonalButton=findViewById(R.id.seasonalButton)
        festiveButton=findViewById(R.id.FestiveButton)
        favoriteButton=findViewById(R.id.getFavoriteButton)
        deleteFavorite=findViewById(R.id.deleteFavoriteButton)
        titleSelectTemplate=findViewById(R.id.titleSelectTemplate)

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
            deleteFavorite.visibility=View.GONE
            templateRecyclerView.adapter=standardAdapter
            changeTitle("Standard Templates")
        }
        seasonalButton.setOnClickListener(){v->
            deleteFavorite.visibility=View.GONE
            templateRecyclerView.adapter=seasonalAdapter
            changeTitle("Seasonal Templates")
        }
        specialButton.setOnClickListener(){v->
            deleteFavorite.visibility=View.GONE
            templateRecyclerView.adapter=specialAdapter
            changeTitle("Special Templates")
        }
        festiveButton.setOnClickListener(){v->
            deleteFavorite.visibility=View.GONE
            templateRecyclerView.adapter=festiveAdapter
            changeTitle("Festive Templates")
        }
        favoriteButton.setOnClickListener(){v->
            getFavorites()
            changeTitle("Your Favorites")
        }
        deleteFavorite.setOnClickListener(){v->
            waiting.visibility=View.VISIBLE
            realm.beginTransaction()
            val realmResult=realm.where<FavoriteModel>().findAll()
            realmResult.deleteAllFromRealm()
            realm.commitTransaction()
            Toast.makeText(this,"History Cleared",Toast.LENGTH_SHORT).show()
            getFavorites()
        }
    }

    private fun getFavorites(){
        deleteFavorite.visibility=View.VISIBLE
        waiting.visibility=View.GONE
        var favorites=ArrayList(realm.where<FavoriteModel>().findAll())
        favoriteAdapter=FavoriteRecyclerAdapter(favorites,this)
        templateRecyclerView.adapter=favoriteAdapter
    }

    private fun changeTitle(title:String){
        var animation= AnimationUtils.loadAnimation(this, R.anim.transition)
        titleSelectTemplate.startAnimation(animation)
        titleSelectTemplate.text = title
    }


}
