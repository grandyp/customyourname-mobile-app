package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_daftar_pesanan.*

class daftarPesananActivity : AppCompatActivity() {
    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    var listPesanan = arrayListOf<pesanan>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_pesanan)

        Glide.with(this).load(R.drawable.animasiloading).into(loadingDaftarPesanan)

        btnBackDaftarPesanan.setOnClickListener {
            val keHome = Intent(this,MainActivity::class.java)
            keHome.putExtra("extra_nomorfragment",5)
            startActivity(keHome)
        }
        loadDataPesanan("baru")

        btnPesananBaru.setOnClickListener { loadDataPesanan("baru") }
        btnPesananDisetujui.setOnClickListener { loadDataPesanan("disetujui") }
        btnPesananDibayar.setOnClickListener { loadDataPesanan("dibayar") }
        btnPesananDiverifikasi.setOnClickListener { loadDataPesanan("diverifikasi") }
        btnPesananDiproses.setOnClickListener { loadDataPesanan("diproses") }
        btnPesananDikirim.setOnClickListener { loadDataPesanan("dikirim") }
        btnPesananSelesai.setOnClickListener { loadDataPesanan("selesai") }
        btnPesananDibatalkan.setOnClickListener { loadDataPesanan("ditolak") }
    }

    public fun keDetailPesanan(pesanan:pesanan){
        val keDetailPesanan = Intent(this,detailPesananActivity::class.java)
        keDetailPesanan.putExtra("extra_pesanan",pesanan)
        startActivity(keDetailPesanan)
    }

    public fun keDaftarReview(){
        val keListReview = Intent(this,listBarangReviewActivity::class.java)
        keListReview.putExtra("extra_dari","daftarPesanan")
        startActivity(keListReview)
    }

    private fun refreshData(){
        tvTidakAdaPesananDaftarPesanan.visibility = View.INVISIBLE
        listPesanan.clear()
        rvDaftarPesanan.layoutManager = LinearLayoutManager(this)
        val adapterDaftarPesanan = daftarPesananAdapter(listPesanan)
        rvDaftarPesanan.adapter = adapterDaftarPesanan
        rvDaftarPesanan.scrollToPosition(listPesanan.size-1)
    }

    private fun loadDataPesanan(status:String){
        refreshData()
        var ref = database.child("pesanan").child(user?.uid.toString())
            .orderByChild("status_pesanan").equalTo(status)
        val postListener = object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                loadDaftarPesanan(snapshot)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        ref.addListenerForSingleValueEvent(postListener)
    }

    private fun loadDaftarPesanan(it:DataSnapshot){
        if(it.childrenCount>0){
            var data_pesanan = it.children
            for(data in data_pesanan){
                var pesanan1 = pesanan(
                    kurir = data.child("kurir").value.toString(),
                    status_pesanan = data.child("status_pesanan").value.toString(),
                    tgl_pemesanan = data.child("tgl_pemesanan").value.toString(),
                    total_harga = data.child("total_harga").value.toString(),
                    total_ongkir = data.child("total_ongkir").value.toString(),
                    no_resi = data.child("no_resi").value.toString(),
                    catatan = data.child("catatan").value.toString(),
                    catatan_pengrajin = data.child("catatan_pengrajin").value.toString(),
                    id_pesanan = data.key.toString(),
                    metode_pembayaran = data.child("metode_pembayaran").value.toString()
                )

                //masukkan data list barang
                var listBarang = arrayListOf<keranjang>()
                for(barang in data.child("daftar_barang").children){
                    var barang1 = keranjang(
                        nama_produk =barang.child("nama_produk").value.toString(),
                        foto_produk = barang.child("foto_produk").value.toString(),
                        warna_produk = barang.child("warna_produk").value.toString(),
                        jumlah_produk = barang.child("jumlah_produk").value.toString().toInt(),
                        id_produk = barang.child("id_produk").value.toString(),
                        harga_produk = barang.child("harga_produk").value.toString(),
                        kategori_produk = barang.child("kategori_produk").value.toString(),
                        custom_produk = barang.child("custom_produk").value.toString()
                    )
                    if(barang1.kategori_produk=="kalung"){
                        barang1.panjang_kalung = barang.child("panjang_kalung").value.toString()
                    }
                    //masukkan data custom
                    if(barang1.custom_produk=="ya"){
                        var desain1 = desain(
                            nama_desain = barang.child("desain/nama_desain").value.toString(),
                            jenis = barang.child("desain/jenis").value.toString()
                        )

                        if(desain1.jenis =="tulisan"){
                            val tulisan =barang.child("desain").child("tulisan").value.toString()
                            val font = barang.child("desain").child("font").value.toString()
                            val url = barang.child("desain").child("url_gambar").value.toString()
                            desain1.tulisan = tulisan
                            desain1.url_gambar = url
                            desain1.font=font
                        }
                        else if (desain1.jenis =="clipart"){
                            var dataListClipart = barang.child("desain").child("list_url_clipart").children
                            var dataListUrlClipart = barang.child("desain").child("list_clipart").children
                            var listClipart = arrayListOf<String>()
                            var listUrlClipart = arrayListOf<String>()
                            for(url in dataListUrlClipart){
                                listUrlClipart.add(url.value.toString())
                            }
                            for(clip in dataListClipart){
                                listClipart.add(clip.value.toString())
                            }
                            desain1.list_url_clipart = listUrlClipart
                            desain1.list_clipart = listClipart
                        }
                        else {
                            //jenis = upload gambar
                            val urlgambar = barang.child("desain").child("url_gambar").value.toString()
                            desain1.url_gambar = urlgambar
                        }
                        barang1.desain = desain1
                    }
                    listBarang.add(barang1)
                }
                pesanan1.daftar_barang = listBarang

                //masukkan data pemesan
                var pembeli = data.child("pembeli")
                var pemesan = User(
                    alamat = pembeli.child("alamat").value.toString(),
                    kecamatan = pembeli.child("kecamatan").value.toString(),
                    kota = pembeli.child("kota").value.toString(),
                    nama = pembeli.child("nama").value.toString(),
                    nomorhp = pembeli.child("nomorhp").value.toString(),
                    provinsi = pembeli.child("provinsi").value.toString()
                )
                pesanan1.pembeli = pemesan
                listPesanan.add(pesanan1)
            }

            //masukkan data ke recyclerview
            var layoutmanager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,true)
            layoutmanager.stackFromEnd = true
            rvDaftarPesanan.layoutManager = layoutmanager
            val adapterDaftarPesanan = daftarPesananAdapter(listPesanan)
            rvDaftarPesanan.adapter = adapterDaftarPesanan
            rvDaftarPesanan.scrollToPosition(listPesanan.size-1)
        }
        else{
            tvTidakAdaPesananDaftarPesanan.visibility = View.VISIBLE
        }
        loadingDaftarPesanan.visibility = View.GONE
    }
}