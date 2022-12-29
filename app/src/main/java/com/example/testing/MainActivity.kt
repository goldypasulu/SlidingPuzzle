package com.example.testing

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.testing.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import database.User
import java.util.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // ini variable buat time
    private lateinit var binding: ActivityMainBinding
    private var timerStarted = false
    private lateinit var serviceIntent : Intent
    private var time = 0.0

    private lateinit var documentId : String

    lateinit var _textView : TextView
    lateinit var _textView2 : TextView

    var arData : ArrayList<User>? = arrayListOf<User>()
    private lateinit var dbFS : FirebaseFirestore

    var arr_button = intArrayOf(R.id.button, R.id.button2, R.id.button3,
        R.id.button4, R.id.button5, R.id.button6,
        R.id.button7, R.id.button8, R.id.button9)

    val value = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 0) //buat assign value nya button
    val win = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 0) //buat cek dengan arr value dan recoloring button
    val num = arrayOf(0, 1, 1, 1, 1, 1, 1, 1, 1) //buat cek button yang invisible

    //val value = arrayOf(0, 4, 8, 3, 2, 1, 6, 7, 5) //ini untuk ngetes biar bisa win
    /*
    Untuk demonstrasi supaya win, gunakan array value yang sudah disiapkan (udah tau caranya soalnya)
    jangan lupa value.shuffle nya di comment

    Nah, jika sudah win menggunakan value yang sudah disiapkan, reset button memang tidak bisa digunakan
    karena value nya sudah sama dengan win, dan shufflenya kita comment

    Tapi kalo win nya menggunakan shuffle-an dari function initialize game, bisa di reset kok gamenya
    */

    //ALERT DIALOG POSITIVE AND NEGATIVE
    val positiveButtonClick = {
            dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext, android.R.string.yes, Toast.LENGTH_SHORT).show()
        val eIntent = Intent(this@MainActivity, MainActivity2::class.java).apply {
        }
        startActivity(eIntent)
        finish()
    }

    val negativeButtonClick = {
            dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext, android.R.string.no, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ini fungsi fungsi buat time
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(applicationContext,TimerSevice::class.java)
        registerReceiver(updateTime, IntentFilter(TimerSevice.TIMER_UPDATED))

        //GET INTENT FROM INPUT ACTIVITY
        var playerName = ""

        if (intent.extras!=null) {
            if (intent.getParcelableArrayListExtra<User>("DATA") != null) {
                arData = (intent.getParcelableArrayListExtra<User>("DATA") as ArrayList<User>)
            }
            documentId = (intent.getStringExtra("DOC_ID") as String)
        }

        playerName = arData!![0].nama!!
        title = "Selamat bermain $playerName" //SET TITLE BAR

        //buat puzzlenya
        var _button : Button = findViewById(R.id.button)
        var _button2 : Button = findViewById(R.id.button2)
        var _button3 : Button = findViewById(R.id.button3)
        var _button4 : Button = findViewById(R.id.button4)
        var _button5 : Button = findViewById(R.id.button5)
        var _button6 : Button = findViewById(R.id.button6)
        var _button7 : Button = findViewById(R.id.button7)
        var _button8 : Button = findViewById(R.id.button8)
        var _button9 : Button = findViewById(R.id.button9)
        _textView = findViewById(R.id.textView) //untuk cek, nanti hapus
        _textView2 = findViewById(R.id.textView2)//untuk cek, nanti hapus

        initialize_game()

        //start stop dan reset button
        binding.buttonStartStop.setOnClickListener{
            startStopTimer()

            for (i in arr_button) {
                var getbutton : Button = findViewById(i)
                getbutton.isEnabled = true //enable button karena sudah start
            }

            binding.buttonStartStop.isEnabled = false
            binding.buttonStartStop.visibility = View.INVISIBLE

            binding.buttonReset.isEnabled = true
            binding.buttonReset.visibility = View.VISIBLE
        }

        binding.buttonReset.setOnClickListener {
            resetTimer()

            initialize_game()

            binding.buttonStartStop.isEnabled = true
            binding.buttonStartStop.visibility = View.VISIBLE

            binding.buttonReset.isEnabled = false
            binding.buttonReset.visibility = View.INVISIBLE
        }


        //pengecekan tiap button, main game
        _button.setOnClickListener{
            if(num.get(1) == 0){
                //invis button 1
                _button.visibility = View.INVISIBLE

                //vis button 2
                _button2.visibility = View.VISIBLE
                _button2.text = value.get(0).toString()

                //change num array
                num.set(0, 0)
                num.set(1, 1)

                //change value array
                value[1] = value[0] //dipindah dulu baru di set = 0
                value[0] = 0

                //change color if in right position
                if(value.elementAt(1) == win.elementAt(1)){
                    _button2.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(1) != win.elementAt(1)){
                    _button2.setBackgroundColor(Color.RED)
                }
            } else if(num.get(3) == 0){
                //invis button 1
                _button.visibility = View.INVISIBLE

                //vis button 4
                _button4.visibility = View.VISIBLE
                _button4.text = value.get(0).toString()

                //change num array
                num.set(0, 0)
                num.set(3, 1)

                //change value array
                value[3] = value[0] //dipindah dulu baru di set = 0
                value[0] = 0

                //change color if in right position
                if(value.elementAt(3) == win.elementAt(3)){
                    _button4.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(3) != win.elementAt(3)){
                    _button4.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button2.setOnClickListener{
            if(num.get(0) == 0){
                //invis button 2
                _button2.visibility = View.INVISIBLE

                //vis button 1
                _button.visibility = View.VISIBLE
                _button.text = value.get(1).toString()

                //change num array
                num.set(1, 0)
                num.set(0, 1)

                //change value array
                value[0] = value[1] //dipindah dulu baru di set = 0
                value[1] = 0

                //change color if in right position
                if(value.elementAt(0) == win.elementAt(0)){
                    _button.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(0) != win.elementAt(0)){
                    _button.setBackgroundColor(Color.RED)
                }
            } else if(num.get(2) == 0){
                //invis button 2
                _button2.visibility = View.INVISIBLE

                //vis button 3
                _button3.visibility = View.VISIBLE
                _button3.text = value.get(1).toString()

                //change num array
                num.set(1, 0)
                num.set(2, 1)

                //change value array
                value[2] = value[1] //dipindah dulu baru di set = 0
                value[1] = 0

                //change color if in right position
                if(value.elementAt(2) == win.elementAt(2)){
                    _button3.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(2) != win.elementAt(2)){
                    _button3.setBackgroundColor(Color.RED)
                }
            } else if(num.get(4) == 0){
                //invis button 2
                _button2.visibility = View.INVISIBLE

                //vis button 5
                _button5.visibility = View.VISIBLE
                _button5.text = value.get(1).toString()

                //change num array
                num.set(1, 0)
                num.set(4, 1)

                //change value array
                value[4] = value[1] //dipindah dulu baru di set = 0
                value[1] = 0

                //change color if in right position
                if(value.elementAt(4) == win.elementAt(4)){
                    _button5.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(4) != win.elementAt(4)){
                    _button5.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button3.setOnClickListener{
            if(num.get(1) == 0){
                //invis button 3
                _button3.visibility = View.INVISIBLE

                //vis button 2
                _button2.visibility = View.VISIBLE
                _button2.text = value.get(2).toString()

                //change num array
                num.set(2, 0)
                num.set(1, 1)

                //change value array
                value[1] = value[2] //dipindah dulu baru di set = 0
                value[2] = 0

                //change color if in right position
                if(value.elementAt(1) == win.elementAt(1)){
                    _button2.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(1) != win.elementAt(1)){
                    _button2.setBackgroundColor(Color.RED)
                }
            } else if(num.get(5) == 0){
                //invis button 3
                _button3.visibility = View.INVISIBLE

                //vis button 6
                _button6.visibility = View.VISIBLE
                _button6.text = value.get(2).toString()

                //change num array
                num.set(2, 0)
                num.set(5, 1)

                //change value array
                value[5] = value[2] //dipindah dulu baru di set = 0
                value[2] = 0

                //change color if in right position
                if(value.elementAt(5) == win.elementAt(5)){
                    _button6.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(5) != win.elementAt(5)){
                    _button6.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button4.setOnClickListener{
            if(num.get(0) == 0){
                //invis button 4
                _button4.visibility = View.INVISIBLE

                //vis button 1
                _button.visibility = View.VISIBLE
                _button.text = value.get(3).toString()

                //change num array
                num.set(3, 0)
                num.set(0, 1)

                //change value array
                value[0] = value[3] //dipindah dulu baru di set = 0
                value[3] = 0

                //change color if in right position
                if(value.elementAt(0) == win.elementAt(0)){
                    _button.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(0) != win.elementAt(0)){
                    _button.setBackgroundColor(Color.RED)
                }
            } else if(num.get(4) == 0){
                //invis button 4
                _button4.visibility = View.INVISIBLE

                //vis button 5
                _button5.visibility = View.VISIBLE
                _button5.text = value.get(3).toString()

                //change num array
                num.set(3, 0)
                num.set(4, 1)

                //change value array
                value[4] = value[3] //dipindah dulu baru di set = 0
                value[3] = 0

                //change color if in right position
                if(value.elementAt(4) == win.elementAt(4)){
                    _button5.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(4) != win.elementAt(4)){
                    _button5.setBackgroundColor(Color.RED)
                }
            } else if(num.get(6) == 0){
                //invis button 4
                _button4.visibility = View.INVISIBLE

                //vis button 7
                _button7.visibility = View.VISIBLE
                _button7.text = value.get(3).toString()

                //change num array
                num.set(3, 0)
                num.set(6, 1)

                //change value array
                value[6] = value[3] //dipindah dulu baru di set = 0
                value[3] = 0

                //change color if in right position
                if(value.elementAt(6) == win.elementAt(6)){
                    _button7.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(6) != win.elementAt(6)){
                    _button7.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button5.setOnClickListener{
            if(num.get(1) == 0){
                //invis button 5
                _button5.visibility = View.INVISIBLE

                //vis button 2
                _button2.visibility = View.VISIBLE
                _button2.text = value.get(4).toString()

                //change num array
                num.set(4, 0)
                num.set(1, 1)

                //change value array
                value[1] = value[4] //dipindah dulu baru di set = 0
                value[4] = 0

                //change color if in right position
                if(value.elementAt(1) == win.elementAt(1)){
                    _button2.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(1) != win.elementAt(1)){
                    _button2.setBackgroundColor(Color.RED)
                }
            } else if(num.get(3) == 0){
                //invis button 5
                _button5.visibility = View.INVISIBLE

                //vis button 4
                _button4.visibility = View.VISIBLE
                _button4.text = value.get(4).toString()

                //change num array
                num.set(4, 0)
                num.set(3, 1)

                //change value array
                value[3] = value[4] //dipindah dulu baru di set = 0
                value[4] = 0

                //change color if in right position
                if(value.elementAt(3) == win.elementAt(3)){
                    _button4.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(3) != win.elementAt(3)){
                    _button4.setBackgroundColor(Color.RED)
                }
            } else if(num.get(5) == 0){
                //invis button 5
                _button5.visibility = View.INVISIBLE

                //vis button 6
                _button6.visibility = View.VISIBLE
                _button6.text = value.get(4).toString()

                //change num array
                num.set(4, 0)
                num.set(5, 1)

                //change value array
                value[5] = value[4] //dipindah dulu baru di set = 0
                value[4] = 0

                //change color if in right position
                if(value.elementAt(5) == win.elementAt(5)){
                    _button6.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(5) != win.elementAt(5)){
                    _button6.setBackgroundColor(Color.RED)
                }
            } else if(num.get(7) == 0){
                //invis button 5
                _button5.visibility = View.INVISIBLE

                //vis button 8
                _button8.visibility = View.VISIBLE
                _button8.text = value.get(4).toString()

                //change num array
                num.set(4, 0)
                num.set(7, 1)

                //change value array
                value[7] = value[4] //dipindah dulu baru di set = 0
                value[4] = 0

                //change color if in right position
                if(value.elementAt(7) == win.elementAt(7)){
                    _button8.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(7) != win.elementAt(7)){
                    _button8.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button6.setOnClickListener{
            if(num.get(2) == 0){
                //invis button 6
                _button6.visibility = View.INVISIBLE

                //vis button 3
                _button3.visibility = View.VISIBLE
                _button3.text = value.get(5).toString()

                //change num array
                num.set(5, 0)
                num.set(2, 1)

                //change value array
                value[2] = value[5] //dipindah dulu baru di set = 0
                value[5] = 0

                //change color if in right position
                if(value.elementAt(2) == win.elementAt(2)){
                    _button3.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(2) != win.elementAt(2)){
                    _button3.setBackgroundColor(Color.RED)
                }
            } else if(num.get(4) == 0){
                //invis button 6
                _button6.visibility = View.INVISIBLE

                //vis button 5
                _button5.visibility = View.VISIBLE
                _button5.text = value.get(5).toString()

                //change num array
                num.set(5, 0)
                num.set(4, 1)

                //change value array
                value[4] = value[5] //dipindah dulu baru di set = 0
                value[5] = 0

                //change color if in right position
                if(value.elementAt(4) == win.elementAt(4)){
                    _button5.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(4) != win.elementAt(4)){
                    _button5.setBackgroundColor(Color.RED)
                }
            } else if(num.get(8) == 0){
                //invis button 6
                _button6.visibility = View.INVISIBLE

                //vis button 9
                _button9.visibility = View.VISIBLE
                _button9.text = value.get(5).toString()

                //change num array
                num.set(5, 0)
                num.set(8, 1)

                //change value array
                value[8] = value[5] //dipindah dulu baru di set = 0
                value[5] = 0

                //change color if in right position
                if(value.elementAt(8) == win.elementAt(8)){
                    _button9.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(8) != win.elementAt(8)){
                    _button9.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button7.setOnClickListener{
            if(num.get(3) == 0){
                //invis button 7
                _button7.visibility = View.INVISIBLE

                //vis button 4
                _button4.visibility = View.VISIBLE
                _button4.text = value.get(6).toString()

                //change num array
                num.set(6, 0)
                num.set(3, 1)

                //change value array
                value[3] = value[6] //dipindah dulu baru di set = 0
                value[6] = 0

                //change color if in right position
                if(value.elementAt(3) == win.elementAt(3)){
                    _button4.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(3) != win.elementAt(3)){
                    _button4.setBackgroundColor(Color.RED)
                }
            } else if(num.get(7) == 0){
                //invis button 7
                _button7.visibility = View.INVISIBLE

                //vis button 8
                _button8.visibility = View.VISIBLE
                _button8.text = value.get(6).toString()

                //change num array
                num.set(6, 0)
                num.set(7, 1)

                //change value array
                value[7] = value[6] //dipindah dulu baru di set = 0
                value[6] = 0

                //change color if in right position
                if(value.elementAt(7) == win.elementAt(7)){
                    _button8.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(7) != win.elementAt(7)){
                    _button8.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button8.setOnClickListener{
            if(num.get(4) == 0){
                //invis button 8
                _button8.visibility = View.INVISIBLE

                //vis button 5
                _button5.visibility = View.VISIBLE
                _button5.text = value.get(7).toString()

                //change num array
                num.set(7, 0)
                num.set(4, 1)

                //change value array
                value[4] = value[7] //dipindah dulu baru di set = 0
                value[7] = 0

                //change color if in right position
                if(value.elementAt(4) == win.elementAt(4)){
                    _button5.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(4) != win.elementAt(4)){
                    _button5.setBackgroundColor(Color.RED)
                }
            } else if(num.get(6) == 0){
                //invis button 8
                _button8.visibility = View.INVISIBLE

                //vis button 7
                _button7.visibility = View.VISIBLE
                _button7.text = value.get(7).toString()

                //change num array
                num.set(7, 0)
                num.set(6, 1)

                //change value array
                value[6] = value[7] //dipindah dulu baru di set = 0
                value[7] = 0

                //change color if in right position
                if(value.elementAt(6) == win.elementAt(6)){
                    _button7.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(6) != win.elementAt(6)){
                    _button7.setBackgroundColor(Color.RED)
                }
            } else if(num.get(8) == 0){
                //invis button8
                _button8.visibility = View.INVISIBLE

                //vis button 9
                _button9.visibility = View.VISIBLE
                _button9.text = value.get(7).toString()

                //change num array
                num.set(7, 0)
                num.set(8, 1)

                //change value array
                value[8] = value[7] //dipindah dulu baru di set = 0
                value[7] = 0

                //change color if in right position
                if(value.elementAt(8) == win.elementAt(8)){
                    _button9.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(8) != win.elementAt(8)){
                    _button9.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }

        _button9.setOnClickListener{
            if(num.get(7) == 0){
                //invis button 9
                _button9.visibility = View.INVISIBLE

                //vis button 8
                _button8.visibility = View.VISIBLE
                _button8.text = value.get(8).toString()

                //change num array
                num.set(8, 0)
                num.set(7, 1)

                //change value array
                value[7] = value[8] //dipindah dulu baru di set = 0
                value[8] = 0

                //change color if in right position
                if(value.elementAt(7) == win.elementAt(7)){
                    _button8.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(7) != win.elementAt(7)){
                    _button8.setBackgroundColor(Color.RED)
                }
            } else if(num.get(5) == 0){
                //invis button 8
                _button9.visibility = View.INVISIBLE

                //vis button 6
                _button6.visibility = View.VISIBLE
                _button6.text = value.get(8).toString()

                //change num array
                num.set(8, 0)
                num.set(5, 1)

                //change value array
                value[5] = value[8] //dipindah dulu baru di set = 0
                value[8] = 0

                //change color if in right position
                if(value.elementAt(5) == win.elementAt(5)){
                    _button6.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(5) != win.elementAt(5)){
                    _button6.setBackgroundColor(Color.RED)
                }
            }

            checkWin()
        }
    }


    fun checkWin(){
        var counter : Int = 0
        for (i in 0..8){
            if(value.elementAt(i) == win.elementAt(i)){
                counter+=1
            }
        }
        if(counter == 9){
            for (i in arr_button) {
                var getbutton: Button = findViewById(i)
                getbutton.isEnabled = false
            }

            var _bestTime = getTimerStringFromDouble(time)

            _textView.text = "Menang! Hore. Timer Stopped"

            stopService(serviceIntent)
            timerStarted = false

            //UPDATE BEST TIME IN FIRESTORE
            dbFS = FirebaseFirestore.getInstance()
            val docRef = dbFS.document("data_time/$documentId")
            docRef.update("time", "$_bestTime")

            alert_dialog()
        }

        _textView.text = Arrays.toString(win)
        _textView2.text = Arrays.toString(value)
    }

    fun initialize_game(){
        //solveable algorithm

        //kalo kondisi nya tidak solveable, app tidak akan muncul karena stuck di while loop

        var loop : Boolean = true
        var count : Int = 0
        while(loop){
            //value.shuffle()

            //dari sini tambahan baru
            //random value
            for (i in 0..8){
                if(i != 8) {
                    val rnds = (1..100).random()
                    value[i] = rnds
                } else if (i == 8){
                    value[i] = 0
                }
            }

            for (i in 0..8){
                if(i != 0) {
                    num[i] = 1
                    win[i] = value[i]
                } else if (i == 0){
                    num[i] = 0
                    win[i] = value[i]
                }
            }

            value.shuffle()
            win.sort()
            //rearrange win nya
            for (i in 0..7){
                win[i] = win[i+1]
            }
            //0 di akhir array
            win[8] = 0
            //dari sini tambahan

            //notes shuffle: shuffle nya bakal sama terus pada saat di run pertama kali
            //kalo reset game, bakal beda kok
            count = 0

            for (i in 0..7){
                for (j in i+1..8){
                    if (value.elementAt(i) != 0 && value.elementAt(j) != 0 && value.elementAt(i) > value.elementAt(j)){
                        count+=1
                    }
                }
            }

            if(count % 2 == 0){
                loop = false
            }
        }
        //print value array
        _textView.text = Arrays.toString(value)
        _textView2.text = count.toString()

        //initialize game
        //edit num array (lihat posisi yang kosong nya)
        for (i in 0..8){
            if(value.elementAt(i) == 0){
                num.set(i, 0)
            } else if(value.elementAt(i) != 0){
                num.set(i, 1)
            }
        }

        //kasi value ke tiap button
        var index_initialize : Int = 0
        for (i in arr_button) {
            var getbutton : Button = findViewById(i)
            if(value.elementAt(index_initialize) == 0){
                getbutton.text = (value.elementAt(index_initialize)).toString()
                getbutton.visibility = View.INVISIBLE
            } else if(value.elementAt(index_initialize) != 0){
                getbutton.text = (value.elementAt(index_initialize)).toString()
                getbutton.visibility = View.VISIBLE
                //change color if in right position
                if(value.elementAt(index_initialize) == win.elementAt(index_initialize)){
                    getbutton.setBackgroundColor(Color.GREEN)
                } else if(value.elementAt(index_initialize) != win.elementAt(index_initialize)){
                    getbutton.setBackgroundColor(Color.RED)
                }
            }
            index_initialize+=1

            getbutton.isEnabled = false //klik start button dulu, baru button di enable
        }
    }

    // dibawah ini buat time nya
    private fun resetTimer()
    {
        stopTimer()
        time = 0.0
        binding.textTimer.text = getTimerStringFromDouble(time)

    }

    private fun startStopTimer()
    {
        if(timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerSevice.TIME_EXTRA,time)
        startService(serviceIntent)
        binding.buttonStartStop.text = "Stop"
        binding.buttonStartStop.icon = getDrawable(R.drawable.ic_baseline_pause_24)
        timerStarted = true
    }

    private fun stopTimer() {
        stopService(serviceIntent)
        binding.buttonStartStop.text = "start"
        binding.buttonStartStop.icon = getDrawable(R.drawable.ic_baseline_play_arrow_24)
        timerStarted = false
    }

    private val updateTime:BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context:Context, intent:Intent) {
            time = intent.getDoubleExtra(TimerSevice.TIME_EXTRA,0.0)
            binding.textTimer.text = getTimerStringFromDouble(time)
        }
    }

    private fun getTimerStringFromDouble(time: Double): String {

        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours,minutes,seconds)

    }
    private fun makeTimeString(hour: Int, min: Int, sec: Int): String = String.format("%02d:%02d:%02d",hour,min,sec)

    fun alert_dialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Congrats! u did it")
        builder.setMessage("See leaderboard?")
        builder.setPositiveButton("OK", DialogInterface.OnClickListener(positiveButtonClick))
        builder.setNegativeButton(android.R.string.no, negativeButtonClick)
        builder.show()
    }

}
