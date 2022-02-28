package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class listBarangReviewAdapter(private val listBarang:ArrayList<produk>):
RecyclerView.Adapter<listBarangReviewAdapter.ViewHolder>(){
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var foto = itemView.findViewById<ImageView>(R.id.imageViewFotoProdukReview)
        var nama = itemView.findViewById<TextView>(R.id.tvNamaProdukListReview)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): listBarangReviewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_review,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: listBarangReviewAdapter.ViewHolder, position: Int) {
        val con = holder.itemView.context
        Glide.with(holder.itemView.context)
            .load(listBarang[position].foto_produk[0])
            .into(holder.foto)

        holder.nama.setText(listBarang[position].nama_produk)

        holder.itemView.setOnClickListener{
            (con as listBarangReviewActivity).keDetailReview(position)
        }
    }

    override fun getItemCount(): Int {
        return listBarang.size
    }
}