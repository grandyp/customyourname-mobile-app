package com.cynid.customyourname_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class homeProdukAdapter(private val listProduk:ArrayList<produk>,fragmentHome:fragment_home) :
    RecyclerView.Adapter<homeProdukAdapter.ViewHolder>() {

    var fragmentnya = fragmentHome
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var foto:ImageView = itemView.findViewById(R.id.gambarProdukHome)
        var tvNama:TextView = itemView.findViewById(R.id.namaProdukHome)
        var tvHarga:TextView = itemView.findViewById(R.id.hargaProdukHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProduk[position]
        val con = holder.itemView.context

        if(produk.foto_produk.size>0) {
            Glide.with(holder.itemView.context)
                .load(produk.foto_produk.get(produk.foto_produk.lastIndex))
                .into(holder.foto)
        }

        holder.tvNama.text = produk.nama_produk
        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val harga = numberFormat.format(produk.harga_produk?.toDouble()).toString()
        holder.tvHarga.text = harga

        holder.itemView.setOnClickListener{
            fragmentnya.keDetailProduk(produk.id_produk.toString(),"home")
        }
    }

    override fun getItemCount(): Int {
        return listProduk.size
    }
}