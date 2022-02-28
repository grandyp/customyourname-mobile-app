package com.cynid.customyourname_app

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_desain_text.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class desainTextActivity : AppCompatActivity() {

    lateinit var listSpinnerFont:ArrayList<Spinner>
    lateinit var listTVTulisan:ArrayList<TextView>
    lateinit var listBtnSave:ArrayList<Button>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desain_text)

        listTVTulisan = arrayListOf<TextView>(textViewDesainTulisan1,textViewDesainTulisan2,textViewDesainTulisan3,textViewDesainTulisan4)
        listSpinnerFont = arrayListOf<Spinner>(spinPilhFont1,spinPilhFont2,spinPilhFont3,spinPilhFont4)
        listBtnSave = arrayListOf(btnSaveDesainTulisan1,btnSaveDesainTulisan2,btnSaveDesainTulisan3,btnSaveDesainTulisan4)

        btnBackDesainText.setOnClickListener {
            val mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",2)
            startActivity(mainIntent)
        }

        btnPakaiTulisan.setOnClickListener {
            if(editTextTulisanDesainTulisan.length()!=0){
                for(tv in listTVTulisan){
                    tv.setText(editTextTulisanDesainTulisan.text)
                }
            }else{
                Toast.makeText(this, "Ketik tulisan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        for(i in 0..listSpinnerFont.size-1){
            listSpinnerFont[i].onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    gantiFont(p0?.getItemAtPosition(p2).toString(),i)
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

        for(i in 0..(listBtnSave.size-1)){
            listBtnSave[i].setOnClickListener {
                var li = LayoutInflater.from(this)
                var promptsView = li.inflate(R.layout.save_desain_dialog,null)
                var dialogbuilder = AlertDialog.Builder(this)

                promptsView.findViewById<TextView>(R.id.tvPreviewTulisanSave).visibility = View.VISIBLE
                promptsView.findViewById<TextView>(R.id.tvPreviewTulisanSave).setText(listTVTulisan[i].text)
                promptsView.findViewById<TextView>(R.id.tvPreviewTulisanSave).setTypeface(listTVTulisan[i].typeface)

                dialogbuilder.setView(promptsView)
                dialogbuilder.setCancelable(false)
                    .setPositiveButton("Simpan",
                        DialogInterface.OnClickListener { dialogInterface, j ->
                            //save masukkan data ke daftar desain di firebase
                            val db = Firebase.database.reference
                            val uid = Firebase.auth.currentUser?.uid.toString()
                            val tulisan = listTVTulisan[i].text.toString()
                            val font = listSpinnerFont[i].selectedItem.toString()
                            val namadesain = promptsView.findViewById<EditText>(R.id.editTextNamaDesain).text.toString()
                            if(namadesain.length>0) {
                                val desain1 = desain(
                                    jenis = "tulisan",
                                    tulisan = tulisan,
                                    font = font,
                                    nama_desain = namadesain)
                                var ref = db.child("desain").child(uid).push()
                                ref.setValue(desain1)
                                Toast.makeText(this, "Desain disimpan", Toast.LENGTH_SHORT).show()

                                //simpan gambarnya
                                var id =ref.key
                                simpanGambarDesain(id.toString(),i)
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
    }

    private fun gantiFont(font:String,index:Int){
        val face:Typeface = Typeface.createFromAsset(assets,"fonts/"+font+".ttf")
        listTVTulisan[index].setTypeface(face)
    }

    private fun simpanGambarDesain(idDesain:String,index:Int){
        listTVTulisan[index].setTextColor(Color.WHITE)
        var bitmap = Bitmap.createBitmap(listTVTulisan[index].width,listTVTulisan[index].height,Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        listTVTulisan[index].measure(100,100)
        listTVTulisan[index].draw(canvas)
        listTVTulisan[index].setTextColor(Color.BLACK)

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
                    .child(idDesain).child("url_gambar").setValue(it.toString())
                Toast.makeText(this, "Desain disimpan", Toast.LENGTH_SHORT).show()
            }
        }
        uploadTask.addOnFailureListener{
            simpanGambarDesain(idDesain,index)
        }
    }


}