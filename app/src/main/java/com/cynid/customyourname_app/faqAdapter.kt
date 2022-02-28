package com.cynid.customyourname_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView

class faqAdapter(private val listFAQ:ArrayList<faq>):
    RecyclerView.Adapter<faqAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var pertanyaan:TextView = itemView.findViewById(R.id.tvPertanyaanFAQ)
        var jawaban:TextView = itemView.findViewById(R.id.tvJawabanFAQ)
        var expandLayout:ConstraintLayout = itemView.findViewById(R.id.expandFAQ)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.pertanyaan.setText(listFAQ.get(position).pertanyaan)
        holder.jawaban.setText(listFAQ.get(position).jawaban)

        //cek diexpand tidak faqnya
        var expanded = listFAQ.get(position).expanded
        holder.expandLayout.visibility = if(expanded) View.VISIBLE else View.GONE

        holder.pertanyaan.setOnClickListener {
            if(listFAQ.get(position).expanded == false){
                listFAQ.get(position).expanded = true
            }else{
                listFAQ.get(position).expanded = false
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return listFAQ.size
    }
}