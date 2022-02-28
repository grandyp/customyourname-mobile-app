package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class chatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        rvChat.layoutManager = LinearLayoutManager(this)
        //(rvChat.layoutManager as LinearLayoutManager).stackFromEnd = true;

        var listPesan:ArrayList<message> = arrayListOf()
        var first = true

        val user = Firebase.auth.currentUser
        val db = Firebase.database.reference
        btnBackChat.setOnClickListener {
            val keMessageIntent = Intent(this,messageActivity::class.java)
            startActivity(keMessageIntent)
        }

        btnKirimChat.setOnClickListener {
            if(editTextPesanChat.text.length != 0){
                //kirim pesan, masukkan ke db
                val sdf = SimpleDateFormat("dd/M/yyyy")
                val sdf2 = SimpleDateFormat("HH:mm")
                val tgl = sdf.format(Date())
                val jam = sdf2.format(Date())
                var ref = db.child("pesan").child(user?.uid.toString()).child("list_chat").push()
                db.child("user").child(user?.uid.toString()).get().addOnSuccessListener {
                    var chat:message = message(
                        pengirim = it.child("nama").value.toString(),
                        id_pengirim = user?.uid.toString(),
                        pesan = editTextPesanChat.text.toString(),
                        tanggal = tgl.toString(),
                        jam = jam.toString(),
                        status = "sent",
                        id_chat = ref.key
                    )
                    db.child("pesan").child(user?.uid.toString()).child("nama_user").setValue(it.child("nama").value.toString())
                    ref.setValue(chat)
                    editTextPesanChat.setText("")
                }
            }
        }

        /*db.child("pesan").child(user?.uid.toString()).child("list_chat").get().addOnSuccessListener {
            for (pesan in it.children){
                val pesan1 = message(
                    pesan.child("pengirim").value.toString(),
                    pesan.child("id_pengirim").value.toString(),
                    pesan.child("pesan").value.toString(),
                    pesan.child("timestamp").value.toString()
                )
                listPesan.add(pesan1)
            }
            val listPesanAdapter = messageAdapter(listPesan)
            rvChat.adapter=listPesanAdapter
            first = false
        }*/

        db.child("pesan").child(user?.uid.toString()).child("list_chat").addChildEventListener(object :ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val pesan1 = message(
                        snapshot.child("pengirim").value.toString(),
                        snapshot.child("id_pengirim").value.toString(),
                        snapshot.child("pesan").value.toString(),
                        snapshot.child("tanggal").value.toString(),
                        snapshot.child("jam").value.toString(),
                        snapshot.child("status").value.toString(),
                        snapshot.child("id_chat").value.toString()
                    )
                    if(pesan1.id_pengirim!=user?.uid.toString()){
                        //dari admin, ubah status jadi read
                        db.child("pesan").child(user?.uid.toString()).child("list_chat").child(pesan1.id_chat.toString())
                            .child("status").setValue("read")
                    }
                    listPesan.add(pesan1)
                    val listPesanAdapter = messageAdapter(listPesan)
                    rvChat.adapter=listPesanAdapter
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if(snapshot.child("id_pengirim").value.toString()==user?.uid.toString()) {
                    for(pesan in listPesan){
                        if(pesan.id_chat.toString()==snapshot.child("id_chat").value.toString()){
                            pesan.status = snapshot.child("status").value.toString()
                            val listPesanAdapter = messageAdapter(listPesan)
                            rvChat.adapter=listPesanAdapter
                            break
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@chatActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }

        })

    }
}