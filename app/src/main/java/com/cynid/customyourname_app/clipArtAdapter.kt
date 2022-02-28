package com.cynid.customyourname_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class clipArtAdapter(private val listClipart: ArrayList<Int>,val context:Context):
    RecyclerView.Adapter<clipArtAdapter.ViewHolder>() {

    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        var foto: ImageView = itemView.findViewById(R.id.imageviewClipart)
        var mcontext = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.data_clipart,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val clipart = listClipart[position]
        holder.foto.setImageResource(clipart)
        holder.foto.setOnClickListener {
            (context as desainClipartActivity).inputClipart(position)
        }
    }

    override fun getItemCount(): Int {
        return listClipart.size
    }
}