package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_faq.*

class faqActivity : AppCompatActivity() {

    private var listFAQ:ArrayList<faq> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)

        btnBackFaq.setOnClickListener {
            val mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",4)
            startActivity(mainIntent)
        }

        //ambil data dari firebase
        var database = Firebase.database.reference
        listFAQ.clear()
        database.child("informasi/faq").get().addOnSuccessListener {
            val dataSnapshot = it.children
            for (i in dataSnapshot){
                var faq1: faq = faq(
                    i.child("pertanyaan").value.toString(),
                    i.child("jawaban").value.toString()
                )
                listFAQ.add(faq1)
            }
            rvFAQ.layoutManager = LinearLayoutManager(this)
            val faqAdapter = faqAdapter(listFAQ)
            rvFAQ.adapter=faqAdapter
        }
    }
}