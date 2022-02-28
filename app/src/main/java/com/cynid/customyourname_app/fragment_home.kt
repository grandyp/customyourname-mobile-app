package com.cynid.customyourname_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_home.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_home.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_home : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listProduk:ArrayList<produk> = arrayListOf()
    private var urutanHarga:Int = 0
    private var kategori:Int =0
    private var database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvProduk.setHasFixedSize(true)
        Glide.with(this).load(R.drawable.animasiloading).into(animasiLoadingHome)
        animasiLoadingHome.visibility = View.VISIBLE
        //viewLoading.visibility = View.GONE

        //spinner filter harga
        //0:tidak ada, 1:harga tertinggi, 2: harga terendah
        val listSpinUrutkan = arrayOf("Urutkan","Harga Tertinggi","Harga Terendah")
        var spinUrutkanAdapter:ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireContext(),android.R.layout.simple_spinner_dropdown_item,listSpinUrutkan
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView

                if (position == 0){
                    // set the spinner disabled item text color
                    view.setTextColor(Color.LTGRAY)
                }

                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        spinUrutkanHome.adapter = spinUrutkanAdapter

        //spinner filter kategori
        val listSpinKategori = arrayOf("Kategori","Kalung","Gelang","Cincin","Anting","Bisa Custom","Tidak Bisa Custom")
        var spinKategoriAdapter:ArrayAdapter<String> = object :ArrayAdapter<String>(
            requireContext(),android.R.layout.simple_spinner_dropdown_item,listSpinKategori
        ){
            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(
                    position,
                    convertView,
                    parent
                ) as TextView
                if (position == 0){
                    // set the spinner disabled item text color
                    view.setTextColor(Color.LTGRAY)
                }
                return view
            }
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
        }
        spinKategoriHome.adapter = spinKategoriAdapter

        //ambil data dari firebase
        listProduk.clear()
        try{
            loadProduk()
        }catch (ex:Exception){}


        btnWishlistHome.setOnClickListener {
            var parent:MainActivity= activity as MainActivity
            parent.keWishlist("home")
        }

        btnChatHome.setOnClickListener {
            var parent:MainActivity= activity as MainActivity
            parent.keMessage("home")
        }

        //search
        btnSearchHome.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                //ambil data dari database berdasarkan kata kunci yang digunakan untuk search
                /*listProduk.clear()
                val kataKunci = p0
                val postReference = database.child("produk").orderByChild("namaproduk")
                    .startAt(kataKunci).endAt(kataKunci + "\uf8ff")
                val postListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val dataProduk = dataSnapshot.children
                        for (i in dataProduk){
                            if(i.child("statusproduk").value.toString()=="aktif") {
                                var listgambar = i.child("gambarproduk")
                                val listUrlGambar = arrayListOf<String>()
                                for (j in listgambar.children) {
                                    listUrlGambar.add(j.child("url").value.toString())
                                }
                                var produk1: produk = produk(
                                    i.child("idproduk").value.toString(),
                                    i.child("namaproduk").value.toString(),
                                    i.child("hargaproduk").value.toString(),
                                    i.child("deskripsiproduk").value.toString(),
                                    i.child("beratproduk").value.toString(),
                                    i.child("kategoriproduk").value.toString(),
                                    i.child("statusproduk").value.toString(),
                                    listUrlGambar
                                )
                                listProduk.add(produk1)
                            }
                        }
                        rvProduk.layoutManager = GridLayoutManager(context,2)
                        val listProdukAdapter = homeProdukAdapter(listProduk)
                        rvProduk.adapter=listProdukAdapter
                        Toast.makeText(context, listProduk.count().toString(), Toast.LENGTH_SHORT).show()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show()
                    }
                }*/
                //postReference.addListenerForSingleValueEvent(postListener)

                //filter dari list
                val key = p0.toString().toLowerCase()
                var listSearch = listProduk.filter { it.nama_produk!!.toLowerCase().contains(key) }
                rvProduk.layoutManager = GridLayoutManager(context,2)
                val listProdukAdapter = homeProdukAdapter(listSearch as ArrayList<produk>,this@fragment_home)
                rvProduk.adapter=listProdukAdapter
                return false
            }
            override fun onQueryTextChange(p0: String?): Boolean {return false}
        })

        //urutkan produk, kalau ada item di spinner yang terselect
        //0:tidak ada, 1:harga tertinggi, 2: harga terendah
        spinUrutkanHome.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2!=0){
                    urutanHarga = p2
                    filterProduk()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //filter kategori spinner select
        //1:kalung, 2:gelang, 3:cincin, 4:Anting, 5:custom, 6:noncustom
        spinKategoriHome.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(p2!=0){
                    kategori = p2
                    filterProduk()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    public fun keDetailProduk(idproduk:String,dari:String){
        var parent:MainActivity= activity as MainActivity
        parent.keDetailProduk(idproduk,dari)
    }

    private fun loadProduk(){
        database.child("produk").get().addOnSuccessListener {
            try {
                val dataSnapshot = it.children
                for (i in dataSnapshot) {
                    if (i.child("statusproduk").value.toString() == "aktif") {
                        var listgambar = i.child("gambarproduk")
                        val listUrlGambar = arrayListOf<String>()
                        for (j in listgambar.children) {
                            listUrlGambar.add(j.child("url").value.toString())
                        }
                        var produk1: produk = produk(
                            i.child("idproduk").value.toString(),
                            i.child("namaproduk").value.toString(),
                            i.child("hargaproduk").value.toString(),
                            i.child("deskripsiproduk").value.toString(),
                            i.child("beratproduk").value.toString(),
                            i.child("kategoriproduk").value.toString(),
                            i.child("statusproduk").value.toString(),
                            listUrlGambar
                        )
                        listProduk.add(produk1)
                    }
                    var parent: MainActivity = activity as MainActivity
                    parent.first = false
                }
                //Toast.makeText(context, listProduk.get(3).foto_produk.get(0), Toast.LENGTH_LONG).show()

                rvProduk.layoutManager = GridLayoutManager(context, 2)
                val listProdukAdapter = homeProdukAdapter(listProduk,this)
                rvProduk.adapter = listProdukAdapter
                animasiLoadingHome.visibility = View.GONE
                //viewLoading.visibility = View.GONE
            }catch (error:java.lang.Exception){
                Log.e("error",error.toString())
            }
        }.addOnFailureListener{
            //getProdukDariFirebase()
            loadProduk()
        }
    }

    private fun filterProduk(){
        listProduk.clear()
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataProduk = dataSnapshot.children
                for (i in dataProduk){
                    if(i.child("statusproduk").value.toString()=="aktif") {
                        var listgambar = i.child("gambarproduk")
                        val listUrlGambar = arrayListOf<String>()
                        for (j in listgambar.children) {
                            listUrlGambar.add(j.child("url").value.toString())
                        }
                        var produk1: produk = produk(
                            i.child("idproduk").value.toString(),
                            i.child("namaproduk").value.toString(),
                            i.child("hargaproduk").value.toString(),
                            i.child("deskripsiproduk").value.toString(),
                            i.child("beratproduk").value.toString(),
                            i.child("kategoriproduk").value.toString(),
                            i.child("statusproduk").value.toString(),
                            listUrlGambar
                        )
                        listProduk.add(produk1)
                    }
                }
                if(urutanHarga==1){
                    listProduk.sortByDescending { it.harga_produk?.toInt() }
                }
                else if(urutanHarga==2) {
                    listProduk.sortBy { it.harga_produk?.toInt() }
                }
                rvProduk.layoutManager = GridLayoutManager(context,2)
                val listProdukAdapter = homeProdukAdapter(listProduk,this@fragment_home)
                rvProduk.adapter=listProdukAdapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Toast.makeText(context, databaseError.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        if(urutanHarga !=0) {
            if(kategori==0){
                var postReference = database.child("produk").orderByChild("hargaproduk")
                postReference.addListenerForSingleValueEvent(postListener)
            }
            else{
                if(urutanHarga==1){
                    listProduk.sortByDescending { it.harga_produk }
                }
                else if(urutanHarga==2) {
                    listProduk.sortBy { it.harga_produk }
                }
            }
        }

        //filter kategori
        //1:kalung, 2:gelang, 3:cincin, 4:Anting, 5:custom, 6:noncustom
        if(kategori !=0){
            if(kategori==1){
                var postReference = database.child("produk")
                    .orderByChild("kategoriproduk").equalTo("kalung")
                postReference.addListenerForSingleValueEvent(postListener)
            }else if(kategori ==2){
                var postReference = database.child("produk")
                    .orderByChild("kategoriproduk").equalTo("gelang")
                postReference.addListenerForSingleValueEvent(postListener)
            }
            else if(kategori ==3){
                var postReference = database.child("produk")
                    .orderByChild("kategoriproduk").equalTo("cincin")
                postReference.addListenerForSingleValueEvent(postListener)
            }
            else if(kategori ==4){
                var postReference = database.child("produk")
                    .orderByChild("kategoriproduk").equalTo("anting")
                postReference.addListenerForSingleValueEvent(postListener)
            }
            else if(kategori ==5){
                var postReference = database.child("produk")
                    .orderByChild("customproduk").equalTo("ya")
                postReference.addListenerForSingleValueEvent(postListener)
            }
            else if(kategori ==6){
                var postReference = database.child("produk")
                    .orderByChild("customproduk").equalTo("tidak")
                postReference.addListenerForSingleValueEvent(postListener)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}