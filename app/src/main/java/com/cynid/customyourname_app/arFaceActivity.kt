package com.cynid.customyourname_app

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.Renderable
import kotlinx.android.synthetic.main.activity_ar_face.*

class arFaceActivity : AppCompatActivity() {

    companion object{
        const val MIN_OPENGL_VERSION = 3.0
    }

    lateinit var arFragment:FaceArFragment
    var faceNodeMap = HashMap<AugmentedFace,CustomFaceNode>()
    lateinit var desain:desain
    lateinit var jenis:String
    lateinit var listNamaAsset:ArrayList<String>
    lateinit var listAsset:ArrayList<asset>
    var awal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!cekSupport()){
            return
        }
        setContentView(R.layout.activity_ar_face)
        arFragment = face_fragment as FaceArFragment
        desain = intent.getParcelableExtra<desain>("extra_desain")!!
        jenis = intent.getStringExtra("extra_jenis").toString()
        listNamaAsset = intent.getStringArrayListExtra("extra_namaasset")!!
        listAsset = intent.getParcelableArrayListExtra<asset>("extra_asset")!!

        btnBackPreviewAR.setOnClickListener {
            val keDesain = Intent(this,MainActivity::class.java)
            keDesain.putExtra("extra_nomorfragment",2)
            startActivity(keDesain)
        }

        if(jenis=="anting"){
            spinNamaAssetFace.visibility= View.GONE
            btnTerapkanARFace.visibility = View.GONE
        }

        var adapter = ArrayAdapter(this,android.R.layout.simple_spinner_item,listNamaAsset)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinNamaAssetFace.adapter = adapter


        btnHelpARFace.setOnClickListener{
            popupawal()
        }

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener{
            sceneView.session
                ?.getAllTrackables(AugmentedFace::class.java)?.let {
                    for (i in it){
                        if(!faceNodeMap.containsKey(i)){
                            val faceNode = CustomFaceNode(i,this,desain,jenis,
                                btnHitamArFace,btnPutihArFace2,spinNamaAssetFace,listAsset,btnTerapkanARFace)
                            faceNode.setParent(scene)
                            faceNodeMap.put(i,faceNode)
                        }
                    }

                    val ctr = faceNodeMap.entries.iterator()
                    while (ctr.hasNext()){
                        val entry = ctr.next()
                        val face = entry.key
                        if(face.trackingState== TrackingState.STOPPED){
                            val faceNode = entry.value
                            faceNode.setParent(null)
                            ctr.remove()
                        }
                    }
                }
        }
    }

    private fun popupawal(){
        val view = LayoutInflater.from(this).inflate(R.layout.popup_attention_ar,null)
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
        //tampilkan
        view.setOnClickListener{popupWindow.dismiss()}
        popupWindow.showAtLocation(layout_ar_face, Gravity.CENTER,0,0)
    }

    private fun cekSupport() : Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Kamu perlu download Google Play Services for AR", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString =  (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.deviceConfigurationInfo
            ?.glEsVersion

        openGlVersionString?.let { s ->
            if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
                Toast.makeText(this, "Versi OpenGL kamu tidak support", Toast.LENGTH_LONG)
                    .show()
                finish()
                return false
            }
        }
        return true
    }
}