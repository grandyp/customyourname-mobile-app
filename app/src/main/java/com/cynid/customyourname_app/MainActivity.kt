package com.cynid.customyourname_app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_desain.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var sudahlogin:Boolean = false

    var db = Firebase.database.reference
    var user = Firebase.auth.currentUser

    val fragment_akun = fragment_akun()
    val fragment_home = fragment_home()
    val fragment_cart = fragment_cart()
    val fragment_informasi = fragment_informasi()
    val fragment_desain = fragment_desain()

    var first=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cekNotifikasi()

        var nomorfragment = 1
        if(intent.getIntExtra("extra_nomorfragment",1)!=null){
            nomorfragment = intent.getIntExtra("extra_nomorfragment",1)
        }

        botNavMain.setOnNavigationItemSelectedListener{
            if(it.itemId==R.id.menu_akun){
                gantiFragmentKalauSudahLogin(5)
            }
            else if(it.itemId==R.id.menu_home){
                gantiFragment(1)
            }
            else if(it.itemId==R.id.menu_cart){
                gantiFragmentKalauSudahLogin(3)
            }
            else if(it.itemId==R.id.menu_info){
                gantiFragment(4)
            }
            else if(it.itemId==R.id.menu_desain){
                gantiFragmentKalauSudahLogin(2)
            }
            true
        }

        auth = Firebase.auth
        gantiFragment(nomorfragment)
    }


    //2x pencet keluar
    private var doubleBackKeluarPencetSatu = false
    private var mHandler: Handler = Handler()

    private final var mRunnable = Runnable() {
        run {
            doubleBackKeluarPencetSatu = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mHandler != null){mHandler.removeCallbacks(mRunnable)}
    }

    override fun onBackPressed() {
        if(doubleBackKeluarPencetSatu) {
            super.onBackPressed()
            finish()
            System.exit(0)
        }
        this.doubleBackKeluarPencetSatu = true;
        Toast.makeText(this,"pencet lagi untuk keluar aplikasi",Toast.LENGTH_SHORT).show()
        mHandler.postDelayed(mRunnable,2000)
    }
    //

    // cek user yang lagi login sekarang
    override fun onStart() {
        super.onStart()
        cekLogin()
    }
    //

    private fun cekLogin(){
        // cek sudah ada user yang login atau belum
        val currentUser = auth.currentUser
        if(currentUser != null){
            //ada yang login
            sudahlogin = true
        }
    }

    public fun logout(){
        Firebase.auth.signOut()
        Toast.makeText(this, "Berhasil Logout", Toast.LENGTH_SHORT).show()
        val logregIntent = Intent(this@MainActivity,LogRegActivity::class.java)
        startActivity(logregIntent)
    }

    public fun keDetailProduk(idproduk:String,dari: String){
        if(sudahlogin){
            var detailIntent = Intent(this,DetailProdukActivity::class.java)
            detailIntent.putExtra("extra_idproduk",idproduk)
            detailIntent.putExtra("extra_dari",dari)
            startActivity(detailIntent)
        }else{
            //tidak ada yang login
            val logregIntent = Intent(this@MainActivity,LogRegActivity::class.java)
            startActivity(logregIntent)
        }
    }

    public fun keDaftarPesanan(){
        val daftarPEsananIntent = Intent(this@MainActivity,daftarPesananActivity::class.java)
        startActivity(daftarPEsananIntent)
    }

    public fun keGantiPassword(){
        val gantipassintent = Intent(this@MainActivity,updatePasswordActivity::class.java)
        startActivity(gantipassintent)
    }

    fun keEditProfil(){
        val editProfilIntent = Intent(this@MainActivity,editProfilActivity::class.java)
        startActivity(editProfilIntent)
    }

    public fun keInfoBahan(){
        val infoBahanIntent = Intent(this@MainActivity,infoBahanActivity::class.java)
        startActivity(infoBahanIntent)
    }

    public fun keFAQ(){
        val faqIntent = Intent(this@MainActivity,faqActivity::class.java)
        startActivity(faqIntent)
    }

    public fun keCaraBeli(){
        val cbIntent = Intent(this@MainActivity,carabeliActivity::class.java)
        startActivity(cbIntent)
    }

    public fun keMessage(dari:String){
        if(sudahlogin){
            val messageIntent = Intent(this@MainActivity,messageActivity::class.java)
            messageIntent.putExtra("extra_dari",dari)
            startActivity(messageIntent)
        }else{
            //tidak ada yang login
            val logregIntent = Intent(this@MainActivity,LogRegActivity::class.java)
            startActivity(logregIntent)
        }

    }

    public fun keWishlist(dari:String){
        if(sudahlogin){
            val wishlistIntent = Intent(this@MainActivity,wishlistActivity::class.java)
            wishlistIntent.putExtra("extra_dari",dari)
            startActivity(wishlistIntent)
        }else{
            //tidak ada yang login
            val logregIntent = Intent(this@MainActivity,LogRegActivity::class.java)
            startActivity(logregIntent)
        }
    }

    private fun gantiFragmentKalauSudahLogin(nomorfragment:Int){
        // cek sudah ada user yang login atau belum
        if(sudahlogin){
            gantiFragment(nomorfragment)
        }
        else {
            //tidak ada yang login
            val logregIntent = Intent(this@MainActivity,LogRegActivity::class.java)
            startActivity(logregIntent)
        }
    }

    private fun gantiFragment(nomorfragment: Int){
        var fragment:Fragment = fragment_home
        if(nomorfragment==1){
            fragment = fragment_home
            botNavMain.menu.getItem(0).setChecked(true)
        }
        else if(nomorfragment==3){
            fragment = fragment_cart
            botNavMain.menu.getItem(2).setChecked(true)
        }
        else if(nomorfragment==5){
            fragment = fragment_akun
            botNavMain.menu.getItem(4).setChecked(true)
        }
        else if(nomorfragment==4){
            fragment = fragment_informasi
            botNavMain.menu.getItem(3).setChecked(true)
        }
        else if(nomorfragment==2){
            fragment = fragment_desain
            botNavMain.menu.getItem(1).setChecked(true)
        }
        supportFragmentManager.beginTransaction().replace(R.id.containerMain,fragment).commit()
    }

    private fun cekNotifikasi(){
        cekPesanBaru()
        cekUpdatePesanan()
    }

    private fun cekPesanBaru(){
        db.child("pesan").child(user?.uid.toString()).child("list_chat").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(!first) {
                    if (snapshot.child("id_pengirim").value.toString() != user?.uid.toString()) {
                        notifikasi("pesan")
                    }
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun cekUpdatePesanan(){
        db.child("pesanan").child(user?.uid.toString()).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                notifikasi("pesanan")
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun notifikasi(jenis:String){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            var channel = NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT)
            var manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)

            var pesan = "Notifikasi baru"
            if(jenis=="pesan"){pesan="Ada Pesan Baru"}
            else if(jenis=="pesanan"){pesan="Ada update untuk pesanan anda"}
            var builder = NotificationCompat.Builder(this,"n")
                .setContentText(pesan)
                .setSmallIcon(R.drawable.customyourname_icon)
                .setAutoCancel(true)

            var managerCompat = NotificationManagerCompat.from(this)
            managerCompat.notify(999,builder.build())
        }
    }
}
