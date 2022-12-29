package com.example.testing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import database.User

class MainActivity2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var userArrayList : ArrayList<User>
    private lateinit var adapterData_ : adapterData
    private lateinit var dbFS : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setTitle("Leaderboard")

        recyclerView = findViewById(R.id.rvData)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userArrayList = arrayListOf()
        adapterData_ = adapterData(userArrayList)
        recyclerView.adapter = adapterData_
        dbFS = FirebaseFirestore.getInstance()
        val query = dbFS.collection("data_time").orderBy("time",Query.Direction.ASCENDING)
        DataChangeListener()
    }

    //Get data from firestore
    private fun DataChangeListener(){
        dbFS = FirebaseFirestore.getInstance()
        val query = dbFS.collection("data_time").orderBy("time",Query.Direction.ASCENDING)
        query.addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?)
            {
                if (error != null){
                    Log.e("Firestore Error", error.message.toString())
                }

                for (dc : DocumentChange in value?.documentChanges!!){

                    if (dc.type == DocumentChange.Type.ADDED){
                        userArrayList.add(dc.document.toObject(User::class.java))
                    }

                }

                adapterData_.notifyDataSetChanged()

            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val eIntent = Intent(this@MainActivity2, InputNameActivity::class.java).apply {
        }
        startActivity(eIntent)
        finish()
    }
}