package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class messageAdapter(private val listPesan:ArrayList<message>):
RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class sayaPengirim(itemView:View): RecyclerView.ViewHolder(itemView){
        var pesan:TextView = itemView.findViewById(R.id.tvMessageSaya)
        var tgl:TextView = itemView.findViewById(R.id.tvTglChat)
        var jam:TextView = itemView.findViewById(R.id.tvJamChat)
        var status:ImageView = itemView.findViewById(R.id.tvStatusChat)
        //var jamPesan:TextView = itemView.findViewById(R.id.tvWaktuMessageSaya)
        fun bind(pesanSaya:message){
            pesan.setText(pesanSaya.pesan.toString())
            tgl.setText(pesanSaya.tanggal.toString())
            jam.setText(pesanSaya.jam.toString())
            if(pesanSaya.status=="sent") {
                status.setImageResource(R.drawable.ic_baseline_check_24_sent)
            }
            else{
                status.setImageResource(R.drawable.ic_baseline_check_24)
            }
        }
    }

    inner class orangPengirim(itemView: View):RecyclerView.ViewHolder(itemView){
        var pesan:TextView = itemView.findViewById(R.id.tvChatOrang)
        var tgl:TextView = itemView.findViewById(R.id.tvTglChatOrang)
        var jam:TextView = itemView.findViewById(R.id.tvJamChatOrang)
        //ar jamPesan:TextView = itemView.findViewById(R.id.tvWaktuMessageOrang)
        fun bind(pesanSaya:message){
            pesan.setText(pesanSaya.pesan.toString())
            tgl.setText(pesanSaya.tanggal.toString())
            jam.setText(pesanSaya.jam.toString())
        }
    }

    override fun getItemViewType(position: Int): Int {
        val pesan = listPesan[position]
        val uid = Firebase.auth.currentUser?.uid
        if(pesan.id_pengirim==uid){
            //saya yang ngirim
            return 1
        }
        else{return 2}
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType==1){
            //sy ngirim
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_saya,parent,false)
            return sayaPengirim(view)
        }else{
            //orang yang ngirim
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_orang,parent,false)
            return orangPengirim(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val pesan = listPesan.get(position)
        if(holder.itemViewType==1){
            (holder as sayaPengirim).bind(pesan)
        }
        else if(holder.itemViewType==2){
            (holder as orangPengirim).bind(pesan)
        }
    }

    override fun getItemCount(): Int {
        return listPesan.size
    }

}