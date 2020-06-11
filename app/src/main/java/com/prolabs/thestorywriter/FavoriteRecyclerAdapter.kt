package com.prolabs.thestorywriter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import io.realm.RealmResults
import io.realm.kotlin.where

class FavoriteRecyclerAdapter(var favoriteModels: RealmResults<FavoriteModel>, var context:TemplateSelector):RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>(){

    class FavoriteViewHolder(var v: View): RecyclerView.ViewHolder(v){
        var templateImageView: ImageView = v.findViewById(R.id.templateImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        var layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        var view=layoutInflater.inflate(R.layout.template_adapter_layout,parent,false) as View
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  favoriteModels.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        Picasso.get()
                .load(favoriteModels[position]!!.url)
                .placeholder(R.drawable.ic_crop_original_black_24dp)
                .error(R.drawable.errorimage)
                .into(holder.templateImageView)

        holder.templateImageView.setOnClickListener{v->
            //
            var intent: Intent = Intent(context,EditorActivity::class.java)
            intent.putExtra("url",favoriteModels[position]!!.url)
            context.startActivity(intent)
        }

        holder.templateImageView.setOnLongClickListener(){v->
            AlertDialog.Builder(context)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure that you want to delete this?")
                .setPositiveButton("Yep!"){dialog, which ->
                    deleteFunction(position)
                }
                .setNegativeButton("Oh hell no!"){dialog, which ->
                    dialog.dismiss()
                }
                .show()
            return@setOnLongClickListener true
        }
    }
    var realm=RealmInit.getInstance()
    fun deleteFunction(pos:Int){
        realm.executeTransaction{r->
            favoriteModels.deleteFromRealm(pos)
        }
        notifyItemRemoved(pos)
    }
}