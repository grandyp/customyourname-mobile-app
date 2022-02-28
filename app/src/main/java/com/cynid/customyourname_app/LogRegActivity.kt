package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_log_reg.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*

class LogRegActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private var RC_SIGN_IN: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_reg)

        val fragment_login = fragment_login()
        val fragment_register = fragment_register()
        //awalnya masuk di login
        gantiFragment(fragment_login)

        //untuk set bottom navigation ke login atau register
        LogregNavigation.setOnNavigationItemSelectedListener{
            if(it.itemId==R.id.menu_login){
                gantiFragment(fragment_login)
            }else{
                gantiFragment(fragment_register)
            }
            true
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth
        database = Firebase.database.reference
    }

    // cek user yang lagi login sekarang
    /*
    override fun onStart() {
        super.onStart()
        // cek sudah ada user yang login atau belum
        val currentUser = auth.currentUser
        if(currentUser != null){
            //pindah ke home
            val homeIntent = Intent(this@LogRegActivity,MainActivity::class.java)
            startActivity(homeIntent)
        }
    }*/
    //

    public fun keHomeTanpaLogin(){
        val homeIntent = Intent(this@LogRegActivity,MainActivity::class.java)
        startActivity(homeIntent)
    }

    public fun loginGoogle(){signIn()}
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // [START onactivityresult]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("login google", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("login google", "Google sign in failed", e)
            }
        }
    }
    // [END onactivityresult]

    //auth dengan google
    private fun firebaseAuthWithGoogle(idToken: String) {
        //nonaktifkan tombol dulu
        btnLogin.isEnabled = false
        btnLogin.isClickable = false
        btnGoogleSignIn.isClickable = false
        btnGoogleSignIn.isEnabled = false

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser

                    //cek dulu data sudah ada nd di db, kalo nd ada baru insert
                    database.child("user").child(user?.uid.toString()).get().addOnSuccessListener {
                        if(it.childrenCount<=0){
                            insertUserData(user?.uid.toString(),user?.displayName.toString()
                                ,user?.email.toString(),null)
                            Toast.makeText(this,"Berhasil Login", Toast.LENGTH_SHORT).show()
                            val homeIntent = Intent(this@LogRegActivity,MainActivity::class.java)
                            startActivity(homeIntent)
                        }
                        else{
                            cekStatusAkun()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("auth google dengan firebase", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this,"Gagal Login",
                        Toast.LENGTH_SHORT).show()
                    //aktifkan lagi tombolnya
                    btnLogin.isEnabled = true
                    btnLogin.isClickable = true
                    btnGoogleSignIn.isClickable = true
                    btnGoogleSignIn.isEnabled = true
                }
            }
    }


    private fun gantiFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(R.id.container_logreg,fragment).commit()
    }

    fun login(email:String, pass:String){
        if(email!=""&&pass!=""){
            signIn(email,pass)
        }
        else{
            Toast.makeText(this,"Pastikan Semua Data Terisi",Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(this,email,Toast.LENGTH_SHORT).show()
    }
    fun register(email:String,pass:String,cpass:String,nama:String,telp:String){
        if(pass!=""&&cpass!=""&&email!=""&&nama!=""&&telp!=""){
            if(telp.length>=10){
                if(pass.length>=8){
                    if(pass != cpass){
                        Toast.makeText(this,"Password dan Confirm Password Tidak Sesuai"
                            ,Toast.LENGTH_SHORT).show()
                    }else{
                        createAccount(email,pass,nama,telp)
                    }
                }
                else{
                    Toast.makeText(this,"Password Min 8 Karakter"
                        ,Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Pastikan Nomor HP valid"
                    ,Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(this,"Pastikan Semua Data Terisi"
                ,Toast.LENGTH_SHORT).show()
        }
    }
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    cekStatusAkun()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Gagal Login", task.exception)
                    Toast.makeText(baseContext, "Email/Password Salah",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END sign_in_with_email]
    }

    private fun cekStatusAkun(){
        database.child("user").child(Firebase.auth.currentUser?.uid.toString()).child("aktif").get()
            .addOnSuccessListener {
                if (it.value.toString()=="ya"){
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext,"Berhasil Login", Toast.LENGTH_SHORT).show()
                    val homeIntent = Intent(this@LogRegActivity,MainActivity::class.java)
                    startActivity(homeIntent)
                }
                else{
                    Firebase.auth.signOut()
                    Toast.makeText(this,"Gagal Login, akun dinonaktifkan", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun createAccount(email: String, password: String,nama:String, telp:String) {
        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(baseContext,"Berhasil Register", Toast.LENGTH_SHORT)
                        .show()
                    val user = auth.currentUser
                    insertUserData(user?.uid.toString(),nama,user?.email.toString(),telp)
                    val homeIntent = Intent(this@LogRegActivity,MainActivity::class.java)
                    startActivity(homeIntent)
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(baseContext, "Gagal Register, Pastikan Email valid",
                        Toast.LENGTH_SHORT).show()
                    Log.w("info login", task.exception)
                }
            }
        // [END create_user_with_email]
    }

    private fun insertUserData(uid: String, nama:String, email:String, telp: String?){
        val user = User(nama,email,telp)
        database.child("user").child(uid).child("nama").setValue(user.nama)
        database.child("user").child(uid).child("email").setValue(user.email)
        database.child("user").child(uid).child("aktif").setValue("ya")
        if(telp!=""&&telp!=null){
            database.child("user").child(uid).child("nomorhp").setValue(user.nomorhp)
        }
    }

}