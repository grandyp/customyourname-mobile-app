package com.cynid.customyourname_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_cart.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_cart.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_cart : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var listProduk:ArrayList<keranjang> = arrayListOf()
    //lateinit var listProdukAdapter:cartProdukAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(R.drawable.animasiloading).into(loadingCart)

        val currentUser = Firebase.auth.currentUser
        tvTidakadaCart.visibility = View.INVISIBLE

        btnWishlistCart.setOnClickListener {
            var parent:MainActivity= activity as MainActivity
            parent.keWishlist("cart")
        }

        rvCart.setHasFixedSize(true)

        //checkout
        btnBeliCart.setOnClickListener {
            if(listProduk.size>0) {
                val keCheckoutIntent = Intent(context, checkoutActivity::class.java)
                keCheckoutIntent.putParcelableArrayListExtra("extra_listCart", listProduk)
                startActivity(keCheckoutIntent)
            }else{
                Toast.makeText(context, "Belum ada barang di keranjang", Toast.LENGTH_SHORT).show()
            }
        }


        //ambil data dari firebase
        var database = Firebase.database.reference
        listProduk.clear()
        var total = 0
        var ref = database.child("user").child(currentUser?.uid.toString()).child("cart")
        try {
            ref.get().addOnSuccessListener {
                if (it.value != null) {
                    for (i in it.children) {
                        var produk1: keranjang = keranjang(
                            id_produk = i.child("id_produk").value.toString(),
                            nama_produk = i.child("nama_produk").value.toString(),
                            harga_produk = i.child("harga_produk").value.toString(),
                            foto_produk = i.child("foto_produk").value.toString(),
                            jumlah_produk = i.child("jumlah_produk").value.toString().toInt(),
                            berat_produk = i.child("berat_produk").value.toString(),
                            warna_produk = i.child("warna_produk").value.toString(),
                            kategori_produk = i.child("kategori_produk").value.toString(),
                            custom_produk = i.child("custom_produk").value.toString(),
                            id_cart = i.key.toString()
                        )
                        if (produk1.kategori_produk == "kalung") {
                            produk1.panjang_kalung = i.child("panjang_kalung").value.toString()
                        }
                        if (produk1.custom_produk == "ya") {
                            //masukkan data desain
                            produk1.id_desain = i.child("id_desain").value.toString()
                            produk1.desain = desain(
                                nama_desain = i.child("desain/nama_desain").value.toString(),
                                jenis = i.child("desain/jenis").value.toString()
                            )
                            if (produk1.desain?.jenis == "tulisan") {
                                val tulisan = i.child("desain").child("tulisan").value.toString()
                                val font = i.child("desain").child("font").value.toString()
                                val url = i.child("desain").child("url_gambar").value.toString()
                                produk1.desain?.tulisan = tulisan
                                produk1.desain?.url_gambar = url
                                produk1.desain?.font = font
                            } else if (produk1.desain?.jenis == "clipart") {
                                var dataListClipart =
                                    i.child("desain").child("list_clipart").children
                                var dataListUrlClipart =
                                    i.child("desain").child("list_url_clipart").children
                                var listClipart = arrayListOf<String>()
                                var listUrlClipart = arrayListOf<String>()
                                for (clip in dataListClipart) {
                                    listClipart.add(clip.value.toString())
                                }
                                for (clip in dataListUrlClipart) {
                                    listUrlClipart.add(clip.value.toString())
                                }
                                produk1.desain?.list_url_clipart = listUrlClipart
                                produk1.desain?.list_clipart = listClipart
                            } else {
                                //jenis = upload gambar
                                val urlgambar =
                                    i.child("desain").child("url_gambar").value.toString()
                                produk1.desain?.url_gambar = urlgambar
                            }
                        }
                        val harga = produk1.harga_produk?.toInt()
                        val jumlah = produk1.jumlah_produk?.toInt()
                        total = total + (harga!! * jumlah!!)
                        listProduk.add(produk1)
                    }
                    val hargatotal = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                        .format(total.toDouble()).toString()
                    tvTotalhargaCart.setText(hargatotal)
                    rvCart.layoutManager = LinearLayoutManager(context)
                    var listProdukAdapter = cartProdukAdapter(listProduk, this)
                    rvCart.adapter = listProdukAdapter
                } else {
                    //cart kosong
                    tvTidakadaCart.visibility = View.VISIBLE
                }
                loadingCart.visibility = View.GONE
            }.addOnFailureListener {
                //getProdukDariFirebase()
                Toast.makeText(context, R.string.gagal_konek, Toast.LENGTH_SHORT).show()
            }
        }catch (error:java.lang.Exception){
            Log.e("error",error.toString())
        }
    }

    public fun gantiJumlah(idCart: String,jumlah:Int){
        var idUser = Firebase.auth.uid.toString()
        var database = Firebase.database.reference
        database.child("user").child(idUser).child("cart").child(idCart)
            .child("jumlah_produk").setValue(jumlah.toString())
        //Toast.makeText(context, "Keranjang diperbarui", Toast.LENGTH_SHORT).show()
        updateTotalHarga()
    }

    public fun hapusCart(idCart:String){
        var idUser = Firebase.auth.uid.toString()
        var database = Firebase.database.reference
        database.child("user").child(idUser).child("cart")
            .child(idCart).removeValue().addOnSuccessListener {
                for (i in listProduk){
                    if(i.id_cart==idCart){
                        listProduk.remove(i)
                        break
                    }
                }
                rvCart.layoutManager = LinearLayoutManager(context)
                var listProdukAdapter = cartProdukAdapter(listProduk,this)
                rvCart.adapter=listProdukAdapter
                //Toast.makeText(context, "Produk dihapus dari cart", Toast.LENGTH_SHORT).show()
                updateTotalHarga()
            }
    }

    private fun updateTotalHarga(){
        var total =0
        for(i in listProduk){
            var jum = i.jumlah_produk
            var harga = i.harga_produk?.toInt()
            total = total+(jum!! *harga!!)
        }
        if(total ==0){
            tvTotalhargaCart.setText("-")
            tvTidakadaCart.visibility = View.VISIBLE
        }else{
            val hargatotal = NumberFormat.getCurrencyInstance(Locale("in","ID")).format(total.toDouble()).toString()
            tvTotalhargaCart.setText(hargatotal)
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_cart.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_cart().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}