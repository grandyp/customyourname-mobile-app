package com.cynid.customyourname_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_akun.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_akun.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_akun : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_akun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnGantiPassword.visibility = View.GONE
        val userlogin = auth.currentUser
        var providerdata = userlogin?.providerData
        for (data in providerdata!!){
            if(data.providerId == "password"){
                btnGantiPassword.visibility = View.VISIBLE
            }
        }
        Firebase.database.reference.child("user").child(userlogin?.uid.toString()).get().addOnSuccessListener {
            val nama = it.child("nama").value.toString()
            namaUser_akun.text = nama
            hurufDepanProfil.text = nama[0].toString().toUpperCase()
        }

        btnEditProfil.setOnClickListener{view->
            var parent:MainActivity= activity as MainActivity
            parent.keEditProfil()
        }

        btnGantiPassword.setOnClickListener{view->
            var parent:MainActivity= activity as MainActivity
            parent.keGantiPassword()
        }

        btnLogoutAkun.setOnClickListener { view->
            var parent:MainActivity= activity as MainActivity
            parent.logout()
        }

        btnDaftarPesananAkun.setOnClickListener {
            var parent:MainActivity= activity as MainActivity
            parent.keDaftarPesanan()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_akun.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_akun().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}