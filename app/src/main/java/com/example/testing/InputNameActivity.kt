package com.example.testing

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import database.User

class InputNameActivity : AppCompatActivity() {

    var arData : ArrayList<User>? = arrayListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_name)
        setTitle("Sliding Puzzle")

        var _etInputName = findViewById<EditText>(R.id.etInputName)
        var _btnInputName = findViewById<Button>(R.id.btnInputName)
        var _btnLeaderboard = findViewById<Button>(R.id.btnLeadeboard)

        val tempBestTime = "Not Finished"
        var playerName = _etInputName.text

        //initiate Firestore
        val dbFS = FirebaseFirestore.getInstance()
        val newDocRef = dbFS.collection("data_time").document()
        val documentId = newDocRef.id

        _etInputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                _btnInputName.isEnabled = true

                if (_etInputName.text.isEmpty()){
                    _btnInputName.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
                // This method is called after the text is changed
            }
        })

        _btnInputName.setOnClickListener{

            Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()

            val dataUser = User(
                playerName.toString(),
                tempBestTime
            )

            arData?.add(dataUser)

            newDocRef.set(dataUser)

            val eIntent = Intent(this@InputNameActivity, MainActivity::class.java).apply {
                putExtra("DATA", arData)
                putExtra("DOC_ID", documentId)
            }

            startActivity(eIntent)
            finish()
        }

        _btnLeaderboard.setOnClickListener{
            val eIntent = Intent(this@InputNameActivity, MainActivity2::class.java)
            startActivity(eIntent)
            finish()
        }
    }
}