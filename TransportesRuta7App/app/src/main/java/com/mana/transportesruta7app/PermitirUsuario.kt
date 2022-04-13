package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_permitir_usuario.*
import kotlinx.android.synthetic.main.activity_permitir_usuario.correoText
import kotlinx.android.synthetic.main.activity_permitir_usuario.registrarButton
import kotlinx.android.synthetic.main.activity_register.*


class PermitirUsuario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permitir_usuario)

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()

        setup(email,provider)
        cargarPerfiles()
    }
    private fun setup(email: String, provider: String) {
       registrarButton.setOnClickListener(){
           agregarCorreo(email,provider)
        }

    }
    private fun cargarPerfiles(){
        val db = FirebaseFirestore.getInstance()
        val docRef = db.collection("Listas").document("Perfiles")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaPerfil = document.data.toString()
                listaPerfil = listaPerfil.replace("{", "")
                listaPerfil = listaPerfil.replace("}", "")
                val arr = listaPerfil.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (pLista in arr) {
                    val found = pLista.indexOf("=");
                    list.add(pLista.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnPerfiles.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    private fun agregarCorreo(email: String, provider: String) {

        val db = FirebaseFirestore.getInstance()
        val update = db.collection("Listas").document("Permisos")
        var correo = correoText.text.toString()
        correo = correo.replace(".", "_")
        val perfil = spnPerfiles.getItemAtPosition(spnPerfiles.selectedItemPosition)

        update.update(correo,perfil)
                .addOnSuccessListener {
                    showFelicidades()
                    showAdmin(email, provider)
                }
                .addOnFailureListener {
                    showAlert()
                }

    }
    private fun showAdmin(email: String, provider: String) {

        val adminIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(adminIntent)
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()
        showAdmin(email, provider)

    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(" Ocurrio un error")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showFelicidades() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Felicidades")
        builder.setMessage("Correo agregado")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}