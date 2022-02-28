package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_wishlist.*

class wishlistActivity : AppCompatActivity() {

    lateinit var database:DatabaseReference
    var listProduk = arrayListOf<produk>()
    lateinit var listProdukAdapter:wishlistProdukAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        btnBackWishlist.setOnClickListener {
            var dari = intent.getStringExtra("extra_dari")
            var keHomeIntent = Intent(this,MainActivity::class.java)
            if(dari=="home"){
                keHomeIntent.putExtra("extra_nomorfragment",1)
            }
            else if(dari=="cart"){
                keHomeIntent.putExtra("extra_nomorfragment",3)
            }
            startActivity(keHomeIntent)
        }

        tvTidakAdaWishlist.visibility = View.INVISIBLE

        val currentUser = Firebase.auth.currentUser

        //cek ada wihlistnya atau tidak
        database = Firebase.database.reference
        database.child("user").child(currentUser?.uid.toString()).child("wishlist").get().addOnSuccessListener {
            if (it.value != null) {
                //ada wishlist, ambil data produk yang di wishlist
                for (i in it.children) {
                    var listgambar = i.child("foto_produk")
                    val listUrlGambar = arrayListOf<String>()
                    for (j in listgambar.children) {
                        listUrlGambar.add(j.value.toString())
                    }
                    var produk1: produk = produk(
                        id_produk = i.key.toString(),
                        nama_produk = i.child("nama_produk").value.toString(),
                        harga_produk = i.child("harga_produk").value.toString(),
                        foto_produk = listUrlGambar,
                        berat_produk = i.child("berat_produk").value.toString(),
                        custom_produk = i.child("custom_produk").value.toString(),
                        kategori_produk = i.child("kategori_produk").value.toString()
                    )
                    if(produk1.kategori_produk=="kalung"){
                        produk1.panjangRantai = i.child("panjang_kalung").value.toString()
                    }
                    /*
                    if(produk1.custom_produk=="ya"){
                        val desain = i.child("desain")
                        if(desain.child("jenis").value.toString() =="tulisan"){
                            val jenis = desain.child("jenis").value.toString()
                            val tulisan =desain.child("tulisan").value.toString()
                            val font = desain.child("font").value.toString()
                            val namadesain = desain.child("nama_desain").value.toString()
                            var desain1 = desain(jenis=jenis,tulisan = tulisan,font =font,nama_desain = namadesain)
                            produk1.desain_produk = desain1
                        }
                        else if (desain.child("jenis").value.toString() =="clipart"){
                            var dataListClipart = desain.child("list_clipart").children
                            var listClipart = arrayListOf<String>()
                            for(clip in dataListClipart){
                                listClipart.add(clip.value.toString())
                            }
                            val jenis = desain.child("jenis").value.toString()
                            val namadesain = desain.child("nama_desain").value.toString()
                            var desain1 = desain(jenis=jenis,list_clipart = listClipart,nama_desain = namadesain)
                            produk1.desain_produk = desain1
                        }
                        else{
                            //jenis = upload gambar
                            val jenis = desain.child("jenis").value.toString()
                            val namadesain = desain.child("nama_desain").value.toString()
                            val urlgambar = desain.child("url_gambar").value.toString()
                            var desain1 = desain(jenis=jenis,url_gambar = urlgambar,nama_desain = namadesain)
                            produk1.desain_produk = desain1
                        }
                    }*/
                    listProduk.add(produk1)
                }
            } else {
                //tidak ada wishlist
                tvTidakAdaWishlist.visibility = View.VISIBLE
            }
            rvWishlist.layoutManager = LinearLayoutManager(this)
            listProdukAdapter = wishlistProdukAdapter(listProduk)
            rvWishlist.adapter=listProdukAdapter
        }.addOnFailureListener{
            //getProdukDariFirebase()
            Toast.makeText(this, R.string.gagal_konek, Toast.LENGTH_SHORT).show()
        }

    }

    public fun hapusWishlist(id:String){
        hapusBarangDariWishlist(id)
    }

    private fun hapusBarangDariWishlist(id:String){
        var idUser = Firebase.auth.uid.toString()
        database.child("user").child(idUser).child("wishlist")
            .child(id).removeValue().addOnSuccessListener {
                for (i in listProduk){
                    if(i.id_produk==id){
                        listProduk.remove(i)
                        break
                    }
                }
                listProdukAdapter.notifyDataSetChanged()
                if(listProduk.size==0){
                    tvTidakAdaWishlist.visibility = View.VISIBLE
                }
            }
        Toast.makeText(this, "Produk dihapus dari wishlist", Toast.LENGTH_SHORT).show()
    }

    public fun tambahkeKeranjang(id:String,nama:String,harga:String,foto:String
                                 ,berat:String,warna:String,ukuran:String,custom:String,kategori:String,desain:desain){
        masukkankeKeranjang(id,nama,harga,foto,berat,warna,ukuran,custom,kategori,desain)
    }

    private fun masukkankeKeranjang(id:String,nama:String,harga:String,foto:String,berat:String,warna: String
                                    ,ukuran: String,custom: String,kategori: String,desain: desain){
        var idUser = Firebase.auth.uid.toString()
        var ref = database.child("user").child(idUser).child("cart")
        //cek dulu apakah barang sudah ada di cart, kalau ada jumlahnya ditambah
        ref.child(id).get().addOnSuccessListener {
            if(it.value!=null){
                //barang sudah ada di keranjang
                //tambah jumlah barang yang ada di keranjang
                var jumlah = it.child("jumlah_produk").value.toString()
                var jumlahyangbaru = jumlah.toInt()+1
                ref.child(id).child("jumlah_produk").setValue(jumlahyangbaru)
            }
            else{
                //barang belum ada di kranjang
                //masukkan barang ke keranjang
                ref.child(id).child("id_produk").setValue(id)
                ref.child(id).child("nama_produk").setValue(nama)
                ref.child(id).child("harga_produk").setValue(harga)
                ref.child(id).child("foto_produk").setValue(foto)
                ref.child(id).child("jumlah_produk").setValue(1)
                ref.child(id).child("berat_produk").setValue(berat)
                ref.child(id).child("warna_produk").setValue(warna)
                ref.child(id).child("custom_produk").setValue(custom)
                if(kategori =="kalung"){
                    ref.child(id).child("panjang_kalung").setValue(ukuran)
                }
                if(kategori=="ya"){
                    //masukkan data desain, pake index
                    ref.child(id).child("desain").setValue(desain)
                }
            }
        }
        Toast.makeText(this, "Barang ditambah ke keranjang", Toast.LENGTH_SHORT).show()
    }
}