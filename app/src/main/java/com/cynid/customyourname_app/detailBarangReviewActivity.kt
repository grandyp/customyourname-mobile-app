package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detail_barang_review.*

class detailBarangReviewActivity : AppCompatActivity() {

    var listBintang = arrayListOf<Button>()
    var jumlah_bintang = 0
    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_barang_review)

        listBintang = arrayListOf(btnBintang1,btnBintang2,btnBintang3,btnBintang4,btnBintang5)
        for(i in 0..listBintang.size-1){
            listBintang[i].setOnClickListener {
                kasiBintang(i+1)
                jumlah_bintang = i+1
            }
        }

        var namabarang = intent.getStringExtra("extra_nama")
        tvNamaBarangDetailReview.setText(namabarang)
        btnBackDetailReview.setOnClickListener {
            var keListReview = Intent(this,listBarangReviewActivity::class.java)
            startActivity(keListReview)
        }

        btnKirimUlasan.setOnClickListener {
            KirimUlasan()
        }
    }

    private fun kasiBintang(jumlah:Int){
        kosongkanBintang()
        for (i in 0..jumlah-1){
            listBintang[i].setBackgroundResource(R.drawable.star_full)
        }
    }

    private fun kosongkanBintang(){
        for (bintang in listBintang){
            bintang.setBackgroundResource(R.drawable.star_empty)
        }
    }

    private fun KirimUlasan(){
        val komen = editTextKomentarReview.text
        var idbarang = intent.getStringExtra("extra_id")!!
        if(jumlah_bintang>0&&komen.length>0){
            database.child("user").child(user?.uid.toString()).get().addOnSuccessListener {
                //masukkan ke database, dan ubah sudah_review menjadi ya
                var ref = database.child("ulasan").child(idbarang).push()
                var ulasan1 = ulasan(
                    id_ulasan = ref.key.toString(),
                    bintang = jumlah_bintang,
                    komentar = komen.toString(),
                    nama_pengulas = it.child("nama").value.toString()
                )
                ref.setValue(ulasan1)

                //update status barang menjadi sudah direview
                database.child("user").child(user?.uid.toString()).child("barang_dibeli")
                    .child(idbarang.toString()).child("sudah_review").setValue("ya").addOnSuccessListener {
                        var keListReview = Intent(this,listBarangReviewActivity::class.java)
                        startActivity(keListReview)
                    }
            }
        }
        else{
            Toast.makeText(this, "Mohon memberi bintang dan komentar, terima kasih", Toast.LENGTH_SHORT).show()
        }
    }
}