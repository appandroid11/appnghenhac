package com.example.songsfirebase


import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso


class MusicListAdapter(var fonticon: Typeface, val list: ArrayList<Song>, private val listener: OnItemClickListener, var layout:Int):RecyclerView.Adapter<MusicListAdapter.ViewHolder>() {
    var selectposition:Int = -1
    fun selectposition( selectposition:Int){
        this.selectposition=selectposition
    }
    fun getposition():Int{
       return selectposition
    }
    inner class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview), View.OnClickListener {
        val title:TextView
        val image:ImageView
        val author:TextView
       val btn:Button
        val like:Button
        init {

            title=itemview.findViewById(R.id.titlesong)
            image=itemview.findViewById(R.id.imagesong)
            author=itemview.findViewById(R.id.authorsong)
            btn=itemview.findViewById(R.id.playreclye)
            like=itemview.findViewById(R.id.like)
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
        fun playrecl(position: Int)
        fun addSong(position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MusicListAdapter.ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: MusicListAdapter.ViewHolder, position: Int) {
        holder.title.text=list[position].title
        holder.btn.typeface=fonticon
        if(selectposition==position){
            holder.btn.text="\uF04C"
        }
        else{
            holder.btn.text="\uF04B"
        }
        Picasso.get().load(list[position].image).into(holder.image)
        holder.author.text=list[position].author
        holder.btn.setOnClickListener {
            listener.playrecl(position)
            if(holder.btn.text=="\uF04B"){
                holder.btn.text="\uF04C"
            }
            else{
                holder.btn.text="\uF04B"
            }

        }
        holder.like.setOnClickListener {
            listener.addSong(position)
            holder.like.text="Dislike"

        }

    }


    override fun getItemCount(): Int {
        return list.size
    }


}