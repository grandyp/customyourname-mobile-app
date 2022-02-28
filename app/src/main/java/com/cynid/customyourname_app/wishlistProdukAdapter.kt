package com.cynid.customyourname_app

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class wishlistProdukAdapter(private val listProdukWishlist:ArrayList<produk>):
RecyclerView.Adapter<wishlistProdukAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var foto: ImageView = itemView.findViewById(R.id.gambarProdukCardview)
        var tvNama: TextView = itemView.findViewById(R.id.tvNamaCardview)
        var tvHarga: TextView = itemView.findViewById(R.id.tvHargaCardview)
        var btnHapus:Button= itemView.findViewById(R.id.btnHapusWishlistCardview)
        //var btnTambahkeCart:Button = itemView.findViewById(R.id.btnTambahKeranjangCardview)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_card,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProdukWishlist[position]
        val con = holder.itemView.context

        Glide.with(holder.itemView.context)
            .load(produk.foto_produk.get(0))
            .apply(RequestOptions().override(100,100))
            .into(holder.foto)

        holder.tvNama.text = produk.nama_produk

        val harga = NumberFormat.getCurrencyInstance(Locale("in","ID")).format(produk.harga_produk?.toDouble())
        holder.tvHarga.text = harga.toString()

        holder.itemView.setOnClickListener{
            var detailIntent = Intent(con,DetailProdukActivity::class.java)
            detailIntent.putExtra("extra_idproduk",produk.id_produk.toString())
            detailIntent.putExtra("extra_dari","wishlist")
            con.startActivity(detailIntent)
        }

        holder.btnHapus.setOnClickListener {
            (con as wishlistActivity).hapusWishlist(produk.id_produk.toString())
        }
        /*holder.btnTambahkeCart.setOnClickListener {
            var desain1:desain
            if(produk.desain_produk!=null){
                desain1 = produk.desain_produk!!
            }else{
                desain1 = desain()
            }
            (con as wishlistActivity).tambahkeKeranjang(
                produk.id_produk.toString(),
                produk.nama_produk.toString(),
                produk.harga_produk.toString(),
                produk.foto_produk[0],
                produk.berat_produk.toString(),
                produk.warna_produk.toString(),
                produk.panjangRantai.toString(),
                produk.custom_produk.toString(),
                produk.kategori_produk.toString(),
                desain1
            )
        }*/
    }

    override fun getItemCount(): Int {
        return listProdukWishlist.size
    }
}