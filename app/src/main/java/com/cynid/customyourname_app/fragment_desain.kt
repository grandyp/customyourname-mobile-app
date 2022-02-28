package com.cynid.customyourname_app

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.ar.core.ArCoreApk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_desain.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [fragment_desain.newInstance] factory method to
 * create an instance of this fragment.
 */
class fragment_desain : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var listDesainku = arrayListOf<desain>()

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
        return inflater.inflate(R.layout.fragment_desain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(R.drawable.animasiloading).into(animasiLoadingDaftarDesain)
        animasiLoadingDaftarDesain.visibility = View.VISIBLE
        listDesainku.clear()
        var popup:PopupMenu = PopupMenu(context,btnTambahDesainBaru)
        popup.menuInflater.inflate(R.menu.menu_buat_desain,popup.menu)
        popup.setOnMenuItemClickListener {
           if(it.itemId==R.id.menu_desain_tulisan){
               var intentDesainTulisan = Intent(context,desainTextActivity::class.java)
               startActivity(intentDesainTulisan)
           }
            else if(it.itemId==R.id.menu_desain_clipart){
               var intentDesainClipart = Intent(context,desainClipartActivity::class.java)
               startActivity(intentDesainClipart)
           }
            else{
                //desain pake upload gambar
               var intentDesainUploadGambar = Intent(context,desainUploadGambarActivity::class.java)
               startActivity(intentDesainUploadGambar)
           }
            true
        }
        btnTambahDesainBaru.setOnClickListener {
            popup.show()
        }

        val db = Firebase.database.reference
        val uid = Firebase.auth.uid.toString()
        db.child("desain").child(uid).get().addOnSuccessListener {
            try {
                val data = it.children
                for (desain in data) {
                    var jenis = desain.child("jenis").value.toString()
                    var idDesain = desain.key.toString()
                    var namaDesain = desain.child("nama_desain").value.toString()
                    var d1 =
                        desain(jenis = jenis, id_desain = idDesain, nama_desain = namaDesain)
                    if (jenis == "tulisan") {
                        var font = desain.child("font").value.toString()
                        var tulisan = desain.child("tulisan").value.toString()
                        var url = desain.child("url_gambar").value.toString()
                        d1.font = font
                        d1.tulisan = tulisan
                        d1.url_gambar = url
                    } else if (jenis == "clipart") {
                        d1.list_clipart =
                            desain.child("list_clipart").value as ArrayList<String>
                        d1.list_url_clipart =
                            desain.child("url_gambar").value as ArrayList<String>
                    } else {
                        d1.url_gambar = desain.child("url_gambar").value.toString()
                    }
                    listDesainku.add(d1)
                }
                rvDaftarDesain.layoutManager = LinearLayoutManager(context)
                val daftardesainadapter = daftarDesainAdapter(listDesainku, this)
                rvDaftarDesain.adapter = daftardesainadapter
                animasiLoadingDaftarDesain.visibility = View.GONE
            }catch (error:java.lang.Exception){
                Log.e("error", error.toString())
            }
        }
    }

    public fun previewAR(desain:desain,jenis:String){
        cekSupport(desain,jenis)
    }

    fun cekSupport(desain: desain,jenis:String) {
        val availability = ArCoreApk.getInstance().checkAvailability(context)
        if (availability.isTransient) {
            Handler().postDelayed({
                cekSupport(desain,jenis)
            }, 200)
        }
        if (availability.isSupported) {
            var listAsset = arrayListOf<asset>()
            var listNamaAsset = arrayListOf<String>()
            Firebase.database.reference.child("asset").get().addOnSuccessListener {
                val data = it.children
                for(asset in data){
                    var kategori = asset.child("kategoriasset").value.toString()
                    //Toast.makeText(context, kategori, Toast.LENGTH_SHORT).show()
                    if(jenis==kategori) {
                        var asset1 = asset(asset.child("namaasset").value.toString())
                        asset1.posisiy = asset.child("posisiyasset").value.toString()
                        var gambarasset = asset.child("gambarasset").children
                        for (gbr in gambarasset) {
                            if (gbr.child("sisi").value.toString()=="kanan") {
                                asset1.url2 = gbr.child("url").value.toString()
                            } else {
                                asset1.url1 = gbr.child("url").value.toString()
                            }
                        }
                        listAsset.add(asset1)
                        //Toast.makeText(context, asset1.toString(), Toast.LENGTH_SHORT).show()
                        listNamaAsset.add(asset1.nama.toString())
                    }
                }
                if(jenis=="kalung"||jenis=="anting") {
                    val keARface = Intent(context, arFaceActivity::class.java)
                    keARface.putExtra("extra_desain", desain)
                    keARface.putExtra("extra_jenis", jenis)
                    keARface.putParcelableArrayListExtra("extra_asset", listAsset)
                    keARface.putStringArrayListExtra("extra_namaasset", listNamaAsset)
                    startActivity(keARface)
                }
                else{
                    //cincin dan gelang
                    val keARtangan = Intent(context,arTanganActivity::class.java)
                    keARtangan.putExtra("extra_desain",desain)
                    keARtangan.putExtra("extra_jenis",jenis)
                    keARtangan.putParcelableArrayListExtra("extra_asset", listAsset)
                    keARtangan.putStringArrayListExtra("extra_namaasset", listNamaAsset)
                    startActivity(keARtangan)
                }
            }
        } else {
            Toast.makeText(context, "Devicemu tidak support AR", Toast.LENGTH_SHORT).show()
        }
    }

    public fun shareDesain(url:String){
        val shareIntent = Intent(Intent.ACTION_SEND)
        val text = "Lihat desain yang saya buat untuk aksesoris di customyourname "
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, text+url)
        startActivity(Intent.createChooser(shareIntent, "Share link using"))
    }

    public fun hapusDesain(idDesain:String){
        var idUser = Firebase.auth.uid.toString()
        var database = Firebase.database.reference
        database.child("desain").child(idUser).child(idDesain).removeValue().addOnSuccessListener {
            Firebase.storage.reference.child("desain").child(idUser).child(idDesain).listAll().addOnSuccessListener {
                for(item in it.items){
                    Firebase.storage.reference.child(item.path).delete()
                }

                for (i in listDesainku){
                    if(i.id_desain==idDesain){
                        listDesainku.remove(i)
                        break
                    }
                }
                rvDaftarDesain.layoutManager = LinearLayoutManager(context)
                val daftardesainadapter = daftarDesainAdapter(listDesainku,this)
                rvDaftarDesain.adapter = daftardesainadapter
            }
        }
    }

    public fun previewDesain(index:Int){
        //tampilkan popup window untuk menampilkan desain
        val view = LayoutInflater.from(context).inflate(R.layout.popup_desain_preview,null)
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

        val desain_yang_dipreview = listDesainku[index]
        view.setOnClickListener{popupWindow.dismiss()}
        if(desain_yang_dipreview.jenis=="tulisan"){
            var tulisan = view.findViewById<TextView>(R.id.previewTulisan)
            tulisan.visibility = View.VISIBLE
            val face: Typeface = Typeface.createFromAsset(context?.assets,"fonts/"+desain_yang_dipreview.font+".ttf")
            tulisan.setTypeface(face)
            tulisan.setText(desain_yang_dipreview.tulisan)
        }
        else if(desain_yang_dipreview.jenis=="clipart"){
            var clip1 = view.findViewById<ImageView>(R.id.previewClip1)
            var clip2 = view.findViewById<ImageView>(R.id.previewClip2)
            var clip3 = view.findViewById<ImageView>(R.id.previewClip3)
            var clip4 = view.findViewById<ImageView>(R.id.previewClip4)
            var clip5 = view.findViewById<ImageView>(R.id.previewClip5)
            var clip6 = view.findViewById<ImageView>(R.id.previewClip6)
            var clip7 = view.findViewById<ImageView>(R.id.previewClip7)
            var clip8 = view.findViewById<ImageView>(R.id.previewClip8)
            var listClipart = arrayListOf<ImageView>(clip1,clip2,clip3,clip4,clip5,clip6,clip7,clip8)
            view.findViewById<HorizontalScrollView>(R.id.scrollViewClipart).visibility = View.VISIBLE
            for(clip in listClipart){
                clip.visibility = View.GONE
                clip.maxWidth = 40
                clip.maxHeight = 40
            }
            for(i in 0..desain_yang_dipreview.list_clipart!!.size-1){
                var namaClipart = desain_yang_dipreview.list_clipart!![i]
                var uri = Uri.parse(
                    "android.resource://com.cynid.customyourname_app/drawable/"+namaClipart)
                listClipart[i].visibility = View.VISIBLE
                listClipart[i].setImageURI(uri)
            }
        }
        else{
            var gambar = view.findViewById<ImageView>(R.id.previewGambar)
            gambar.visibility = View.VISIBLE
            Glide.with(view).load(desain_yang_dipreview.url_gambar).into(gambar)
        }
        //tampilkan
        popupWindow.showAtLocation(layout_fragment_desain,Gravity.CENTER,0,0)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_desain.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            fragment_desain().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}