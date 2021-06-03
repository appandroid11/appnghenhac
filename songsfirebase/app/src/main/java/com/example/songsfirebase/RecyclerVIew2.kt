package com.example.songsfirebase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.songsfirebase.R
import com.example.songsfirebase.Song
import com.squareup.picasso.Picasso


class RecyclerVIew2(val list: ArrayList<Song>, private val listener: OnItemClickListener, var layout:Int):RecyclerView.Adapter<RecyclerVIew2.ViewHolder>() {
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
                listener.onItemClick2(position)
            }
        }
    }
    interface OnItemClickListener {
        fun onItemClick2(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerVIew2.ViewHolder {
        val v=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerVIew2.ViewHolder, position: Int) {
        holder.title.text=list[position].title
        Picasso.get().load(list[position].image).into(holder.image)
        holder.author.text=list[position].author


    }

    override fun getItemCount(): Int {
        return list.size
    }

}