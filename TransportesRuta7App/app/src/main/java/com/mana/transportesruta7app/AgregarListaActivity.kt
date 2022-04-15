package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_agregar_lista.*
import kotlinx.android.synthetic.main.activity_agregar_lista.registrarButton
import kotlinx.android.synthetic.main.activity_permitir_usuario.*


class AgregarListaActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_lista)
        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()

        cargarCenctrodeCostos()
        setup(email,provider)
    }
    private fun agregarALista(email: String, provider: String){
        val db = FirebaseFirestore.getInstance()
        val tipo = spnLista.getItemAtPosition(spnPerfiles.selectedItemPosition)
        val dato = db.collection("Listas").document(tipo.toString())
        dato.get().addOnSuccessListener { documentSnapshots ->
            val size = documentSnapshots
        }


    }
    private fun setup(email: String, provider: String) {
        registrarButton.setOnClickListener(){
            agregarALista(email,provider)
        }
    }
    private fun cargarCenctrodeCostos(){

        val docRef = db.collection("Listas").document("Tipo")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaTipo = document.data.toString()
                listaTipo = listaTipo.replace("{", "")
                listaTipo = listaTipo.replace("}", "")
                val arr = listaTipo.split(",")
                val list : MutableList<String> = ArrayList()
                for (listas in arr) {
                    val found = listas.indexOf("=");
                    list.add(listas.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnLista.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showAdmin(email.toString(), provider.toString())

    }
    private fun showAdmin(email: String, provider: String) {
        val adminIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(adminIntent)
    }
    private fun showFelicidades() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Felicidades")
        builder.setMessage("Se a agregado a la lista")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(" Ocurrio un error")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}