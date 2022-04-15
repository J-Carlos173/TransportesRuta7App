package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AgregarRutaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ruta)
    }

    override fun onBackPressed() {
        val mEmail = intent.getStringExtra("mEmail").toString()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("mEmail", mEmail)
        this.startActivity(intent)
    }
}