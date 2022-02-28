package com.cynid.customyourname_app

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class cartProdukAdapter(private val listProdukCart:ArrayList<keranjang>,fragmentCart: fragment_cart):
    RecyclerView.Adapter<cartProdukAdapter.ViewHolder>() {
    var fragmentnya = fragmentCart
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var foto: ImageView = itemView.findViewById(R.id.gambarProdukCart)
        var tvNama: TextView = itemView.findViewById(R.id.tvNamaCart)
        var tvHarga: TextView = itemView.findViewById(R.id.tvHargaCart)
        var btnHapusCart: Button = itemView.findViewById(R.id.btnHapusCart)
        var btnTambahJumlah: Button = itemView.findViewById(R.id.btnTambahJumlahCart)
        var btnKurangiJumlah : Button = itemView.findViewById(R.id.btnKurangJumlahCart)
        var jumlahCart:EditText = itemView.findViewById(R.id.editTextJumlahCart)
        var tvDetailCart:TextView=itemView.findViewById(R.id.tvDetailProdukCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produk_cart,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = listProdukCart[position]
        val con = holder.itemView.context

        Glide.with(holder.itemView.context)
            .load(produk.foto_produk)
            .apply(RequestOptions().override(100,100))
            .into(holder.foto)

        holder.tvNama.text = produk.nama_produk

        val localeID =  Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        val harga = numberFormat.format(produk.harga_produk?.toDouble()).toString()
        holder.tvHarga.text = harga

        //detail produk(warna, panjang kalung, desain)
        var detail = produk.warna_produk
        if(produk.kategori_produk=="kalung"){
            detail = detail+", "+produk.panjang_kalung
        }
        if(produk.custom_produk=="ya"){
            detail = detail+", "+produk.desain?.nama_desain
        }
        holder.tvDetailCart.text = detail

        var jumlahBarang = produk.jumlah_produk?.toInt()
        holder.jumlahCart.setText(jumlahBarang.toString())

        holder.itemView.setOnClickListener{
            var detailIntent = Intent(con,DetailProdukActivity::class.java)
            detailIntent.putExtra("extra_idproduk",produk.id_produk)
            detailIntent.putExtra("extra_dari","cart")
            con.startActivity(detailIntent)
        }

        //acek jumlah barang, kalau 1 disable tombol kurang
        if(jumlahBarang!! >1){
            holder.btnKurangiJumlah.isEnabled = true
        }
        else{
            holder.btnKurangiJumlah.isEnabled = false
        }

        //tombol akan true ketika tombol hapus cart, tambah dan kurangi cart dipencet
        var tombol = false

        holder.btnHapusCart.setOnClickListener {
            fragmentnya.hapusCart(produk.id_cart.toString())
            tombol = true
        }

        holder.btnTambahJumlah.setOnClickListener {
            tombol = true
            jumlahBarang = produk.jumlah_produk?.plus(1)
            produk.jumlah_produk = jumlahBarang
            holder.jumlahCart.setText(jumlahBarang.toString())
            fragmentnya.gantiJumlah(produk.id_cart.toString(), jumlahBarang!!)
            if(jumlahBarang!!>1){
                holder.btnKurangiJumlah.isEnabled = true
            }
        }

        holder.btnKurangiJumlah.setOnClickListener {
            tombol = true
            jumlahBarang = produk.jumlah_produk?.minus(1)
            produk.jumlah_produk = jumlahBarang
            holder.jumlahCart.setText(jumlahBarang.toString())
            fragmentnya.gantiJumlah(produk.id_cart.toString(),jumlahBarang!!)
            if(jumlahBarang!!<=1){
                holder.btnKurangiJumlah.isEnabled = false
            }
        }

        holder.jumlahCart.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(tombol == false){
                    if(p0.toString()!=""){
                        if(p0.toString().toInt()<1){
                            holder.jumlahCart.setText("1")
                            fragmentnya.gantiJumlah(produk.id_cart.toString(),1)
                            produk.jumlah_produk = 1
                        }
                        else{
                            var jumlah = p0.toString().toInt()
                            fragmentnya.gantiJumlah(produk.id_cart.toString(),jumlah)
                            produk.jumlah_produk = jumlah
                        }
                    }
                    else{
                        holder.jumlahCart.setText("1")
                        fragmentnya.gantiJumlah(produk.id_cart.toString(),1)
                        produk.jumlah_produk = 1
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                tombol = false
            }

        })

    }

    override fun getItemCount(): Int {
        return listProdukCart.size
    }
}