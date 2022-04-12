package com.mana.transportesruta7app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")
        cargarDatos(email)
        showCorreo(email)

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        adapter = SliderAdapter(this)
        slider = findViewById(R.id.main_slider2)
        slider.adapter = adapter



    }
    private fun showCorreo(email: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Felicitaciones el correo es")
        builder.setMessage(email)
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun setup(email: String, provider: String){

        emailTextView.text = email
        //providerTextView.text = provider
        //val sdf = SimpleDateFormat("dd/M/yyyy")
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss ss")
        val currentDate = sdf.format(Date())
        //fechaText.text = currentDate

        CrearvaleButton.setOnClickListener {

            showCrearVale(email,ProviderType.BASIC)
        }
        cerrarButton.setOnClickListener {
            //Borrar datos
            val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            FirebaseAuth.getInstance().signOut()
            showLogin()
        }


    }
    private fun cargarDatos(email : String) {

        val db = FirebaseFirestore.getInstance()
        println("*****************email******************************")
        println(email)
        db.collection("Usuarios").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nombre = document.get("usuario_nombre")
                    val apellido = document.get("usuario_apellido")
                    val nombre_completo = "$nombre $apellido"

                    nombreText.setText(nombre_completo)

                } else {
                    showAlert()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al cargar los datos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    override fun onBackPressed() {
        finishAffinity()
    }
    private fun showLogin(){
        val LoginActivity = Intent(this, LoginActivity::class.java)
        startActivity(LoginActivity)
    }
    private fun showCrearVale(email: String, provider: ProviderType) {
        val valeIntent = Intent(this,ValeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(valeIntent)
    }
}

//