package com.example.songsfirebase


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class RecyclerAdapter(val list: ArrayList<Song>, private val listener: OnItemClickListener, var layout:Int):RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview), View.OnClickListener {
        val title:TextView
        val image:ImageView
        val author:TextView

    init {

          title=itemview.findViewById(R.id.titlesong)
          image=itemview.findViewById(R.id.imagesong)
          author=itemview.findViewById(R.id.authorsong)

          itemview.setOnClickListener(this)

    }

        override fun onClick(v: View?) {
            val position=absoluteAdapterPosition
            if(position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.ViewHolder {
       val v=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int) {
       holder.title.text=list[position].title
       Picasso.get().load(list[position].image).into(holder.image)
        holder.author.text=list[position].author


    }

    override fun getItemCount(): Int {
       return list.size
    }

}