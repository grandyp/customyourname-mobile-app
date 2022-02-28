package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class daftarDesainAdapter(private val listDesain:ArrayList<desain>,fragmentDesain: fragment_desain):
RecyclerView.Adapter<daftarDesainAdapter.ViewHolder>(){

    var fragmentnya = fragmentDesain
    inner class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        var nama = itemView.findViewById<TextView>(R.id.tvNamaDesainku)
        var previewAR = itemView.findViewById<Button>(R.id.btnPreviewDesainku)
        var hapusDesain = itemView.findViewById<Button>(R.id.btnHapusDesainku)
        var spinAksesoris = itemView.findViewById<Spinner>(R.id.spinJenisAksesorisDesainku)
        var shareDesain = itemView.findViewById<Button>(R.id.btnShareDesain)
        var nonAR = itemView.findViewById<TextView>(R.id.tvTidakBisaPreviewArDaftarDesain)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_desain,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nama.text = listDesain[position].nama_desain
        holder.previewAR.setOnClickListener {
            var jenis = holder.spinAksesoris.selectedItem.toString()
            fragmentnya.previewAR(listDesain[position],jenis)
        }

        holder.hapusDesain.setOnClickListener {
            fragmentnya.hapusDesain(listDesain[position].id_desain.toString())
        }

        holder.itemView.setOnClickListener{
            fragmentnya.previewDesain(position)
        }

        holder.shareDesain.setOnClickListener {
            var url = ""
            if(listDesain[position].jenis=="clipart"){
                for(clip in listDesain[position].list_url_clipart!!){
                    url = url+clip+", "
                }
            }
            else{
                url = listDesain[position].url_gambar.toString()
            }
            fragmentnya.shareDesain(url)
        }

        if(listDesain[position].jenis=="upload gambar"){
            holder.previewAR.visibility = View.GONE
            holder.spinAksesoris.visibility = View.GONE
            holder.nonAR.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return listDesain.size
    }
}