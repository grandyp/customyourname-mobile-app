package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_info_bahan.*

class infoBahanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_bahan)

        val daftarbahan = arrayOf(R.drawable.silver,R.drawable.gold,R.drawable.rosegold)
        carouselViewInfoBahan.pageCount = 3
        carouselViewInfoBahan.setImageListener { position, imageView ->
            imageView.setImageResource(daftarbahan[position])
        }

        btnBackInfoBahan.setOnClickListener {
            val mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",4)
            startActivity(mainIntent)
        }
    }
}