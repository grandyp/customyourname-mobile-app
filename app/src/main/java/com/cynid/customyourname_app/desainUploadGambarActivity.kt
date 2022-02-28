package com.cynid.customyourname_app

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_desain_upload_gambar.*
import java.io.ByteArrayOutputStream
import java.util.*

class desainUploadGambarActivity : AppCompatActivity() {

    var pilihGambar = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desain_upload_gambar)

        btnBackDesainUploadGambar.setOnClickListener {
            val mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",2)
            startActivity(mainIntent)
        }

        btnKonsulUploadGambar.setOnClickListener {
            val konsulIntent = Intent(this,messageActivity::class.java)
            konsulIntent.putExtra("extra_dari","upload")
            startActivity(konsulIntent)
        }

        btnUploadGambarDesain.setOnClickListener {
            //ketika sedang upload, button disable dan imageview loading
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(
                intent,"Pilih gambar..."),
                1
            )
        }

        btnSimpanDesainUploadGambar.setOnClickListener {
            if(pilihGambar){
                //mmunculkan dialog baru upload gambar dan simpan data desain di database
                simpanDesain()
            }else{
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1&&resultCode==Activity.RESULT_OK&&data!=null&&data.data!=null){
            val uri = data.data
            //masukkan ke imageview
            imageViewGambarUpload.setImageURI(uri)
            pilihGambar = true
        }
    }

    private fun uploadDesain(namaDesain:String){
        //upload gambar dan simpan data ke database
        var ref = Firebase.database.reference.child("desain")
            .child(Firebase.auth.currentUser?.uid.toString()).push()
        val namafile = UUID.randomUUID().toString()+".jpg"
        val storageRef = Firebase.storage.reference.child("desain")
            .child(Firebase.auth.currentUser?.uid.toString()).child(ref.key.toString()).child(namafile)
        val bitmap = (imageViewGambarUpload.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()

        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                var desain1 = desain(
                    jenis = "upload gambar",
                    url_gambar = it.toString(),
                    nama_desain = namaDesain)
                ref.setValue(desain1)
                Toast.makeText(this, "Desain disimpan", Toast.LENGTH_SHORT).show()
            }
        }
        uploadTask.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan desain. Silahkan ulangi", Toast.LENGTH_SHORT).show()
        }
    }

    private fun simpanDesain(){
        var li = LayoutInflater.from(this)
        var promptsView = li.inflate(R.layout.save_desain_dialog,null)
        var dialogbuilder = AlertDialog.Builder(this)
        dialogbuilder.setView(promptsView)
        dialogbuilder.setCancelable(false)
            .setPositiveButton("Simpan",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    //upload gambar dan simpan datanya\
                    val tulisan = promptsView.findViewById<EditText>(R.id.editTextNamaDesain).text.toString()
                    if(tulisan.length>0) {
                        uploadDesain(tulisan)
                    }else{
                        Toast.makeText(this, "Isi nama desain terlebih dahulu", Toast.LENGTH_SHORT).show()
                    }
                })
            .setNegativeButton("Batal",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    dialogInterface.cancel()
                })
        var alertDialog = dialogbuilder.create()
        alertDialog.show()
    }
}