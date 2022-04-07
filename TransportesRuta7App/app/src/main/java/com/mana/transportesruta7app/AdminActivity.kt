package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_vale.*

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        setup()

    }

    private fun setup(){
        //val mEmail = intent.getStringExtra("mEmail").toString()
        //Toast.makeText(baseContext, mEmail, Toast.LENGTH_SHORT).show()

        registrarButton.setOnClickListener(){
            val intent = Intent (this, RegisterActivity::class.java)
            this.startActivity(intent)
        }
        modificarButton.setOnClickListener(){
            val intent = Intent (this, UpdatePasswordActivity::class.java)
            this.startActivity(intent)
        }
        eliminarButton.setOnClickListener(){
            val intent = Intent (this, DeleteAccountActivity::class.java)
            this.startActivity(intent)
        }
        valesButton.setOnClickListener(){
            val intent = Intent (this, ValeActivity::class.java)
            this.startActivity(intent)
        }
    }
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}