package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class daftarUlasanAdapter(private val listUlasan:ArrayList<ulasan>):
RecyclerView.Adapter<daftarUlasanAdapter.ViewHolder>(){
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var b1 = itemView.findViewById<ImageView>(R.id.starUlasan1)
        var b2 = itemView.findViewById<ImageView>(R.id.starUlasan2)
        var b3 = itemView.findViewById<ImageView>(R.id.starUlasan3)
        var b4 = itemView.findViewById<ImageView>(R.id.starUlasan4)
        var b5 = itemView.findViewById<ImageView>(R.id.starUlasan5)
        var komen = itemView.findViewById<TextView>(R.id.tvKomentarUlasan)
        var nama = itemView.findViewById<TextView>(R.id.tvNamaPengulas)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): daftarUlasanAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ulasan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: daftarUlasanAdapter.ViewHolder, position: Int) {
        var ulasan = listUlasan[position]
        var listBintang = arrayListOf<ImageView>(holder.b1,holder.b2,holder.b3,holder.b4,holder.b5)
        for (i in 0..ulasan.bintang!!-1){
            listBintang[i].setImageResource(R.drawable.star_full)
        }
        holder.komen.setText(ulasan.komentar)
        holder.nama.setText(ulasan.nama_pengulas)
    }

    override fun getItemCount(): Int {
        return listUlasan.size
    }
}