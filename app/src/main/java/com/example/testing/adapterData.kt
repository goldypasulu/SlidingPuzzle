package com.example.testing

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import database.User

class adapterData(private val listdata: ArrayList<User>?) : RecyclerView.Adapter<adapterData.ListViewHolder>(){
    inner class ListViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val _nama : TextView = itemView.findViewById(R.id.tvNama)
        val _time : TextView = itemView.findViewById(R.id.tvTime)
        val _btnInisial : Button = itemView.findViewById(R.id.btnInisial)
        val _btnDelete : TextView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var isidata = listdata!!.get(position)

        holder._nama.setText(isidata.nama)
        holder._time.setText(isidata.time)
        holder._btnInisial.setText(isidata.nama?.substring(0,1))
        holder._btnDelete.setOnClickListener{
            Log.d("debunggggggg", isidata.nama.toString())
            listdata.removeAt(position)
            notifyDataSetChanged()

            val db = FirebaseFirestore.getInstance().collection("data_time")
            db.document("${isidata.nama}").delete()
        }

    }

    override fun getItemCount(): Int {
        return listdata!!.size
    }

}