package com.prolabs.thestorywriter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FontRecyclerAdapter(var fontList:ArrayList<String>,var context:EditorActivity): RecyclerView.Adapter<FontRecyclerAdapter.FontViewHolder>() {

    private val backgrounds= arrayOf(R.drawable.background,R.drawable.background2,R.drawable.background3,R.drawable.background4,
            R.drawable.background5,R.drawable.background6,R.drawable.background7)

    class FontViewHolder(var v: View):RecyclerView.ViewHolder(v){
        var textView:TextView=v.findViewById(R.id.fontAdapterLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FontViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val view=layoutInflater.inflate(R.layout.font_adapter_layout,parent,false)
        return FontViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fontList.size
    }

    override fun onBindViewHolder(holder: FontViewHolder, position: Int) {
        val typeface=Typeface.createFromAsset(context.assets,"fonts/"+ fontList[position])
        var displayString=fontList[position].replace('-',' ').split('.')[0]
        holder.textView.text=displayString
        holder.textView.typeface=typeface
        holder.textView.setBackgroundResource(backgrounds[position%backgrounds.size])
        holder.textView.setOnClickListener{v->
            context.usedFont=fontList[position]
            context.fontRecyclerView.visibility=View.GONE
            context.blurBackground.visibility=View.GONE
            context.draw()
        }
    }
}