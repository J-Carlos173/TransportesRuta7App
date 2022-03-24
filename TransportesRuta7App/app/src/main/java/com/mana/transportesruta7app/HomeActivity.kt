package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var adapter:SliderAdapter
    private lateinit var slider: ViewPager2

    //val mEmail = intent.getStringExtra("mEmail").toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        //emailTextView.text = mEmail
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        adapter = SliderAdapter(this,)
        slider = findViewById(R.id.main_slider2)
        slider.adapter = adapter
    }

    private fun CrearVale() {
        val intent = Intent(this, ValeActivity::class.java)
        //intent.putExtra("mEmail", mEmail)
        this.startActivity(intent)
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        this.startActivity(intent)
    }
}

//