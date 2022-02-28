package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class awal : AppCompatActivity() {

    var auth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //val uid = intent.getStringExtra("extra_userlogin")
        //textView2.text = uid
    }

    // cek user yang lagi login sekarang
    override fun onStart() {
        super.onStart()
        // cek sudah ada user yang login atau belum
        val currentUser = auth.currentUser
        if(currentUser != null){
            //pindah ke home
            val homeIntent = Intent(this,MainActivity::class.java)
            startActivity(homeIntent)
        }
        else{
            val logregIntent = Intent(this,LogRegActivity::class.java)
            startActivity(logregIntent)
        }
    }
    //
}