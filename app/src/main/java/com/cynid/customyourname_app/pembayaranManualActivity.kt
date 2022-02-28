package com.cynid.customyourname_app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_pembayaran_manual.*
import java.util.*

class pembayaranManualActivity : AppCompatActivity() {

    lateinit var pesanan:pesanan
    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran_manual)

        pesanan = intent.getParcelableExtra<pesanan>("extra_pesanan")!!
        cekStatusUpload()
        btnBackPembayaranManual.setOnClickListener {
            val kedetail = Intent(this,detailPesananActivity::class.java)
            kedetail.putExtra("extra_pesanan",pesanan)
            startActivity(kedetail)
        }

        btnUploadBuktiTransfer.setOnClickListener {
            uploadBukti()
        }

        btnKonfirmasiPembayaran.setOnClickListener {
            konfirmasiPembayaran()
        }
    }

    private fun uploadBukti(){
        //upload bukti dan ganti status menjadi dibayar
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(
            intent,"Pilih gambar..."),
            1
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&resultCode== Activity.RESULT_OK&&data!=null&&data.data!=null){
            val uri = data.data as Uri
            //masukkan ke database
            uploadgambarbukti(uri)
        }
    }

    private fun uploadgambarbukti(uri:Uri){
        //upload gambar dan simpan data ke database
        var ref = Firebase.database.reference.child("pesanan")
            .child(Firebase.auth.currentUser?.uid.toString()).child(pesanan?.id_pesanan.toString())
        var ref2 = Firebase.database.reference.child("pesanan/admin")
            .child(pesanan?.id_pesanan.toString())
        val namafile = UUID.randomUUID().toString()+".jpg"
        val storageRef = Firebase.storage.reference.child("pesanan")
            .child(Firebase.auth.currentUser?.uid.toString()).child(pesanan?.id_pesanan.toString()).child(namafile)

        var uploadTask = storageRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                ref.child("url_bukti").setValue(it.toString())
                ref2.child("url_bukti").setValue(it.toString())

                Toast.makeText(this, "Gambar diupload", Toast.LENGTH_SHORT).show()
                tvStatusUploadBukti.setText("Bukti sudah diupload")
            }
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Gambar gagal diupload", Toast.LENGTH_SHORT).show()
        }
    }

    private fun konfirmasiPembayaran(){
        val nama_norek = editTextNamadannorekpengirim.text
        if(nama_norek.length>0){
            var ref = Firebase.database.reference.child("pesanan")
                .child(Firebase.auth.currentUser?.uid.toString()).child(pesanan?.id_pesanan.toString())
            var ref2 = Firebase.database.reference.child("pesanan/admin")
                .child(pesanan?.id_pesanan.toString())
            ref.child("status_pesanan").setValue("dibayar")
            ref.child("metode_pembayaran").setValue("manual")
            ref.child("nama_norek").setValue(nama_norek.toString())
            ref2.child("status_pesanan").setValue("dibayar")
            ref2.child("metode_pembayaran").setValue("manual")
            ref2.child("nama_norek").setValue(nama_norek.toString())
            Toast.makeText(this, "Kamu telah membayar", Toast.LENGTH_SHORT).show()
            val kedaftar = Intent(this,daftarPesananActivity::class.java)
            startActivity(kedaftar)
        }
        else{
            Toast.makeText(this, "silahkan isi nama dan nomor rekening pengirim", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cekStatusUpload(){
        database.child("pesanan").child(user?.uid.toString()).child(pesanan?.id_pesanan.toString())
            .child("url_bukti").get().addOnSuccessListener {
                if(it.toString().length>1){
                    tvStatusUploadBukti.setText("Bukti sudah diupload")
                }
            }
    }
}