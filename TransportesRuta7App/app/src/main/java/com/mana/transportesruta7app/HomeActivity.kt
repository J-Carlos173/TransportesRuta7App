package com.mana.transportesruta7app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_home.*
import java.text.SimpleDateFormat
import java.util.*


enum class ProviderType {
    BASIC,
    GOOGLE
}
class HomeActivity : AppCompatActivity() {


    private lateinit var adapter:SliderAdapter
    private lateinit var slider: ViewPager2


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")
        verificar()

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()




        adapter = SliderAdapter(this)
        slider = findViewById(R.id.main_slider2)
        slider.adapter = adapter
    }



    private fun setup(email: String, provider: String){

        title = "Home"

        emailTextView.text = email
        //providerTextView.text = provider

        //val sdf = SimpleDateFormat("dd/M/yyyy")
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss ss")
        val currentDate = sdf.format(Date())
        //fechaText.text = currentDate

        cerrarButton.setOnClickListener {
            //Borrar datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            showAuth()
        }


    }
    private fun verificar():Boolean {

        val db = FirebaseFirestore.getInstance()
        val email = emailTextView.text.toString()
        var actualizado = false
        db.collection("usuarios").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //*******************************CORREGIR*******************************
                    /*val nombre = document.get("usuario_nombre")
                    val direccion = document.get("usuario_direccion")
                    val comuna = document.get("usuario_comuna")
                    val telefono = document.get("usuario_telefono")
                    val token = document.get("token")
                    if (nombre == "" || nombre == null|| direccion == ""|| direccion == null ||
                        comuna == "" || comuna == null|| telefono == "" || telefono == null)
                    {
                        println("mentira, tiene campos vacios")
                        actualizado = false
                        Toast.makeText(applicationContext, "Faltan Datos", Toast.LENGTH_SHORT).show()
                        //showHome(emailTextView.text.toString(), providerTextView.text.toString())
                    }
                    else {
                        println("verdad, todo correcto")
                        actualizado = true
                    }
*/

                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
        return actualizado
    }

    override fun onBackPressed() {
        //cerrar session
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        showAuth()
        //O bajar app y dejar session activa**********************************
        //finishAffinity()
    }
    private fun showAuth(){
        val AuthActivity = Intent(this, LoginActivity::class.java)
        startActivity(AuthActivity)
    }
}

//