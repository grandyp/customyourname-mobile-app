package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_barang_review.*

class listBarangReviewActivity : AppCompatActivity() {

    var listBarang = arrayListOf<produk>()
    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_barang_review)

        var dari = intent.getStringExtra("extra_dari")
        btnBackListReview.setOnClickListener {
            if(dari=="detailPesanan"){
                val keDetail = Intent(this,detailPesananActivity::class.java)
                var pesanan = intent.getParcelableExtra<pesanan>("extra_pesanan")
                keDetail.putExtra("extra_pesanan",pesanan)
                startActivity(keDetail)
            }else{
                val keDaftarPesanan = Intent(this,daftarPesananActivity::class.java)
                startActivity(keDaftarPesanan)
            }
        }
        ambilData()
    }

    private fun ambilData(){
        tvBelumaAdaProdukreview.visibility = View.GONE
        listBarang.clear()
        database.child("user").child(user?.uid.toString()).child("barang_dibeli")
            .get().addOnSuccessListener {
                val item = it.children
                for (produk in item) {
                    val sudah_review = produk.child("sudah_review").value.toString()
                    if(sudah_review=="tidak") {
                        var produk1 = produk(
                            nama_produk = produk.child("detail_produk")
                                .child("nama_produk").value.toString(),
                            id_produk = produk.child("detail_produk")
                                .child("id_produk").value.toString(),
                            foto_produk = arrayListOf(
                                produk.child("detail_produk")
                                    .child("foto_produk/0").value.toString()
                            )
                        )
                        listBarang.add(produk1)
                    }
                }
                if(listBarang.size>0) {
                    rvListProdukygBisaDireview.layoutManager = LinearLayoutManager(this)
                    val adapter = listBarangReviewAdapter(listBarang)
                    rvListProdukygBisaDireview.adapter = adapter
                }
                else{
                    tvBelumaAdaProdukreview.visibility = View.VISIBLE
                }
            }
    }

    public fun keDetailReview(index:Int){
        val keDetail = Intent(this,detailBarangReviewActivity::class.java)
        keDetail.putExtra("extra_id",listBarang[index].id_produk)
        keDetail.putExtra("extra_nama",listBarang[index].nama_produk)
        startActivity(keDetail)
    }
}