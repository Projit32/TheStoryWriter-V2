package com.prolabs.thestorywriter

import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults

class PreviousRecyclerAdapter(var models:RealmResults<StoryModel>,var context:PreviousActivity):RecyclerView.Adapter<PreviousRecyclerAdapter.PreviousViewHolder>() {

    private val backgrounds= arrayOf(R.drawable.background,R.drawable.background2,R.drawable.background3,R.drawable.background4,
            R.drawable.background5,R.drawable.background6,R.drawable.background7)

    class PreviousViewHolder(var v:View):RecyclerView.ViewHolder(v){
        var modelDate=v.findViewById<TextView>(R.id.modelDate)
        var modelTemplate=v.findViewById<TextView>(R.id.modelTemplate)
        var modelSignature=v.findViewById<TextView>(R.id.modelSignature)
        var modelContent=v.findViewById<TextView>(R.id.modelContent)
        var modelLayout=v.findViewById<RelativeLayout>(R.id.previousAdapterLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviousViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        var view=layoutInflater.inflate(R.layout.previous_adapter_layout,parent,false)
        return PreviousViewHolder(view)
    }

    override fun getItemCount(): Int {
        return models.size
    }

    override fun onBindViewHolder(holder: PreviousViewHolder, position: Int) {
        holder.modelLayout.setBackgroundResource(backgrounds[position%backgrounds.size])
        holder.modelContent.text="Content: \n"+models[position]!!.content
        holder.modelDate.text="Date: "+models[position]!!.date
        holder.modelSignature.text="Signature: "+models[position]!!.userSignature
        var templateName=models[position]!!.template.split('/').last().split('.').first()
        holder.modelTemplate.text="Template: "+templateName
        holder.modelLayout.setOnClickListener(){v->
            var intent=Intent(context,EditorActivity::class.java)
            intent.putExtra("url",models[position]!!.template)
            intent.putExtra("Model",true)
            intent.putExtra("date",models[position]!!.date)
            context.startActivity(intent)

        }

    }
}