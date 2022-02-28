package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class daftarPesananAdapter(private val listPesanan:ArrayList<pesanan>):
RecyclerView.Adapter<daftarPesananAdapter.ViewHolder>(){
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var foto = itemView.findViewById<ImageView>(R.id.imageViewItemDaftarPesanan)
        var nama = itemView.findViewById<TextView>(R.id.tvNamaProdukPertamaDaftarPesanan)
        var tgl = itemView.findViewById<TextView>(R.id.tvTanggalDaftarPEsanan)
        var status = itemView.findViewById<TextView>(R.id.tvStatusDaftarPesanan)
        var totalharga = itemView.findViewById<TextView>(R.id.tvTotalBelanjaDaftarPesanan)
        var jumlahsisa = itemView.findViewById<TextView>(R.id.tvJumlahProdukSisaPesanan)
        var detail = itemView.findViewById<TextView>(R.id.tvDetailDaftarPesanan)
        var tvTotal = itemView.findViewById<TextView>(R.id.tvKeteranganTotalBelanja)
        var btnReview = itemView.findViewById<Button>(R.id.btnBeriUlasanPesanan)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): daftarPesananAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pesanan,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: daftarPesananAdapter.ViewHolder, position: Int) {
        val con = holder.itemView.context
        val pesanan = listPesanan[position]
        holder.btnReview.visibility = View.GONE

        holder.itemView.setOnClickListener {
            (con as daftarPesananActivity).keDetailPesanan(pesanan)
        }

        if(pesanan.status_pesanan=="baru"){
            holder.tvTotal.setText("Perkiraan Total Belanja")
        }
        Glide.with(holder.itemView.context)
            .load(pesanan.daftar_barang!![0].foto_produk)
            .into(holder.foto)

        val jumlah = (pesanan.daftar_barang!!.size).toInt()-1
        if(jumlah>0) {
            holder.jumlahsisa.setText("+ " + jumlah + " produk lainnya")
        }else{
            holder.jumlahsisa.visibility = View.GONE
        }

        holder.nama.setText(
            pesanan.daftar_barang!![0].nama_produk
        )

        holder.tgl.setText(pesanan.tgl_pemesanan)

        var status = ""
        if(pesanan.status_pesanan=="baru"){
            status = "Menunggu Konfirmasi"
        }
        else{
            status = pesanan.status_pesanan.toString()
        }
        holder.status.setText(status)

        var total = pesanan.total_harga!!.toInt()+pesanan.total_ongkir!!.toInt()
        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val harga = numberFormat.format(total.toDouble()).toString()
        holder.totalharga.setText(harga)

        var detail = pesanan.daftar_barang!![0].warna_produk
        if(pesanan.daftar_barang!![0].kategori_produk=="kalung"){
            detail = detail+", "+pesanan.daftar_barang!![0].panjang_kalung
        }
        if(pesanan.daftar_barang!![0].custom_produk=="ya"){
            detail = detail + ", "+pesanan.daftar_barang!![0].desain!!.nama_desain
        }
        holder.detail.setText(detail)

        //tombol kasi ulasan
        if(pesanan.status_pesanan=="selesai"){
            holder.btnReview.visibility = View.VISIBLE
            holder.btnReview.setOnClickListener {
                (con as daftarPesananActivity).keDaftarReview()
            }
        }
    }

    override fun getItemCount(): Int {
       return listPesanan.size
    }
}