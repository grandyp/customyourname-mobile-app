package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_carabeli.*

class carabeliActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carabeli)

        btnBackCaraBeli.setOnClickListener {
            val keInformasi = Intent(this,MainActivity::class.java)
            keInformasi.putExtra("extra_nomorfragment",4)
            startActivity(keInformasi)
        }
    }
}