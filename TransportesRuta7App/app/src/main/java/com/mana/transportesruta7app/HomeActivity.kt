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



enum class ProviderType {
    BASIC,
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
        verificar(email)

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        adapter = SliderAdapter(this)
        slider = findViewById(R.id.main_slider2)
        slider.adapter = adapter

    }


    //Setup
    private fun setup(email: String, provider: String){

        emailTextView.text = email
        CrearvaleEmpresaButton.setOnClickListener {
            showCrearValeEmpresa(email,ProviderType.BASIC)
        }
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

    //Cargar Datos
    private fun cargarDatos(email : String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("Usuarios").document(email).get().addOnSuccessListener { document ->
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
    private fun verificar(email : String):Boolean {

        val db = FirebaseFirestore.getInstance()
        var actualizado = false
        db.collection("Usuarios").document(email).get().addOnSuccessListener { document ->
                if (document != null) {
                    val nombre = document.get("usuario_nombre")
                    val apellido = document.get("usuario_apellido")
                    val rut = document.get("usuario_rut")
                    val telefono = document.get("usuario_telefono")
                    val patente = document.get("usuario_patente")
                    val movil = document.get("usuario_movil")
                    if (nombre == "" || nombre == null|| apellido == ""|| apellido == null ||
                        rut == "" || rut == null|| telefono == "" || telefono == null ||
                        patente == "" || patente == null || movil == "" || movil == null)
                    {
                        println("mentira, tiene campos vacios")
                        actualizado = false
                        Toast.makeText(applicationContext, "Faltan Datos", Toast.LENGTH_SHORT).show()
                        showActualizarDatos(emailTextView.text.toString(), ProviderType.BASIC)
                    }
                    else {
                        println("Todo correcto")
                        actualizado = true
                    }


                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
        return actualizado
    }

    //Alertas
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al cargar los datos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Intent
    private fun showLogin(){
        val LoginActivity = Intent(this, LoginActivity::class.java)
        startActivity(LoginActivity)
    }
    private fun showCrearValeEmpresa(email: String, provider: ProviderType) {
        val valeIntent = Intent(this,CrearValeEmpresaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(valeIntent)
    }
    private fun showCrearVale(email: String, provider: ProviderType) {
        val valeIntent = Intent(this,ValeDirecciones::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(valeIntent)
    }
    private fun showActualizarDatos(email: String, provider: ProviderType) {
        val ActuailizarDatosIntent = Intent(this, ActualizarDatosActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(ActuailizarDatosIntent)

    }
    override fun onBackPressed() {
        finishAffinity()
    }

}

//