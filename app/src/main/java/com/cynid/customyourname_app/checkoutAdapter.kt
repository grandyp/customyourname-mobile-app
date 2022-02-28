package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class checkoutAdapter(private val listCart:ArrayList<keranjang>):
    RecyclerView.Adapter<checkoutAdapter.ViewHolder>() {
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var foto =itemView.findViewById<ImageView>(R.id.imageViewCheckout)
        var nama = itemView.findViewById<TextView>(R.id.tvNamaBarangCheckout)
        var detail = itemView.findViewById<TextView>(R.id.tvDetailProdukCheckout)
        var harga = itemView.findViewById<TextView>(R.id.tvHargaprodukCheckout)
        var jumlah = itemView.findViewById<TextView>(R.id.tvJumlahProdukCheckout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_checkout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listCart[position]
        Glide.with(holder.itemView.context)
            .load(produk.foto_produk)
            .into(holder.foto)

        holder.nama.setText(produk.nama_produk)
        var detail = produk.warna_produk
        if(produk.kategori_produk=="kalung"){
            detail = detail+", "+produk.panjang_kalung
        }
        if(produk.custom_produk=="ya"){
            detail = detail+", "+produk.desain?.nama_desain
        }
        holder.detail.setText(detail)

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val harga = numberFormat.format(produk.harga_produk?.toDouble()).toString()
        holder.harga.setText(harga)

        holder.jumlah.setText(produk.jumlah_produk.toString()+" barang")
    }

    override fun getItemCount(): Int {
        return listCart.size
    }

}