package com.cynid.customyourname_app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_checkout.*
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class checkoutActivity : AppCompatActivity() {

    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    var alamatada=false
    var totalOngkir = 0
    var totalharga = 0
    var idKota=""
    var kurir = "jne"
    var loading_ongkir=true;
    var loading_user = true
    lateinit var pembeli:User
    lateinit var listCart:ArrayList<keranjang>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        btnBackCheckout.setOnClickListener {
            val keCart = Intent(this,MainActivity::class.java)
            keCart.putExtra("extra_nomorfragment",3)
            startActivity(keCart)
        }

        btnRequestPesanan.setOnClickListener {
            if(alamatada){
                if(!loading_ongkir&&!loading_user){
                    requestPesanan()
                    Toast.makeText(this, "Pesanan direquest", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Ulangi request", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Silahkan lengkapi profil terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        listCart = intent.getParcelableArrayListExtra<keranjang>("extra_listCart")!!
        rvListCartCheckout.layoutManager = LinearLayoutManager(this)
        val rvCartAdapter = checkoutAdapter(listCart)
        rvListCartCheckout.adapter = rvCartAdapter

        database.child("user").child(user?.uid.toString()).get().addOnSuccessListener {
            val alamat=it.child("alamat").value
            val telp = it.child("nomorhp").value
            val kota = it.child("kota").value
            val kecamatan = it.child("kecamatan").value
            val nama = it.child("nama").value
            val provinsi = it.child("provinsi").value
            idKota = it.child("id_kota").value.toString()

            pembeli = User(
                alamat=alamat.toString(),nama=nama.toString(),nomorhp = telp.toString(), provinsi = provinsi.toString()
                ,kota = kota.toString(),id_kota = idKota,kecamatan = kecamatan.toString(),id_user = user?.uid.toString()
            )

            if(telp!=null&&alamat!=null&&kota!=null&&kecamatan!=null){
                if(alamat!=""&&telp!=""&&kota!=""&&kecamatan!=""){
                    //isi alamat
                    alamatada = true
                    ambilOngkir("jne","1000","163",idKota)
                    set_spinner()
                    tvNamaUserCheckout.setText(nama.toString())
                    tvNomortelponCheckout.setText("("+telp+")")
                    tvAlamatCheckout.setText(alamat.toString()+", "+kecamatan.toString()
                            +", "+kota.toString()+", "+provinsi.toString())
                }
                else{
                    profilBelumLengkap()
                }
            }
            else{
                profilBelumLengkap()
            }
            loading_user=false
        }

        var totaltagihan = 0
        var jumlahBarang = 0
        for(i in listCart){
            totalharga = totalharga + (i.harga_produk!!.toInt()*i.jumlah_produk!!.toInt())
            jumlahBarang = jumlahBarang+(1*i.jumlah_produk!!.toInt())
        }
        totaltagihan = totalharga
        val totalHargaString = NumberFormat.getCurrencyInstance(Locale("in","ID"))
            .format(totalharga.toDouble()).toString()
        val totalTagihanString = NumberFormat.getCurrencyInstance(Locale("in","ID"))
            .format(totaltagihan.toDouble()).toString()
        tvTotalHargaCheckout.setText(totalHargaString)
        tvJumlahBarangCheckout.setText("("+jumlahBarang+" barang)")
        tvTotalTagihanCheckout.setText(totalTagihanString)
    }

    private fun requestPesanan(){
        //simpan ke database
        var sdf = SimpleDateFormat("dd MMMM YYYY")
        val tgl = sdf.format(Date())
        var catatan = editTextTCatatanCheckout.text.toString()

        var pesanan = pesanan(
            daftar_barang = listCart,
            pembeli = pembeli,
            status_pesanan = "baru",
            total_harga = totalharga.toString(),
            total_ongkir = totalOngkir.toString(),
            kurir = kurir,
            tgl_pemesanan = tgl.toString(),
            catatan = catatan,
            acc_admin = "tidak"
        )
        //tambah dulu jumlah pesanannya baru masukkan pesanan
        database.child("pesanan").child(user?.uid.toString()).get().addOnSuccessListener {
            var jumlah_skrg = it.childrenCount
            var jumlah_baru = it.childrenCount+1
            database.child("pesanan").child(user?.uid.toString())
                .child("jumlah_pesanan").setValue(jumlah_baru)

            var ref = database.child("pesanan").child(user?.uid.toString()).push()
            database.child("pesanan").child(user?.uid.toString()).child(ref.key.toString()).setValue(pesanan)
            //masukkan jg ke db untuk admin dan pengrajin
            database.child("pesanan").child("admin").child(ref.key.toString()).setValue(pesanan)

            //hapus cart kalau sudah input pesanan ke database
            database.child("user").child(user?.uid.toString()).child("cart").removeValue()

            //pindah ke halaman daftar pesanan
            val keDaftarPesanan = Intent(this,daftarPesananActivity::class.java)
            startActivity(keDaftarPesanan)
        }
    }

    private fun profilBelumLengkap(){
        tvNamaUserCheckout.setTextColor(Color.RED)
        tvNamaUserCheckout.setText("Silahkan lengkapi profil terlebih dahulu")
        tvAlamatCheckout.visibility = View.INVISIBLE
        tvNomortelponCheckout.visibility = View.INVISIBLE
    }

    private fun set_spinner(){
        spinOngkirCheckout.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(alamatada){
                    val tujuan = idKota
                    //jepara = 163
                    if(p2==0){
                        ambilOngkir("jne","1000","163",tujuan)
                        kurir="jne"
                    }else if(p2==1){
                        ambilOngkir("tiki","1000","163",tujuan)
                        kurir="tiki"
                    }else{
                        ambilOngkir("pos","1000","163",tujuan)
                        kurir="pos"
                    }
                }
                else{
                    Toast.makeText(baseContext, "Lengkapi profil terlebih dahulu", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
    }

    private fun ambilOngkir(kurir:String,berat:String,asal:String,tujuan:String){
        //request ke rajaongkir untuk ambil data provinsi
        var reqQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.rajaongkir.com/starter/cost"
        val request: StringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val respObj = JSONObject(response)
                    var ongkir=""
                    if(kurir=="pos"){
                        var hasil = respObj.getJSONObject("rajaongkir").getJSONArray("results")
                            .getJSONObject(0).getJSONArray("costs").getJSONObject(0)
                        ongkir = hasil.getJSONArray("cost").getJSONObject(0).getString("value")
                    }
                    else{
                        var hasil = respObj.getJSONObject("rajaongkir").getJSONArray("results")
                            .getJSONObject(0).getJSONArray("costs").getJSONObject(1)
                        ongkir = hasil.getJSONArray("cost").getJSONObject(0).getString("value")
                    }
                    val ongkirformat =NumberFormat.getCurrencyInstance(Locale("in","ID"))
                        .format(ongkir.toDouble()).toString()
                    tvTotalOngkirCheckout.setText(ongkirformat)
                    totalOngkir = ongkir.toInt()
                    loading_ongkir=false

                    var totaltagihan = totalharga+totalOngkir
                    val totalTagihanString = NumberFormat.getCurrencyInstance(Locale("in","ID"))
                        .format(totaltagihan.toDouble()).toString()
                    tvTotalTagihanCheckout.setText(totalTagihanString)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["weight"] = berat
                params["origin"] = asal
                params["destination"]=tujuan
                params["courier"] = kurir
                params["key"]="d77ee2de24edc9d560d48691bc27683e"
                return params
            }
        }
        reqQueue.add(request)
    }
}