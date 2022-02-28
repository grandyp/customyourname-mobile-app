                                                                                                                                                                                              package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detail_produk.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailProdukActivity : AppCompatActivity() {

    private lateinit var produkdetail:produk
    private var listUlasan = arrayListOf<ulasan>()
    private lateinit var idproduk:String
    lateinit var currentUser:FirebaseUser
    lateinit var database:DatabaseReference
    var barangdiwishlist:Boolean = false
    var listDesain:ArrayList<desain> = arrayListOf()
    var listNamaDesain:ArrayList<String> = arrayListOf()
    var listIdDesain:ArrayList<String> = arrayListOf()
    var adaDesain = false
    var indexdesain=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_produk)

        tvDesainDetailProduk.visibility = View.INVISIBLE
        spinDaftarDesain.visibility = View.INVISIBLE

        database = Firebase.database.reference
        currentUser = Firebase.auth.currentUser!!

        //list warna dan ukuran
        var listWarna = arrayOf("Silver","Gold","Rosegold")
        var listUkuranKalung = arrayOf("70 cm","80 cm","90 cm")

        idproduk = intent.getStringExtra("extra_idproduk")!!
        var dari = intent.getStringExtra("extra_dari")
        //ambil data dari firebase
        var database = Firebase.database.reference
        database.child("produk").child(idproduk).get().addOnSuccessListener {
            var listgambar = it.child("gambarproduk")
            val listUrlGambar = arrayListOf<String>()
            for (j in listgambar.children) {
                listUrlGambar.add(j.child("url").value.toString())
            }
            var produk1: produk = produk(
                it.child("idproduk").value.toString(),
                it.child("namaproduk").value.toString(),
                it.child("hargaproduk").value.toString(),
                it.child("deskripsiproduk").value.toString(),
                it.child("beratproduk").value.toString(),
                it.child("kategoriproduk").value.toString(),
                it.child("statusproduk").value.toString(),
                listUrlGambar,
                custom_produk = it.child("customproduk").value.toString()
            )
            produkdetail = (produk1)
            //set gambar ke carousel
            carouselView.setImageListener { position, imageView ->
                Glide.with(this).load(produkdetail.foto_produk[position]).into(imageView)
            }
            carouselView!!.pageCount = produkdetail.foto_produk.size
            //Toast.makeText(this, produkdetail.foto_produk[0], Toast.LENGTH_SHORT).show()

            //cek tipe aksesoris
            if(produkdetail.kategori_produk=="kalung"){
                var spinUkuranAdapter:ArrayAdapter<String> = ArrayAdapter(
                    this,android.R.layout.simple_spinner_dropdown_item,listUkuranKalung
                )
                spinUkuranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinUkuranDetailProduk.adapter = spinUkuranAdapter
            }else{
                //hilangkan spinner ukuran
                tvUkuranDetailProduk.visibility = View.GONE
                spinUkuranDetailProduk.visibility = View.GONE
            }

            //tampilkan data
            val localeID =  Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            var harga = numberFormat.format(produkdetail.harga_produk?.toDouble()).toString()
            tvHargaDetailProduk.text = harga
            tvNamaDetailProduk.text = produkdetail.nama_produk
            tvDeskripsiDetailProduk.text=produkdetail.deskripsi_produk
            tvBeratDetailProduk.text = produkdetail.berat_produk+" gr"
            var spinWarnaAdapter:ArrayAdapter<String> = ArrayAdapter(
                this,android.R.layout.simple_spinner_dropdown_item,listWarna
            )
            spinWarnaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinWarnaDetailProduk.adapter = spinWarnaAdapter

            //atur klik" spinner
            spinWarnaDetailProduk.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    produkdetail.warna_produk = spinWarnaDetailProduk.getItemAtPosition(p2).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
            spinUkuranDetailProduk.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    produkdetail.panjangRantai = spinUkuranDetailProduk.getItemAtPosition(p2).toString()
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
            if(produkdetail.custom_produk == "ya"){ambilDataDesain()}

            spinDaftarDesain.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    indexdesain = p2
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        btnBackDetailProduk.setOnClickListener{
            if(dari == "wishlist"){
                var keWishlist = Intent(this,wishlistActivity::class.java)
                startActivity(keWishlist)
            }
            else if(dari=="home"){
                var keHomeIntent = Intent(this,MainActivity::class.java)
                startActivity(keHomeIntent)
            }
            else if(dari=="cart"){
                var keHomeIntent = Intent(this,MainActivity::class.java)
                keHomeIntent.putExtra("extra_nomorfragment",3)
                startActivity(keHomeIntent)
            }
        }

        //WISHLIST
        btnWishlistDetailProduk.setImageResource(R.drawable.wishlist_empty)

        if(currentUser!=null){
            var idUser = currentUser.uid
            //cek barang di wishlist ato tidak
            database.child("user").child(idUser).child("wishlist")
                .get().addOnSuccessListener {
                if (it.value != null) {
                    if(it.child(produkdetail.id_produk.toString()).value!=null){
                        //barang ada di wishlist
                        //kasi full hatinya
                        btnWishlistDetailProduk.setImageResource(R.drawable.wishlist_full)
                        barangdiwishlist = true
                    }
                }
            }
        }

        btnWishlistDetailProduk.setOnClickListener {
            btnWishlist()
        }

        btnAddtoCartDetailProduk.setOnClickListener {
            btnKeranjang()
        }

        loadUlasan()
    }

    private fun loadUlasan(){
        listUlasan.clear()
        database.child("ulasan").child(idproduk).get().addOnSuccessListener {
            if(it.childrenCount>0) {
                tvBelumadaUlasan.visibility = View.GONE
                val ulasan = it.children
                for (ulasan in ulasan) {
                    var ulasan1 = ulasan(
                        id_ulasan = ulasan.child("id_ulasan").value.toString(),
                        nama_pengulas = ulasan.child("nama_pengulas").value.toString(),
                        bintang = ulasan.child("bintang").value.toString().toInt(),
                        komentar = ulasan.child("komentar").value.toString()
                    )
                    listUlasan.add(ulasan1)
                }
                rvUlasanDetailProduk.layoutManager = LinearLayoutManager(this)
                var adapter = daftarUlasanAdapter(listUlasan)
                rvUlasanDetailProduk.adapter = adapter
            }
        }
    }

    private fun ambilDataDesain(){
        //ambil data desain
        database.child("desain").child(currentUser.uid.toString()).get().addOnSuccessListener {
            if (it.children.count()>0){
                tvDesainDetailProduk.visibility = View.VISIBLE
                spinDaftarDesain.visibility = View.VISIBLE
                adaDesain = true
                val data = it.children
                for (desain in data){
                    if(desain.child("jenis").value.toString() =="tulisan"){
                        val jenis = desain.child("jenis").value.toString()
                        val tulisan =desain.child("tulisan").value.toString()
                        val font = desain.child("font").value.toString()
                        val namadesain = desain.child("nama_desain").value.toString()
                        val url = desain.child("url_gambar").value.toString()
                        var desain1 = desain(
                            jenis =jenis,
                            tulisan = tulisan,
                            font =font,
                            nama_desain = namadesain,
                            url_gambar = url
                        )
                        listDesain.add(desain1)
                        listNamaDesain.add(namadesain)
                        listIdDesain.add(desain.key.toString())
                    }
                    else if (desain.child("jenis").value.toString() =="clipart"){
                        var dataListClipart = desain.child("list_clipart").children
                        var dataListUrlClipart = desain.child("url_gambar").children
                        var listClipart = arrayListOf<String>()
                        var listUrlClipart = arrayListOf<String>()
                        for(clip in dataListClipart){
                            listClipart.add(clip.value.toString())
                        }
                        for(url in dataListUrlClipart){
                            listUrlClipart.add(url.value.toString())
                        }
                        val jenis = desain.child("jenis").value.toString()
                        val namadesain = desain.child("nama_desain").value.toString()
                        var desain1 = desain(
                            jenis =jenis, list_clipart = listClipart, nama_desain = namadesain,
                            list_url_clipart = listUrlClipart
                        )
                        listDesain.add(desain1)
                        listNamaDesain.add(namadesain)
                        listIdDesain.add(desain.key.toString())
                    }
                    else{
                        //jenis = upload gambar
                        val jenis = desain.child("jenis").value.toString()
                        val namadesain = desain.child("nama_desain").value.toString()
                        val urlgambar = desain.child("url_gambar").value.toString()
                        var desain1 = desain(jenis =jenis, url_gambar = urlgambar, nama_desain = namadesain)
                        listDesain.add(desain1)
                        listNamaDesain.add(namadesain)
                        listIdDesain.add(desain.key.toString())
                    }
                }
                //spinner daftar desain
                var spindaftarDesainAdapter:ArrayAdapter<String> = ArrayAdapter(
                    this,android.R.layout.simple_spinner_dropdown_item,listNamaDesain
                )
                spindaftarDesainAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinDaftarDesain.adapter = spindaftarDesainAdapter
            }
            else{
                tvDesainDetailProduk.setText("Belum ada desain")
                tvDesainDetailProduk.visibility = View.VISIBLE
            }
        }
    }

    private fun btnKeranjang(){
        if(produkdetail.custom_produk=="ya"){
            if(adaDesain){
                cekCart()
            }
            else{
                Toast.makeText(this, "Silahkan buat desain terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }else{
            cekCart()
        }
    }

    private fun btnWishlist(){
        if(produkdetail.custom_produk=="ya"){
            if(adaDesain){
                tambahkeWishlist()
            }
            else{
                Toast.makeText(this, "Silahkan buat desain terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }else{
            tambahkeWishlist()
        }
    }

    private fun cekCart(){
        //cek keranjang dulu
        var idUser = Firebase.auth.uid.toString()
        var ref = database.child("user").child(idUser).child("cart")
        var id = produkdetail.id_produk.toString()
        var nama = produkdetail.nama_produk.toString()
        var harga = produkdetail.harga_produk.toString()
        var berat = produkdetail.berat_produk.toString()
        var warna = produkdetail.warna_produk.toString()
        var ukuran = produkdetail.panjangRantai.toString()
        var foto = produkdetail.foto_produk[produkdetail.foto_produk.lastIndex]
        var custom = produkdetail.custom_produk.toString()
        var kategori = produkdetail.kategori_produk.toString()

        //cek dulu apakah barang sudah ada di cart
        ref.get().addOnSuccessListener {
            if (it.value != null) {
                var sama = false
                var idCart = ""

                for (dataCart in it.children) {
                    val data_warna = dataCart.child("warna_produk").value.toString()
                    val data_kategori = dataCart.child("kategori_produk").value.toString()
                    val data_custom = dataCart.child("custom_produk").value.toString()

                    if (dataCart.child("id_produk").value.toString() == id) {
                        //kalau id sama, cek warna,desain(id desain : keynya), dan panjang(kalung)
                        if (data_warna == warna){
                            if(data_kategori == "kalung"){
                                val data_panjang = dataCart.child("panjang_kalung").value.toString()
                                if(data_panjang==ukuran){
                                    //cek customnya
                                    if(data_custom == "ya"){
                                        val data_idDesain = dataCart.child("id_desain").value.toString()
                                        if(data_idDesain==listIdDesain[indexdesain]){
                                            sama = true
                                            idCart = dataCart.key.toString()
                                        }
                                    }
                                    else{
                                        sama = true
                                        idCart = dataCart.key.toString()
                                    }
                                }
                            }
                            else{
                                //cek customnya
                                if(data_custom == "ya"){
                                    val data_idDesain = dataCart.child("id_desain").value.toString()
                                    if(data_idDesain==listIdDesain[indexdesain]){
                                        sama = true
                                        idCart = dataCart.key.toString()
                                    }
                                }
                                else{
                                    sama=true
                                    idCart = dataCart.key.toString()
                                }
                            }
                        }
                    }
                }
                if (sama) {
                    //Toast.makeText(this, idCart, Toast.LENGTH_SHORT).show()
                    var jumlah = it.child(idCart).child("jumlah_produk").value.toString()
                    var jumlahyangbaru = jumlah.toInt() + 1
                    ref.child(idCart).child("jumlah_produk").setValue(jumlahyangbaru)
                    Toast.makeText(this, "Cart diupdate", Toast.LENGTH_SHORT).show()
                }
                else{
                    tambahKeKeranjang(id, nama, harga, foto, berat, warna, custom, kategori, ukuran)
                }
            }
            else{
                //keranjang belum ada
                tambahKeKeranjang(id,nama,harga,foto,berat,warna, custom, kategori, ukuran)
            }
        }
    }


    private fun tambahKeKeranjang(id:String,nama:String,harga:String,foto:String,berat:String,warna:String,custom:String,kategori:String,ukuran:String){
        //belum ada kranjang
        //masukkan barang ke keranjang
        var idUser = Firebase.auth.uid.toString()
        var ref = database.child("user").child(idUser).child("cart")
        var cart1 = keranjang(
            id_produk = id,
            nama_produk = nama,
            harga_produk = harga,
            foto_produk = foto,
            jumlah_produk = 1,
            berat_produk = berat,
            warna_produk = warna,
            custom_produk = custom,
            kategori_produk = kategori
        )
        if(produkdetail.kategori_produk=="kalung"){
            cart1.panjang_kalung = ukuran
        }
        if(produkdetail.custom_produk=="ya"){
            //masukkan data desain, pake index
            cart1.desain = listDesain[indexdesain]
            cart1.id_desain = listIdDesain[indexdesain]
        }
        ref.push().setValue(cart1)
        Toast.makeText(this, "Barang ditambah ke keranjang", Toast.LENGTH_SHORT).show()
    }


    private fun tambahkeWishlist(){
            if(currentUser == null){
                //tidak ada yang login
                var intentKeLogReg = Intent(this,LogRegActivity::class.java)
                startActivity(intentKeLogReg)
            }
            else{
                //cek barang ada di wishlist atau tidak
                if(barangdiwishlist){
                    //barang dihapus dari wishlist, ganti ikon jadi kosong
                    var idUser = currentUser.uid
                    database.child("user").child(idUser).child("wishlist")
                        .child(produkdetail.id_produk.toString()).removeValue()
                    btnWishlistDetailProduk.setImageResource(R.drawable.wishlist_empty)
                    Toast.makeText(this, "Barang dihapus dari wishlist", Toast.LENGTH_SHORT).show()
                    barangdiwishlist = false
                }
                else{
                    barangdiwishlist=true
                    //barang dimasukkan ke wishlist, ganti ikon jadi full
                    var idUser = currentUser.uid
                    var produk = produk(
                        nama_produk = produkdetail.nama_produk,
                        harga_produk = produkdetail.harga_produk,
                        foto_produk = arrayListOf(produkdetail.foto_produk[produkdetail.foto_produk.size-1]),
                        berat_produk = produkdetail.berat_produk,
                        warna_produk = produkdetail.warna_produk,
                        custom_produk = produkdetail.custom_produk,
                        kategori_produk = produkdetail.kategori_produk
                    )
                    database.child("user").child(idUser).child("wishlist")
                        .child(produkdetail.id_produk.toString()).setValue(produk)
                    if(produkdetail.kategori_produk=="kalung"){
                        database.child("user").child(idUser).child("wishlist")
                            .child(produkdetail.id_produk.toString()).child("panjang_kalung").setValue(produkdetail.panjangRantai)
                    }
                    if(produkdetail.custom_produk=="ya"){
                        //masukkan objek desain ke database wishlist
                        database.child("user").child(idUser).child("wishlist")
                            .child(produkdetail.id_produk.toString()).child("desain").setValue(listDesain[indexdesain])
                    }
                    btnWishlistDetailProduk.setImageResource(R.drawable.wishlist_full)
                    Toast.makeText(this, "Barang ditambah ke wishlist", Toast.LENGTH_SHORT).show()
                }
            }
    }
}