package com.cynid.customyourname_app

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginTop
import com.bumptech.glide.Glide
import com.google.ar.core.AugmentedFace
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.AugmentedFaceNode

class CustomFaceNode(augmentedFace: AugmentedFace?,
                     val context: Context,desainnya: desain?,jenis_aksesoris:String,hitam:Button,putih:Button
                     ,spinner:Spinner,listAsset:ArrayList<asset>,btnTerapkan:Button
): AugmentedFaceNode(augmentedFace) {

    private var telingaKiri: Node? = null
    private var telingaKanan: Node? = null
    private var leher: Node? = null
    var desain = desainnya
    var jenis_aksesoris = jenis_aksesoris
    var btnHitam = hitam
    var btnPutih = putih
    var spinnerAsset = spinner
    var listAsset = listAsset
    var btnTerapkan = btnTerapkan

        companion object {
        enum class FaceRegion {
            TELINGA_KIRI,
            TELINGA_KANAN,
            LEHER
        }
    }


    override fun onActivate() {
        super.onActivate()
        telingaKiri = Node()
        telingaKiri?.setParent(this)

        telingaKanan = Node()
        telingaKanan?.setParent(this)

        leher = Node()
        leher?.setParent(this)


        if(jenis_aksesoris == "kalung") {
            loadKalung("hitam")
        }
        else{
            loadAnting("hitam")
        }
        btnHitam.setOnClickListener {
            loadWarna("hitam")
        }
        btnPutih.setOnClickListener {
            loadWarna("putih")
        }
        btnTerapkan.setOnClickListener {
            loadAsset("hjitam",spinnerAsset.selectedItemPosition)
        }

        /*
        spinnerAsset.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                //loadAsset(p2)
                Toast.makeText(context, "tes", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }*/
    }

    private fun loadWarna(warna:String){
        if(jenis_aksesoris == "kalung") {
            loadAsset(warna,spinnerAsset.selectedItemPosition)
        }
        else{
            loadAnting(warna)
        }
    }

    private fun loadAsset(warna:String,index:Int){
        ViewRenderable.builder()
            .setView(context, R.layout.ar_aksesoris)
            .build()
            .thenAccept { uiRenderable: ViewRenderable ->
                uiRenderable.isShadowCaster = false
                uiRenderable.isShadowReceiver = false
                leher?.renderable = uiRenderable
                loadDesain(uiRenderable,"kalung",desain?.jenis.toString(),warna)
                var kiri = uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kiri)
                var kanan = uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan)
                var kanan2 = uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan2)
                var layoutkiri = kiri.layoutParams as ConstraintLayout.LayoutParams
                layoutkiri.topMargin = listAsset[index].posisiy!!.toInt()
                var layoutkanan = kiri.layoutParams as ConstraintLayout.LayoutParams
                layoutkanan.topMargin = listAsset[index].posisiy!!.toInt()
                var layoutkanan2 = kiri.layoutParams as ConstraintLayout.LayoutParams
                layoutkanan2.topMargin = listAsset[index].posisiy!!.toInt()
                Glide.with(context).load(listAsset[index].url1).into(kiri)
                Glide.with(context).load(listAsset[index].url2).into(kanan)
                Glide.with(context).load(listAsset[index].url2).into(kanan2)
            }
            .exceptionally { throwable: Throwable? ->
                throw AssertionError(
                    "Could not create ui element",
                    throwable
                )
            }
    }

    private fun loadKalung(warna:String){
        ViewRenderable.builder()
            .setView(context, R.layout.ar_aksesoris)
            .build()
            .thenAccept { uiRenderable: ViewRenderable ->
                uiRenderable.isShadowCaster = false
                uiRenderable.isShadowReceiver = false
                leher?.renderable = uiRenderable
                loadDesain(uiRenderable,"kalung",desain?.jenis.toString(),warna)
            }
            .exceptionally { throwable: Throwable? ->
                throw AssertionError(
                    "Could not create ui element",
                    throwable
                )
            }
    }

    private fun loadAnting(warna:String){
        ViewRenderable.builder()
            .setView(context, R.layout.ar_aksesoris)
            .build()
            .thenAccept { uiRenderable: ViewRenderable ->
                uiRenderable.isShadowCaster = false
                uiRenderable.isShadowReceiver = false
                telingaKiri?.renderable = uiRenderable
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kiri).visibility = View.GONE //hilangkan kalung
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan).visibility = View.GONE //hilangkan kalung
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan2).visibility = View.GONE //hilangkan kalung
                loadDesain(uiRenderable,"anting_kiri",desain?.jenis.toString(),warna)
            }
            .exceptionally { throwable: Throwable? ->
                throw AssertionError(
                    "Could not create ui element",
                    throwable
                )
            }
        ViewRenderable.builder()
            .setView(context, R.layout.ar_aksesoris)
            .build()
            .thenAccept { uiRenderable: ViewRenderable ->
                uiRenderable.isShadowCaster = false
                uiRenderable.isShadowReceiver = false
                telingaKanan?.renderable = uiRenderable
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kiri).visibility = View.GONE //hilangkan kalung
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan).visibility = View.GONE //hilangkan kalung
                uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan2).visibility = View.GONE //hilangkan kalung
                loadDesain(uiRenderable,"anting_kanan",desain?.jenis.toString(),warna)
            }
            .exceptionally { throwable: Throwable? ->
                throw AssertionError(
                    "Could not create ui element",
                    throwable
                )
            }
    }

    private fun loadDesain(uiRenderable: ViewRenderable,jenis_aksesoris:String,jenis_desain:String,warna:String){
        if(jenis_desain.toString()=="tulisan") {
            //Toast.makeText(context, jenis_desain, Toast.LENGTH_SHORT).show()
            uiRenderable.view.findViewById<ConstraintLayout>(R.id.ar_listClipart).visibility = View.GONE
            uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan).visibility = View.GONE
            var tulisan = uiRenderable.view.findViewById<TextView>(R.id.tvDesainTulisanAR)
            val face: Typeface =
                Typeface.createFromAsset(context.assets, "fonts/" + desain?.font + ".ttf")
            tulisan.setTypeface(face)
            tulisan.setText(desain?.tulisan.toString())
            if(warna=="putih"){tulisan.setTextColor(Color.WHITE)}
            else{tulisan.setTextColor(Color.BLACK)}
        }
        else{
            //kalau clipart, hilangkan dulu tvnya
            //oast.makeText(context, "clip", Toast.LENGTH_SHORT).show()
            uiRenderable.view.findViewById<TextView>(R.id.tvDesainTulisanAR).visibility = View.GONE
            uiRenderable.view.findViewById<ImageView>(R.id.ar_aksesoris_kanan2).visibility = View.INVISIBLE
            var listClipart = desain?.list_clipart
            var list_kotak = arrayListOf<ImageView>(
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip1),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip2),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip3),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip4),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip5),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip6),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip7),
                uiRenderable.view.findViewById<ImageView>(R.id.ar_clip8)
            )
            for(kotak in list_kotak){
                kotak.visibility = View.GONE
            }
            for(i in 0..(listClipart?.size)!! -1){
                list_kotak[i].visibility = View.VISIBLE
                var uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+listClipart[i])
                if(warna=="putih"){
                    uri = Uri.parse("android.resource://com.cynid.customyourname_app/drawable/"+listClipart[i]+"_p")
                }
                list_kotak[i].setImageURI(uri)
            }
        }
    }

    private fun getRegionPose(region: FaceRegion) : Vector3? {
        val buffer = augmentedFace?.meshVertices
        if (buffer != null) {
            return when (region) {
                FaceRegion.TELINGA_KIRI->
                    Vector3(buffer.get(356*3),buffer.get(356 * 3 + 1),  buffer.get(356 * 3 + 2))
                FaceRegion.TELINGA_KANAN ->
                    Vector3(buffer.get(127 * 3),buffer.get(127 * 3 + 1),  buffer.get(127 * 3 + 2))
                FaceRegion.LEHER ->
                    Vector3(buffer.get(152 * 3),
                        buffer.get(152 * 3 + 1),
                        buffer.get(152 * 3 + 2))
            }
        }
        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        super.onUpdate(frameTime)
        augmentedFace?.let {face ->
            getRegionPose(FaceRegion.TELINGA_KIRI)?.let {
                telingaKiri?.localPosition = Vector3(it.x, it.y - 0.04f, it.z - 0.035f)
                telingaKiri?.localScale = Vector3(0.03f, 0.03f, 0.03f)
                telingaKiri?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, -5.0f), -10f)
            }

            getRegionPose(FaceRegion.TELINGA_KANAN)?.let {
                telingaKanan?.localPosition = Vector3(it.x, it.y - 0.035f, it.z - 0.035f)
                telingaKanan?.localScale = Vector3(0.03f, 0.03f, 0.03f)
                telingaKanan?.localRotation = Quaternion.axisAngle(Vector3(0.0f, 0.0f, -5.5f), 10f)
            }

            getRegionPose(FaceRegion.LEHER)?.let {
                leher?.localPosition = Vector3(it.x, it.y-0.1f, it.z + 0.015f)
                leher?.localScale = Vector3(0.07f, 0.07f, 0.07f)
            }
        }

    }
}