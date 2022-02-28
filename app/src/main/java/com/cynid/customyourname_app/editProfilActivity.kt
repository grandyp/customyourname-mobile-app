package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_detail_produk.*
import kotlinx.android.synthetic.main.activity_edit_profil.*
import org.json.JSONArray
import org.json.JSONObject

class editProfilActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    lateinit var listProvinsi:MutableMap<String,Int>
    lateinit var listKota:MutableMap<String,Int>
    lateinit var uid:String
    lateinit var db:DatabaseReference
    lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profil)

        //set database

        listProvinsi = mutableMapOf()
        listKota = mutableMapOf()

        btnBackEditProfil.setOnClickListener{ view->
            val mainIntent = Intent(this@editProfilActivity,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",5)
            startActivity(mainIntent)
        }
        auth = Firebase.auth
        uid = auth.currentUser?.uid.toString()

        //ambil data dari db
        db = Firebase.database.reference
        db.child("user").child(uid).get().addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")
            user = it.getValue<User>()!!
            emailEditprofil.setText(user?.email)
            if(user?.nama!="")namaEditProfil.setText(user?.nama)
            if(user?.nomorhp!= null &&user?.nomorhp!=""){
                nomorhpEditProfil.setText(user?.nomorhp)
            }else{
                nomorhpEditProfil.setHint("Isi Nomor HP")
            }
            if(user?.kecamatan!=null&&user?.kecamatan!=""){
                kecamatanEditProfil.setText(user?.kecamatan)
            }else{
                kecamatanEditProfil.setHint("Isi kecamatan")
            }
            if(user?.kelurahan!=null&&user?.kelurahan!=""){
                kelurahanEditProfil.setText(user?.kelurahan)
            }else{
                kelurahanEditProfil.setHint("Isi Kelurahan")
            }
            if(user?.alamat!=null&&user?.alamat!=""){
                alamatEditProfil.setText(user?.alamat)
            }else{
                alamatEditProfil.setHint("Isi Alamat")
            }


        }.addOnFailureListener{
            Log.e("firebase", "Error getting data", it)
        }
        loadProvinsi()

        btnUpdateProfil.setOnClickListener {
            //Toast.makeText(this, "tes", Toast.LENGTH_SHORT).show()
            //update data user
            updateUser()
        }
    }

    private fun updateUser(){
        val nama = namaEditProfil.text.toString()
        val nomorhp = nomorhpEditProfil.text.toString()
        val provinsi = spinner_provinsi.selectedItem.toString()
        val kota = spinner_kota.selectedItem.toString()
        val id_kota = listKota.get(kota).toString()
        val kecamatan = kecamatanEditProfil.text.toString()
        val kelurahan = kelurahanEditProfil.text.toString()
        val alamat = alamatEditProfil.text.toString()
        val email = auth.currentUser?.email.toString()

        //Toast.makeText(this, id_kota.toString(), Toast.LENGTH_SHORT).show()
        val user = User(nama,email,nomorhp,alamat,provinsi,kota,id_kota,kecamatan,kelurahan).toMap()
        db.child("user").child(uid).updateChildren(user)
        Toast.makeText(this, "Data diupdate", Toast.LENGTH_SHORT).show()
    }

    private fun loadProvinsi(){
        //request ke rajaongkir untuk ambil data provinsi
        var reqQueue:RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.rajaongkir.com/starter/province?key=d77ee2de24edc9d560d48691bc27683e"
        var strReq:StringRequest = StringRequest(Request.Method.GET,url,
            Response.Listener { response->
                var objekProvinsi:JSONObject = JSONObject(response)
                var arrRajaongkir:JSONObject = objekProvinsi.getJSONObject("rajaongkir")
                var arrProvinsi:JSONArray = arrRajaongkir.getJSONArray("results")
                var listSpinProvinsi:ArrayList<String> = ArrayList()
                var indexProv:Int=0
                var provinsiUser:String=""
                //cek ada nd provinsi dari db
                if(user?.provinsi!=null){
                    provinsiUser = user?.provinsi.toString()
                }

                for (i in 0 until arrProvinsi.length()){
                    var id = arrProvinsi.getJSONObject(i).getString("province_id").toInt()
                    var nama_provinsi = arrProvinsi.getJSONObject(i).getString("province")
                    listProvinsi.put(nama_provinsi,id)
                    listSpinProvinsi.add(nama_provinsi)
                    if(nama_provinsi==provinsiUser)indexProv=i
                    //Toast.makeText(this,id.toString(),Toast.LENGTH_SHORT).show()
                }
                var spinProvinsiAdapter:ArrayAdapter<String> = ArrayAdapter(
                    this,android.R.layout.simple_spinner_dropdown_item,listSpinProvinsi
                )
                spinProvinsiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_provinsi.adapter=spinProvinsiAdapter
                //Log.d("provinsi",listProv.getJSONObject(0).getString("province"))
                //Toast.makeText(this,arrProvinsi.toString(),Toast.LENGTH_SHORT).show()
                spinner_provinsi.onItemSelectedListener = object:
                    AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                        loadKota()
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

                //set spinner sesuai database (kalau ada)
                spinner_provinsi.setSelection(indexProv)
            },
            Response.ErrorListener {
            }
        )
        reqQueue.add((strReq))
    }

    private fun loadKota(){
        var idProv = spinner_provinsi.selectedItemId +1
        //Toast.makeText(this, idProv.toString(), Toast.LENGTH_LONG).show()

        //request ke rajaongkir untuk ambil data kota pake id provinsi
        var reqQueue:RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.rajaongkir.com/starter/city?" +
                "key=d77ee2de24edc9d560d48691bc27683e&province="+idProv.toString()
        var strReq:StringRequest = StringRequest(Request.Method.GET,url,
            Response.Listener { response->
                var objekProvinsi:JSONObject = JSONObject(response)
                var arrRajaongkir:JSONObject = objekProvinsi.getJSONObject("rajaongkir")
                var arrKota:JSONArray = arrRajaongkir.getJSONArray("results")
                var listSpinKota:ArrayList<String> = ArrayList()

                var indexKota:Int=0
                var kotaUser:String=""
                //cek ada nd kota dari db
                if(user?.kota!=null){
                    kotaUser = user?.kota.toString()
                }

                for (i in 0 until arrKota.length()){
                    var id = arrKota.getJSONObject(i).getString("city_id").toInt()
                    var nama_kota = arrKota.getJSONObject(i).getString("city_name")
                    listKota.put(nama_kota,id)
                    listSpinKota.add(nama_kota)
                    if(nama_kota==kotaUser)indexKota=i
                    //Toast.makeText(this,id.toString(),Toast.LENGTH_SHORT).show()
                }
                var spinKotaAdapter:ArrayAdapter<String> = ArrayAdapter(
                    this,android.R.layout.simple_spinner_dropdown_item,listSpinKota
                )
                spinKotaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner_kota.adapter=spinKotaAdapter

                //set kota sesuai db
                spinner_kota.setSelection(indexKota)
            },
            Response.ErrorListener {
            }
        )
        reqQueue.add((strReq))
    }

}