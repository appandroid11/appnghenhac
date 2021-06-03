package com.example.songsfirebase.fragment.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView

import com.example.songsfirebase.R
import com.squareup.picasso.Picasso


class SlideAdapter( var images:ArrayList<String>):RecyclerView.Adapter<SlideAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideAdapter.ViewHolder {
       return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pager,parent,false))
    }

  inner  class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
     val image:ImageView
     init {
         image=view.findViewById(R.id.imageView)
     }
    }

    override fun onBindViewHolder(holder: SlideAdapter.ViewHolder, position: Int) {
        Picasso.get().load(images[position]).into(holder.image)
    }

    override fun getItemCount(): Int {
     return  images.size
    }

}