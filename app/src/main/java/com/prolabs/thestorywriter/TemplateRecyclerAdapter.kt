package com.prolabs.thestorywriter

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class TemplateRecyclerAdapter(var templateList:ArrayList<Template>?,var context:TemplateSelector):RecyclerView.Adapter<TemplateRecyclerAdapter.TemplateViewHolder>()
{
    var handler:Handler= Handler(Looper.getMainLooper())
    var BaseURL="https://proapplication.000webhostapp.com/TSW/templates/"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        var layoutInflater:LayoutInflater=LayoutInflater.from(parent.context)
        var view=layoutInflater.inflate(R.layout.template_adapter_layout,parent,false) as View
        return TemplateViewHolder(view)
    }


    override fun getItemCount(): Int {
        return templateList!!.size
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        val imageURL=BaseURL+ templateList!![position].templateName
        val loadImageThread: Thread= Thread {
            handler.post {
                Picasso.get()
                        .load(imageURL)
                        .placeholder(R.drawable.ic_crop_original_black_24dp)
                        .into(holder.templateImageView)
            }
        }
        loadImageThread.start()
        loadImageThread.join()
        //set on click listener

        holder.templateImageView.setOnClickListener{v->
            //
            var intent:Intent=Intent(context,EditorActivity::class.java)
            intent.putExtra("url",imageURL)
            context.startActivity(intent)
        }

    }

    class TemplateViewHolder(v: View): RecyclerView.ViewHolder(v) {
        var templateImageView:ImageView = v.findViewById(R.id.templateImageView)
    }
}