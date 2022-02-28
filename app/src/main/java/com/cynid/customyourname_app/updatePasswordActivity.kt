package com.cynid.customyourname_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_update_password.*
import java.lang.Exception

class updatePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_password)

        btnBackGantiPassword.setOnClickListener {
            var mainIntent = Intent(this,MainActivity::class.java)
            mainIntent.putExtra("extra_nomorfragment",5)
            startActivity(mainIntent)
        }

        btnGantiPasswordFirebase.setOnClickListener {
            var oldpass = editTextPassLama.text.toString()
            var newpass = editTextPasswordBaru.text.toString()
            var cnewpass = editTextCPasswordBaru.text.toString()
            var user = Firebase.auth.currentUser

            var credential = EmailAuthProvider.getCredential(
                user?.email.toString(),oldpass
            )
            try {
                user?.reauthenticate(credential)?.addOnSuccessListener {
                    if (newpass == cnewpass) {
                        user.updatePassword(newpass).addOnSuccessListener {
                            Toast.makeText(this, "Password diperbarui", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Pastikan konfirmasi password sesuai",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }catch (error:Exception){
                Toast.makeText(this, "Password lama tidak sesuai", Toast.LENGTH_SHORT).show()
            }
        }
    }
}