package com.cynid.customyourname_app

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import com.bumptech.glide.Glide
import com.google.ar.core.Anchor
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_ar_face.*
import kotlinx.android.synthetic.main.activity_ar_tangan.*
import kotlinx.android.synthetic.main.fragment_desain.*
import kotlinx.android.synthetic.main.fragment_home.*

class arTanganActivity : AppCompatActivity() {

    lateinit var arFragment:ArFragment
    lateinit var desain:desain
    lateinit var jenis:String
    lateinit var uinya:ViewRenderable
    var ada=false
    var awal = true
    var list_kotak:ArrayList<ImageView> = arrayListOf()
    lateinit var listNamaAsset:ArrayList<String>
    lateinit var listAsset:ArrayList<asset>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar_tangan)
        arFragment = fragment_sceneform as ArFragment
        desain = intent.getParcelableExtra<desain>("extra_desain")!!
        jenis = intent.getStringExtra("extra_jenis")!!
        var warna:String = "hitam"
        listNamaAsset = intent.getStringArrayListExtra("extra_namaasset")!!
        listAsset = intent.getParcelableArrayListExtra<asset>("extra_asset")!!

        var adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,listNamaAsset)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinAssetARTangan.adapter = adapter

        btnTerapkanARTangan.setOnClickListener {
            if(jenis=="cincin"){
                gantiCincin(spinAssetARTangan.selectedItemPosition)
            }
            else{
                gantiGelang(spinAssetARTangan.selectedItemPosition)
            }
        }

        arFragment.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            if(plane.type != Plane.Type.HORIZONTAL_UPWARD_FACING){
                return@setOnTapArPlaneListener
            }
            val anchor = hitResult.createAnchor()
            build(arFragment,anchor,warna)
            ada = true
            if(awal){
                popupawal()
                awal = false
            }
        }

        btnBackArTangan.setOnClickListener {
            val keDesain = Intent(this,MainActivity::class.java)
            keDesain.putExtra("extra_nomorfragment",2)
            startActivity(keDesain)
        }

        btnHelpArTangan.setOnClickListener {
            loadPopUpHelp()
        }

        btnHitamArTangan.setOnClickListener {
            gantiWarna(Color.BLACK)
        }
        btnPutihArTangan.setOnClickListener {gantiWarna(Color.WHITE) }
    }

    private fun gantiGelang(index:Int){
        var asset = listAsset[index]
        if(asset.url2.toString()==""){ //1 sisi
            var gambar = uinya.view.findViewById<ImageView>(R.id.gelang1)
            gambar.visibility = View.VISIBLE
            Glide.with(this).load(asset.url1).into(gambar)
            var layoutgambar = gambar.layoutParams as ViewGroup.MarginLayoutParams
            layoutgambar.topMargin = asset.posisiy!!.toInt()+100
            uinya.view.findViewById<ImageView>(R.id.gelangkiri).visibility=View.GONE  //untuk tulisan
            uinya.view.findViewById<ImageView>(R.id.gelangkiri1).visibility=View.GONE //untuk desain clipart
            uinya.view.findViewById<ImageView>(R.id.gelangkanan).visibility=View.GONE //untuk tulisan
            uinya.view.findViewById<ImageView>(R.id.gelangkanan1).visibility=View.GONE //untuk clipart
        }
        else{
            uinya.view.findViewById<ImageView>(R.id.gelang1).visibility = View.GONE
            var jenis_desain = desain.jenis.toString()
            var gelangkiri:ImageView
            var gelangkanan:ImageView
            if(jenis_desain=="tulisan"){
                gelangkiri = uinya.view.findViewById<ImageView>(R.id.gelangkiri)
                gelangkanan = uinya.view.findViewById<ImageView>(R.id.gelangkanan)
                gelangkiri.visibility=View.VISIBLE
                gelangkanan.visibility=View.VISIBLE
                var layoutkiri = gelangkiri.layoutParams as ViewGroup.MarginLayoutParams
                layoutkiri.topMargin = asset.posisiy!!.toInt()+300
                var layoutkanan = gelangkanan.layoutParams as ViewGroup.MarginLayoutParams
                layoutkanan.topMargin = asset.posisiy!!.toInt()+300
            }
            else{
                gelangkiri = uinya.view.findViewById<ImageView>(R.id.gelangkiri1)
                gelangkanan = uinya.view.findViewById<ImageView>(R.id.gelangkanan1)
                gelangkiri.visibility=View.VISIBLE
                gelangkanan.visibility=View.VISIBLE
                var layoutkiri = gelangkiri.layoutParams as ViewGroup.MarginLayoutParams
                layoutkiri.topMargin = asset.posisiy!!.toInt()+4+300
                var layoutkanan = gelangkanan.layoutParams as ViewGroup.MarginLayoutParams
                layoutkanan.topMargin = asset.posisiy!!.toInt()+4+300
            }
            Glide.with(this).load(asset.url1).into(gelangkiri)
            Glide.with(this).load(asset.url2).into(gelangkanan)
        }
    }

    private fun gantiCincin(index:Int){
        var asset = listAsset[index]
        if(asset.url2.toString()==""){ //1 sisi
            var gambar = uinya.view.findViewById<ImageView>(R.id.cincin1Sisi)
            gambar.visibility = View.VISIBLE
            Glide.with(this).load(asset.url1).into(gambar)
            var layoutgambar = gambar.layoutParams as ViewGroup.MarginLayoutParams
            layoutgambar.topMargin = asset.posisiy!!.toInt()+400
            uinya.view.findViewById<ImageView>(R.id.cincinKiri1).visibility=View.GONE  //untuk tulisan
            uinya.view.findViewById<ImageView>(R.id.cincinKiri2).visibility=View.GONE //untuk desain clipart
            uinya.view.findViewById<ImageView>(R.id.cincinKanan1).visibility=View.GONE //untuk tulisan
            uinya.view.findViewById<ImageView>(R.id.cincinKanan2).visibility=View.GONE //untuk clipart
        }
        else{
            uinya.view.findViewById<ImageView>(R.id.cincin1Sisi).visibility = View.GONE
            var jenis_desain = desain.jenis.toString()
            var cincinkiri:ImageView
            var cincinkanan:ImageView
            if(jenis_desain=="tulisan"){
                cincinkiri = uinya.view.findViewById<ImageView>(R.id.cincinKiri1)
                cincinkanan = uinya.view.findViewById<ImageView>(R.id.cincinKanan1)
                cincinkiri.visibility=View.VISIBLE
                cincinkanan.visibility=View.VISIBLE
                var layoutkiri = cincinkiri.layoutParams as ViewGroup.MarginLayoutParams
                layoutkiri.topMargin = asset.posisiy!!.toInt()+500
                var layoutkanan = cincinkanan.layoutParams as ViewGroup.MarginLayoutParams
                layoutkanan.topMargin = asset.posisiy!!.toInt()+500
            }
            else{
                cincinkiri = uinya.view.findViewById<ImageView>(R.id.cincinKiri2)
                cincinkanan = uinya.view.findViewById<ImageView>(R.id.cincinKanan2)
                cincinkiri.visibility=View.VISIBLE
                cincinkanan.visibility=View.VISIBLE
                var layoutkiri = cincinkiri.layoutParams as ViewGroup.MarginLayoutParams
                layoutkiri.topMargin = asset.posisiy!!.toInt()+4+400
                var layoutkanan = cincinkanan.layoutParams as ViewGroup.MarginLayoutParams
                layoutkanan.topMargin = asset.posisiy!!.toInt()+4+400
            }
            Glide.with(this).load(asset.url1).into(cincinkiri)
            Glide.with(this).load(asset.url2).into(cincinkanan)
        }
    }

    private fun popupawal(){
        val view = LayoutInflater.from(this).inflate(R.layout.popup_attention_ar,null)
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
        //tampilkan
        view.setOnClickListener{popupWindow.dismiss()}
        popupWindow.showAtLocation(layout_ar_tangan,Gravity.CENTER,0,0)
    }

    private fun gantiWarna(warna:Int){
        if(ada){
            var jenis_desain = desain.jenis.toString()
            if(jenis=="cincin"){
                if(jenis_desain=="tulisan"){
                    var tulisan = uinya.view.findViewById<TextView>(R.id.tvTulisanARCincin)
                    tulisan.setTextColor(warna)
                }
                else{
                    for(i in 0..(desain.list_clipart?.size)!! -1){
                        var uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+ desain.list_clipart!![i])
                        if(warna==Color.WHITE){
                            uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+ desain.list_clipart!![i]+"_p")
                        }
                        list_kotak[i].setImageURI(uri)
                    }
                }
            }
            else{
                if(jenis_desain=="tulisan"){
                    var tulisan = uinya.view.findViewById<TextView>(R.id.tvTulisanArTangan)
                    tulisan.setTextColor(warna)
                }
                else{
                    for(i in 0..(desain.list_clipart?.size)!! -1){
                        var uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+desain.list_clipart!![i])
                        if(warna==Color.WHITE){
                            uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+desain.list_clipart!![i]+"_p")
                        }
                        list_kotak[i].setImageURI(uri)
                    }
                }
            }
        }
    }

    private fun loadPopUpHelp(){
        val view = LayoutInflater.from(this).inflate(R.layout.popup_help,null)
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
        popupWindow.showAtLocation(layout_ar_tangan, Gravity.CENTER,0,0)
    }

    private fun build(fragment: ArFragment,anchor: Anchor,warna:String){
        var viewrender = ViewRenderable.builder()
        if(jenis=="cincin"){
            viewrender.setView(fragment.context, R.layout.ar_aksesoris_cincin)
        }
        else{
            viewrender.setView(fragment.context, R.layout.ar_aksesoris_tangan)
        }
        viewrender.build()
            .thenAccept {
                it.isShadowCaster = false
                it.isShadowReceiver = false
                tampilkanDesain(fragment,anchor,it)
                loadDesainCustom(it,desain.jenis.toString(),warna)
                uinya = it
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }

    private fun tampilkanDesain(fragment: ArFragment,anchor: Anchor,renderable: Renderable){
        val anchorNode = AnchorNode(anchor)
        val node = TransformableNode(fragment.transformationSystem)
        node.getScaleController().setMaxScale(0.5f);
        node.getScaleController().setMinScale(0.02f);
        node.renderable = renderable
        node.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
    }

    private fun loadDesainCustom(uiRenderable: ViewRenderable,jenis_desain:String,warna:String){

        if(jenis_desain=="tulisan") {
            var tulisan:TextView
            if(jenis=="cincin"){
                uiRenderable.view.findViewById<ConstraintLayout>(R.id.ar_listClipart_cincin).visibility = View.GONE
                tulisan = uiRenderable.view.findViewById<TextView>(R.id.tvTulisanARCincin)
            }
            else{
                uiRenderable.view.findViewById<ConstraintLayout>(R.id.ar_listClipart_gelang).visibility = View.GONE
                tulisan = uiRenderable.view.findViewById<TextView>(R.id.tvTulisanArTangan)
            }
            val face: Typeface =
                Typeface.createFromAsset(assets, "fonts/" + desain?.font + ".ttf")
            tulisan.setTypeface(face)
            tulisan.setText(desain?.tulisan.toString())
            if(warna=="hitam"){tulisan.setTextColor(Color.BLACK)}
            else {tulisan.setTextColor(Color.WHITE)}
        }
        else{
            //kalau clipart, hilangkan dulu tvnya
            //oast.makeText(context, "clip", Toast.LENGTH_SHORT).show()
            var listClipart = desain?.list_clipart
            if(jenis=="cincin") {
                uiRenderable.view.findViewById<TextView>(R.id.tvTulisanARCincin).visibility = View.GONE
                list_kotak = arrayListOf<ImageView>(
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin1),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin2),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin3),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin4),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin5),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin6),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin7),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipartCincin8)
                )
            }
            else{
                //kalau gelang
                uiRenderable.view.findViewById<TextView>(R.id.tvTulisanArTangan).visibility = View.GONE
                list_kotak = arrayListOf<ImageView>(
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang1),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang2),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang3),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang4),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang5),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang6),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang7),
                    uiRenderable.view.findViewById<ImageView>(R.id.ar_clipGelang8)
                )
            }
            for(kotak in list_kotak){
                kotak.visibility = View.GONE
            }
            for(i in 0..(listClipart?.size)!! -1){
                list_kotak[i].visibility = View.VISIBLE
                var uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+listClipart[i])
                list_kotak[i].setImageURI(uri)
            }
        }
    }
}