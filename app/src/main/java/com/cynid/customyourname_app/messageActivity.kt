package com.cynid.customyourname_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_message.*


class messageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        btnWhasappMessage.setOnClickListener {
            val uri: Uri = Uri.parse("smsto:085771210008")
            val i = Intent(Intent.ACTION_SENDTO, uri)
            i.setPackage("com.whatsapp")
            startActivity(i)
        }

        btnTokopediaMessage.setOnClickListener {
            val tokpedIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://www.tokopedia.com/customyournameid")
            )
            startActivity(tokpedIntent)
        }

        btnShopeeMessage.setOnClickListener {
            val ShopeeIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://shopee.co.id/customyournameid")
            )
            startActivity(ShopeeIntent)
        }

        btnInstagramMessage.setOnClickListener {
            val instaIntent = Intent(
                "android.intent.action.VIEW",
                Uri.parse("https://www.instagram.com/customyourname.id/")
            )
            startActivity(instaIntent)
        }

        btnChatMessage.setOnClickListener {
            val keChatIntent = Intent(this,chatActivity::class.java)
            startActivity(keChatIntent)
        }

        btnBackMessage.setOnClickListener{
            var dari = intent.getStringExtra("extra_dari")
            var keChatIntent=Intent(this,MainActivity::class.java)
            if(dari=="upload"){
                keChatIntent = Intent(this,desainUploadGambarActivity::class.java)
            }
            startActivity(keChatIntent)
        }
    }
}