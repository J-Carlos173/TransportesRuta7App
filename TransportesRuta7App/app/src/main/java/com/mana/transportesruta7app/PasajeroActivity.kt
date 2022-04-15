package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_pasajero.*


class PasajeroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasajero)

    }
    private fun setup (){
        firmarId.setOnClickListener(){
            /*val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)*/

            val message = "mEmail"
            val intent = Intent(this, FirmaActivity::class.java)
            intent.putExtra("mensaje", message)
            this.startActivity(intent)
        }
    }

}