package com.cynid.customyourname_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class pembayaranGatewayActivity : AppCompatActivity() {

    lateinit var pesanan:pesanan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pembayaran_gateway)
        pesanan = intent.getParcelableExtra<pesanan>("extra_pesanan")!!


    }

    private fun tampilkanMidtrans(){

    }
}