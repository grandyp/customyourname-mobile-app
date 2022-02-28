package com.cynid.customyourname_app

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.uikit.SdkUIFlowBuilder
import kotlinx.android.synthetic.main.activity_detail_pesanan.*
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class detailPesananActivity : AppCompatActivity() {

    lateinit var pesanan:pesanan
    lateinit var popup:PopupMenu
    var database = Firebase.database.reference
    var user = Firebase.auth.currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pesanan)
        //popup menu dulu
        popup= PopupMenu(this,btnBayarDetailPesanan)
        popup.menuInflater.inflate(R.menu.menu_pembayaran,popup.menu)
        popup.setOnMenuItemClickListener {
            if(it.itemId==R.id.menu_pembayaran_manual){
                bayarPesanan("manual")
            }
            else if(it.itemId==R.id.menu_pembayaran_otomatis){
                bayarPesanan("otomatis")
            }
            true
        }

        btnBackDetailPesanan.setOnClickListener {
            val keDaftarPesanan = Intent(this,daftarPesananActivity::class.java)
            startActivity(keDaftarPesanan)
        }

        btnReviewPesanan.setOnClickListener {
            reviewPesanan()
        }

        btnSelesaikanPesanan.setOnClickListener {
            selesaikanPesanan()
        }


        pesanan = intent.getParcelableExtra<pesanan>("extra_pesanan")!!
        //Toast.makeText(this, totalharga1.toString(), Toast.LENGTH_SHORT).show()
        cekStatusdiAwal()
        btnLacakPaket.setOnClickListener {
            if(pesanan.status_pesanan=="dikirim"||pesanan.status_pesanan=="selesai") {
                lacakPaket()
            }
            else{
                Toast.makeText(this, "Nomor Resi Belum Ada", Toast.LENGTH_SHORT).show()
            }
        }

        isiStatus(pesanan.status_pesanan.toString())
        cekStatus(pesanan.status_pesanan.toString())
        tvTglPesanDetailPesanan.setText(pesanan.tgl_pemesanan)
        tvKurirDetailPesanan.setText(pesanan.kurir)

        if(pesanan.no_resi!="null"){
            tvResiDetailPesanan.setText(pesanan.no_resi)
        }

        var alamat = pesanan.pembeli?.nama+"\n"+pesanan.pembeli?.nomorhp+"\n"+
                pesanan.pembeli?.alamat+", "+pesanan.pembeli?.kecamatan+", "+
                pesanan.pembeli?.kota+", "+pesanan.pembeli?.provinsi
        tvAlamatDetailPesanan.setText(alamat)

        val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        val harga = numberFormat.format(pesanan.total_harga!!.toDouble()).toString()
        val ongkir = numberFormat.format(pesanan.total_ongkir!!.toDouble()).toString()
        tvTotalHargaDetaillPesanan.setText(harga)
        tvTotalOngkirDetailPesanan.setText(ongkir)
        val totalbayar = pesanan.total_harga!!.toInt()+pesanan.total_ongkir!!.toInt()
        val totalBayarFormat = numberFormat.format(totalbayar.toDouble()).toString()
        tvTotalBayarDetailPesanan.setText(totalBayarFormat)

        rvListBarangDetailPesanan.layoutManager = LinearLayoutManager(this)
        val barangAdapter = checkoutAdapter(pesanan.daftar_barang!!)
        rvListBarangDetailPesanan.adapter = barangAdapter
    }

    private fun lacakPaket(){
        var resi = pesanan.no_resi.toString()
        resi = "002770532595"
        var reqQueue: RequestQueue = Volley.newRequestQueue(this)
        var kurir = pesanan.kurir.toString()
        kurir = "sicepat"
        val url = "https://api.binderbyte.com/v1/track?courier="+kurir+"&api_key=c37aa62ab2a443571c54e02dac7e1d49354effeb04ae08f38b7c5a87507f8603&awb="+resi
        val request: StringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener { response ->
                try {
                    val respObj = JSONObject(response)
                    if (respObj.getString("status") == "200") {
                        var history = respObj.getJSONObject("data").getJSONArray("history")
                        var status = history.getJSONObject(0).getString("desc")
                        var date = history.getJSONObject(0).getString("date")
                        popupStatusPelacakan(status,date)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        reqQueue.add(request)
    }

    private fun popupStatusPelacakan(status:String,date:String){
        val view = LayoutInflater.from(this).inflate(R.layout.layout_pelacakan_paket,null)
        val popupWindow = PopupWindow(
            view,LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT
        )

        //transisi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val fade = Fade()
            popupWindow.enterTransition = fade
            popupWindow.exitTransition = fade
        }

        view.setOnClickListener{popupWindow.dismiss()}
        var tvStatus = view.findViewById<TextView>(R.id.tvStatusLacakPaket)
        var tvTgl = view.findViewById<TextView>(R.id.tvTglLacakPaket)
        tvStatus.setText(status)
        tvTgl.setText(date)
        //tampilkan
        popupWindow.showAtLocation(layout_detail_pesanan,Gravity.CENTER,0,0)
    }

    private fun reviewPesanan(){
        //ke halaman list barang yang bisa direview
        val keListReview = Intent(this,listBarangReviewActivity::class.java)
        keListReview.putExtra("extra_dari","detailPesanan")
        keListReview.putExtra("extra_pesanan",pesanan)
        startActivity(keListReview)
    }

    private fun selesaikanPesanan(){
        //ubah status jadi selesai
        //kasi alert dulu yakin nd selesaikan
        var li = LayoutInflater.from(this)
        var promptsView = li.inflate(R.layout.layout_selesaikan_pesanan,null)
        var dialogbuilder = AlertDialog.Builder(this)
        dialogbuilder.setView(promptsView)
        dialogbuilder.setCancelable(false)
            .setPositiveButton("Ya",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    //ganti status jadi ditolak
                    var ref = database.child("pesanan/admin").child(pesanan?.id_pesanan.toString())
                    var ref2 = database.child("pesanan").child(user?.uid.toString())
                        .child(pesanan?.id_pesanan.toString())
                    ref.child("status_pesanan").setValue("selesai")
                    ref2.child("status_pesanan").setValue("selesai")

                    //masukkan ke child barang_dibeli
                    for(barang in pesanan.daftar_barang!!){
                        barangDibeli(barang)
                    }
                    Toast.makeText(this, "Pesanan Telah Selesai", Toast.LENGTH_SHORT).show()
                    refreshPage()
                })
            .setNegativeButton("Tidak",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    dialogInterface.cancel()
                })
        var alertDialog = dialogbuilder.create()
        alertDialog.show()
    }

    private fun barangDibeli(barang:keranjang){
        //cek dulu sudah ada nd di id barang dibeli
        var ref2 = database.child("user").child(user?.uid.toString()).child("barang_dibeli")
            .child(barang.id_produk.toString())
        ref2.get().addOnSuccessListener {
            if(!it.hasChildren()){
                ref2.child("sudah_review").setValue("tidak")
                var produk = produk(
                    nama_produk = barang.nama_produk,
                    id_produk = barang.id_produk,
                    foto_produk = arrayListOf(barang.foto_produk.toString())
                )
                ref2.child("detail_produk").setValue(produk)
            }
        }
    }

    private fun produkTerjual(tgl:String,daftar_barang:ArrayList<keranjang>){
        for(barang in pesanan.daftar_barang!!){
            var ref = database.child("produk_terjual").child(tgl)
                .child(barang.id_produk.toString())
            ref.get().addOnSuccessListener {
                if(it.hasChildren()){
                    //kalau sudah ada, jumlah barangnya yang ditambah
                    var jumlah = it.child("jumlah").value.toString().toInt()
                    var jumlahBaru = barang.jumlah_produk!!.toInt() + jumlah
                    ref.child("jumlah").setValue(jumlahBaru)
                }
                else{
                    //kalau belum, insert baru
                    var dataproduk = mapOf<String,String>(
                        "jumlah" to barang.jumlah_produk.toString(),
                        "nama" to barang.nama_produk.toString(),
                        "tgl_pemesanan" to tgl,
                        "idproduk" to barang.id_produk.toString(),
                        "kategori" to barang.kategori_produk.toString()
                    )
                    ref.setValue(dataproduk)
                }
            }
        }
    }

    private fun isiStatus(status:String){
        tvMenungguKonfirmasi.visibility = View.GONE
        if(status=="baru"){
            tvStatusDetailPesanan.setText("Menunggu Konfirmasi Pengrajin")
            tvMenungguKonfirmasi.visibility =View.VISIBLE
        }
        else if(status=="disetujui"){
            tvStatusDetailPesanan.setText("Pesanan telah disetujui, silahkan melakukan pembayaran.\n" +
                    "Harap periksa harga barang yang telah ditetapkan oleh pengrajin")
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="ditolak"){
            tvStatusDetailPesanan.setText("Pesanan ditolak\nCatatan dari pengrajin: "+pesanan.catatan_pengrajin)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="dibayar"){
            tvStatusDetailPesanan.setText("Pesanan sudah dibayar. Menunggu admin memeriksa pembayaran\nCatatan dari pengrajin: "+pesanan.catatan)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="diverifikasi"){
            tvStatusDetailPesanan.setText("Pesanan telah diverifikasi. Menunggu pengrajin untuk memproses\nCatatan dari pengrajin: "+pesanan.catatan)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="diproses"){
            tvStatusDetailPesanan.setText("Pesanan sedang dalam proses pengerjaan\nCatatan dari pengrajin: "+pesanan.catatan_pengrajin)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="dikirim"){
            tvStatusDetailPesanan.setText("Pesanan sedang dalam pengiriman\nCatatan dari pengrajin: "+pesanan.catatan_pengrajin)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
        else if(status=="selesai"){
            tvStatusDetailPesanan.setText("Pesanan Selesai\nCatatan dari pengrajin: "+pesanan.catatan_pengrajin)
            tvMenungguKonfirmasi.visibility =View.GONE
        }
    }

    private fun cekStatus(status:String){
        btnTolakPesanan.visibility = View.GONE
        btnSelesaikanPesanan.visibility = View.GONE
        btnReviewPesanan.visibility = View.GONE
        if(status == "ditolak"){
            btnBayarDetailPesanan.visibility = View.GONE
        }
        else if(status=="baru"){
            btnBayarDetailPesanan.setOnClickListener {
                Toast.makeText(this, "Mohon tunggu kepastian dari pengrajin", Toast.LENGTH_SHORT).show()
            }
        }
        else if(status=="disetujui"){
            btnTolakPesanan.visibility = View.VISIBLE
            btnBayarDetailPesanan.setBackgroundResource(R.drawable.button_melengkungv2)
            btnBayarDetailPesanan.setOnClickListener {popup.show()}
            btnTolakPesanan.setOnClickListener { batalkanPesanan() }
        }
        else if(status=="dibayar"){
            btnBayarDetailPesanan.visibility = View.GONE
        }
        else if(status=="diverifikasi"){
            btnBayarDetailPesanan.visibility = View.GONE
        }
        else if(status=="diproses"){
            btnBayarDetailPesanan.visibility = View.GONE
        }
        else if(status=="dikirim"){
            btnBayarDetailPesanan.visibility = View.GONE
            btnSelesaikanPesanan.visibility = View.VISIBLE
        }
        else if(status=="selesai"){
            btnBayarDetailPesanan.visibility = View.GONE
            btnReviewPesanan.visibility = View.VISIBLE
        }
    }

    private fun bayarPesanan(metode:String){
        if(metode=="manual"){
            val kePembayaran = Intent(this,pembayaranManualActivity::class.java)
            kePembayaran.putExtra("extra_pesanan",pesanan)
            startActivity(kePembayaran)
        }
        else{
            cekStatusPembayaran()
        }
    }

    private fun cekStatusdiAwal(){
        if(pesanan.status_pesanan=="disetujui") {
            var reqQueue: RequestQueue = Volley.newRequestQueue(this)
            val url = "https://api.sandbox.midtrans.com/v2/" + pesanan.id_pesanan + "/status/"
            val request: StringRequest = object : StringRequest(
                Method.GET, url,
                Response.Listener { response ->
                    try {
                        val respObj = JSONObject(response)
                        if (respObj.getString("status_code") != "404") {
                            var status = respObj.getString("transaction_status")
                            if (status == "settlement"||status=="success") {
                                Toast.makeText(
                                    this,
                                    "Pembayaran Kamu telah berhasil",
                                    Toast.LENGTH_SHORT
                                ).show()
                                var ref = database.child("pesanan")
                                    .child(user?.uid.toString())
                                    .child(pesanan?.id_pesanan.toString())
                                var ref2 = database.child("pesanan/admin")
                                    .child(pesanan?.id_pesanan.toString())
                                ref.child("status_pesanan").setValue("diverifikasi")
                                ref.child("metode_pembayaran").setValue("otomatis")
                                ref2.child("status_pesanan").setValue("diverifikasi")
                                ref2.child("metode_pembayaran").setValue("otomatis")

                                //masukkan ke db produk terjual
                                produkTerjual(pesanan.tgl_pemesanan.toString(),pesanan.daftar_barang!!)

                                //redirect ke daftar pesanan
                                val keDaftar = Intent(this, daftarPesananActivity::class.java)
                                startActivity(keDaftar)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["Authorization"] =
                        "Basic U0ItTWlkLXNlcnZlci1BTURQdnI1cDdPTDJBNjhsOHRfS1R0dVI6"
                    params["x-API-key"] = "SB-Mid-client-tHZ6uWIbns_f5Ky9"
                    return params
                }
            }
            reqQueue.add(request)
        }
    }

    private fun cekStatusPembayaran(){
        var reqQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "https://api.sandbox.midtrans.com/v2/"+pesanan.id_pesanan+"/status/"
        val request: StringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    val respObj = JSONObject(response)
                    if(respObj.getString("status_code")!="404") {
                        var status = respObj.getString("transaction_status")
                        if (status != "settlement") {
                            var bank = respObj.getJSONArray("va_numbers").getJSONObject(0).getString("bank").toString()
                            var va = respObj.getJSONArray("va_numbers").getJSONObject(0)
                                .getString("va_number").toString()
                            popupBayarOtomatis(bank,va)
                        }
                    }
                    else{
                        paymentgateway()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_SHORT).show()
            }) {
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "Basic U0ItTWlkLXNlcnZlci1BTURQdnI1cDdPTDJBNjhsOHRfS1R0dVI6"
                params["x-API-key"] = "SB-Mid-client-tHZ6uWIbns_f5Ky9"
                return params
            }
        }
        reqQueue.add(request)
    }

    private fun paymentgateway(){
        SdkUIFlowBuilder.init()
            .setClientKey("SB-Mid-client-tHZ6uWIbns_f5Ky9")
            .setContext(this)
            .setTransactionFinishedCallback{
                cekStatusdiAwal()
            }
            .setMerchantBaseUrl("https://customyourname.herokuapp.com/api/")
            .buildSDK()

        var totalharga1 = pesanan.total_harga!!.toDouble()+pesanan.total_ongkir!!.toDouble()
        var transRequest = TransactionRequest(pesanan.id_pesanan.toString(),totalharga1)
        var listbarang = arrayListOf<com.midtrans.sdk.corekit.models.ItemDetails>()
        for(barang in pesanan.daftar_barang!!){
            var item1 = com.midtrans.sdk.corekit.models.ItemDetails(
                barang.id_produk!!.toString(),barang.harga_produk!!.toDouble(),barang.jumlah_produk!!.toInt(),barang.nama_produk!!.toString()
            )
            listbarang.add(item1)
        }
        var item2 = com.midtrans.sdk.corekit.models.ItemDetails(
            "0",pesanan.total_ongkir!!.toDouble(),1,"ongkos kirim"
        )
        listbarang.add(item2)
        transRequest.itemDetails = listbarang

        val setting = MidtransSDK.getInstance().uiKitCustomSetting
        setting.setSkipCustomerDetailsPages(true)
        MidtransSDK.getInstance().uiKitCustomSetting = setting
        MidtransSDK.getInstance().transactionRequest = transRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }


    private fun batalkanPesanan(){
        var li = LayoutInflater.from(this)
        var promptsView = li.inflate(R.layout.layout_batalkan,null)
        var dialogbuilder = AlertDialog.Builder(this)
        dialogbuilder.setView(promptsView)
        dialogbuilder.setCancelable(false)
            .setPositiveButton("Ya",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    //ganti status jadi ditolak
                    var ref = database.child("pesanan/admin").child(pesanan?.id_pesanan.toString())
                    var ref2 = database.child("pesanan").child(user?.uid.toString())
                        .child(pesanan?.id_pesanan.toString())
                    ref.child("status_pesanan").setValue("ditolak")
                    ref2.child("status_pesanan").setValue("ditolak")
                    Toast.makeText(this, "Pesanan Dibatalkan", Toast.LENGTH_SHORT).show()
                    refreshPage()
                })
            .setNegativeButton("Tidak",
                DialogInterface.OnClickListener { dialogInterface, j ->
                    dialogInterface.cancel()
                })
        var alertDialog = dialogbuilder.create()
        alertDialog.show()
    }

    private fun popupBayarOtomatis(bank:String,va:String){
        val view = LayoutInflater.from(this).inflate(R.layout.popup_bayar_midtrans,null)
        val popupWindow = PopupWindow(
            view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        //transisi
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.elevation = 10.0F
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val fade = Fade()
            popupWindow.enterTransition = fade
            popupWindow.exitTransition = fade
        }
        view.setOnClickListener{popupWindow.dismiss()}
        view.findViewById<TextView>(R.id.tvNomorVirtualPopup).setText(va)
        //tampilkan
        popupWindow.showAtLocation(layout_detail_pesanan, Gravity.CENTER,0,0)
    }

    private  fun refreshPage(){
        val refresh = Intent(this,daftarPesananActivity::class.java)
        startActivity(refresh)
    }
}