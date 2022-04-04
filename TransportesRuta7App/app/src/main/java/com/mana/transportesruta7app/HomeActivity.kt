package com.mana.transportesruta7app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    private lateinit var adapter:SliderAdapter
    private lateinit var slider: ViewPager2

    //val mEmail = intent.getStringExtra("mEmail").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        setup()
        //emailTextView.text = mEmail
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        adapter = SliderAdapter(this)
        slider = findViewById(R.id.main_slider2)
        slider.adapter = adapter
    }



    fun setup(){

        /*CrearvaleButton.setOnClickListener(){
            val intent = Intent(this, ValeActivity::class.java)
            //intent.putExtra("mEmail", mEmail)
            this.startActivity(intent)

        }*/



    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
    }
}

//