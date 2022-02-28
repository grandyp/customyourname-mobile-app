package com.cynid.customyourname_app

import android.content.DialogInterface
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_desain_clipart.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class desainClipartActivity : AppCompatActivity() {

    var nomorKotakSelected=-1
    lateinit var listClipart:TypedArray
    var listClipartTampil = arrayListOf<Int>()
    var listclipartTampil_kategori = arrayListOf<Int>()
    var listNamaClipart_filter = arrayListOf<String>()
    lateinit var listNamaCLipart:Array<String>
    lateinit var listKategoriCLipart:Array<String>
    var listClipartSaved = arrayOf("","","","","","","","")
    lateinit var listKotak:ArrayList<ImageView>
    var semuakategori:Boolean = true;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desain_clipart)

        listClipart = resources.obtainTypedArray(R.array.data_clipart)
        for(i in 0..listClipart.length()-1){
            listClipartTampil.add(listClipart.getResourceId(i,-1))
        }
        listNamaCLipart = resources.getStringArray(R.array.nama_clipart)
        listKategoriCLipart = resources.getStringArray(R.array.kategori_clipart)
        listKotak = arrayListOf<ImageView>(desainClipart1,desainClipart2,desainClipart3,desainClipart4,desainClipart5,desainClipart6,desainClipart7,desainClipart8)

        btnBackDesainClipart.setOnClickListener {
            val mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",2)
            startActivity(mainIntent)
        }

        for (i in 0..listKotak.size-1){
            listKotak.get(i).setOnClickListener {
                pilihKotak(listKotak.get(i))
                nomorKotakSelected = i
            }
        }

        spinKategoriClipart.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                loadClipart(p0!!.getItemAtPosition(p2).toString())
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnSimpanDesainClipart.setOnClickListener {
            var li = LayoutInflater.from(this)
            var promptsView = li.inflate(R.layout.save_desain_dialog,null)
            var dialogbuilder = AlertDialog.Builder(this)
            dialogbuilder.setView(promptsView)
            dialogbuilder.setCancelable(false)
                .setPositiveButton("Simpan",
                    DialogInterface.OnClickListener { dialogInterface, j ->
                        //save data desain clipart
                        val db = Firebase.database.reference
                        val user = Firebase.auth.currentUser
                        val namadesain = promptsView.findViewById<EditText>(R.id.editTextNamaDesain).text.toString()
                        if(namadesain.length>0) {
                            var ref = db.child("desain").child(user?.uid.toString()).push()
                            var dataclipartuser = desain(
                                jenis = "clipart",
                                nama_desain = namadesain,
                                list_clipart = arrayListOf())
                            for(i in 0..(listClipartSaved.size-1)){
                                if(listClipartSaved[i]!=""){
                                    dataclipartuser.list_clipart?.add(listClipartSaved[i])
                                    uploadClipart(ref.key.toString(),listKotak[i],i)
                                }
                            }

                            ref.setValue(dataclipartuser)
                            Toast.makeText(this, "Desain disimpan", Toast.LENGTH_SHORT).show()
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

    private fun loadClipart(kategori:String){
        if(kategori!="semua"){
            semuakategori = false
            listNamaClipart_filter.clear()
            listclipartTampil_kategori.clear()
            for(i in 0..listKategoriCLipart.size-1){
                if(listKategoriCLipart[i]==kategori){
                    listclipartTampil_kategori.add(listClipartTampil[i])
                    listNamaClipart_filter.add(listNamaCLipart[i])
                }
            }
            rvListClipart.layoutManager=GridLayoutManager(this,4)
            val adapterclipart = clipArtAdapter(listclipartTampil_kategori,this)
            rvListClipart.adapter = adapterclipart
        }
        else{
            semuakategori = true
            rvListClipart.layoutManager=GridLayoutManager(this,4)
            val adapterclipart = clipArtAdapter(listClipartTampil,this)
            rvListClipart.adapter = adapterclipart
        }
    }

    private fun uploadClipart(idDesain:String,clipart:ImageView,index:Int){
        var bitmap = Bitmap.createBitmap(clipart.width,clipart.height,Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        clipart.measure(100,100)
        var bg = clipart.background
        clipart.setBackgroundColor(Color.WHITE)
        clipart.draw(canvas)
        clipart.background = bg

        val namafile = UUID.randomUUID().toString()+".jpg"
        val storageRef = Firebase.storage.reference.child("desain")
            .child(Firebase.auth.currentUser?.uid.toString()).child(idDesain).child(namafile)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()
        var uploadTask = storageRef.putBytes(data)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                Firebase.database.reference.child("desain").child(Firebase.auth.currentUser?.uid.toString())
                    .child(idDesain).child("url_gambar").child(index.toString()).setValue(it.toString())
                Toast.makeText(this, "Desain disimpan", Toast.LENGTH_SHORT).show()
            }
        }
        uploadTask.addOnFailureListener{
            uploadClipart(idDesain,clipart,index)
        }
    }

    private fun refreshKotak(){
        for(kotak in listKotak){
            kotak.setBackgroundResource(R.drawable.button_melengkungv3)
        }
    }

    public fun inputClipart(index:Int){
        //masukkan clipart ke kotak
        if(nomorKotakSelected!=-1){
            if(semuakategori) {
                listKotak[nomorKotakSelected].setImageResource(listClipart.getResourceId(index, -1))
                listClipartSaved[nomorKotakSelected] = listNamaCLipart[index]
            }
            else{
                listKotak[nomorKotakSelected].setImageResource(listclipartTampil_kategori.get(index))
                listClipartSaved[nomorKotakSelected] = listNamaClipart_filter[index]
            }
        }else{
            Toast.makeText(this, "Pilih Kotak Terlebih Dahulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pilihKotak(view: View){
        refreshKotak()
        view.setBackgroundResource(R.drawable.button_melengkungv4)
    }
}