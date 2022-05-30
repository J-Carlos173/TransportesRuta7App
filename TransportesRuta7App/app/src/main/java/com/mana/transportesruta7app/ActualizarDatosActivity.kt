package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_actualizar_datos.*
import kotlinx.android.synthetic.main.activity_actualizar_datos.apellidoText
import kotlinx.android.synthetic.main.activity_actualizar_datos.nombreText
import kotlinx.android.synthetic.main.activity_actualizar_datos.registrarButton
import kotlinx.android.synthetic.main.activity_actualizar_datos.rutText
import kotlinx.android.synthetic.main.activity_actualizar_datos.telefonoText
import kotlinx.android.synthetic.main.activity_agregar_lista.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.datoText

class ActualizarDatosActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actualizar_datos)
        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")
        cargarDatos(email)

    }
    private fun registrar(email: String) {
        registrarButton.setOnClickListener() {
            if (nombreText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show()
            }
            else if (apellidoText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un apellido", Toast.LENGTH_SHORT).show()
            }
            else if (rutText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un rut", Toast.LENGTH_SHORT).show()
            }
            else if (telefonoText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar una telefono", Toast.LENGTH_SHORT).show()
            }
            else if (patenteText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar una Patente", Toast.LENGTH_SHORT).show()
            }
            else if (movilText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar el numero de movil", Toast.LENGTH_SHORT).show()
            }
           else{
                val actualizarDoc = db.collection("Usuarios").document(email)
                actualizarDoc.get().addOnSuccessListener { documentSnapshots ->

                    actualizarDoc.update(
                        "usuario_nombre"    , nombreText.text.toString(),
                        "usuario_apellido"  , apellidoText.text.toString(),
                        "usuario_rut"           , rutText.text.toString(),
                        "usuario_telefono"      , telefonoText.text.toString(),
                        "usuario_patente"       ,patenteText.text.toString(),
                        "usuario_movil"         , movilText.text.toString())
                    showHome(email, ProviderType.BASIC)
                }

            }



        }
    }
    private fun setup(email: String, provider: String) {
        registrarButton.setOnClickListener(){
            registrar(email)
        }
    }

    private fun cargarDatos(email : String) {

        db.collection("Usuarios").document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val nombre      = document.get("usuario_nombre")
                    val apellido    = document.get("usuario_apellido")
                    val rut         = document.get("usuario_rut")
                    val telefono    = document.get("usuario_telefono")
                    val patente     = document.get("usuario_patente")
                    val movil       = document.get("usuario_movil")

                    nombreText.setText(nombre.toString())
                    apellidoText.setText(apellido.toString())
                    rutText.setText(rut.toString())
                    telefonoText.setText(telefono.toString())
                    patenteText.setText(patente.toString())
                    movilText.setText(movil.toString())

                    if (nombreText.text.toString() == "null")
                    {
                        nombreText.setText("")
                    }
                    if (apellidoText.text.toString() == "null")
                    {
                        apellidoText.setText("")
                    }
                    if (rutText.text.toString() == "null")
                    {
                        rutText.setText("")
                    }
                    if (telefonoText.text.toString() == "null")
                    {
                        telefonoText.setText("")
                    }
                    if (patenteText.text.toString() == "null")
                    {
                        patenteText.setText("")
                    }
                    if (movilText.text.toString() == "null")
                    {
                        movilText.setText("")
                    }
                } else {
                    showAlert()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Error", "get failed with ", exception)
            }
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showHome(email.toString(), ProviderType.BASIC)
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al cargar los datos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}