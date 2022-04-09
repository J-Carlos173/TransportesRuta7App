package com.mana.transportesruta7app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_vale.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ValeActivity : AppCompatActivity() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vale)
        crearValeButton.setOnClickListener() {
            crearVale()
        }
        setup()

    }
    override fun onBackPressed() {
        val mEmail = intent.getStringExtra("mEmail").toString()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("mEmail", mEmail)
        this.startActivity(intent)
    }

    private fun setup() {
        cargarCenctrodeCostos()
        cargarDirecciones()
        cargarEmpresas()
        cargarDatos()

        crearValeButton.setOnClickListener(){
            crearVale()
        }
    }

    fun crearVale() {
        val mEmail =intent.getStringExtra("mEmail").toString()
        val vale = hashMapOf(
                "Email"             to mEmail,
                "Fecha"             to fechaTextView.text.toString(),
                "Hora Inicio"       to "10:30",
                "Hora Fin"          to "11:00",
                "Chofer"            to choferTextView.text.toString(),
                "Movil"             to movilTextView.text.toString(),
                "Patente"           to patenteTextView.text.toString(),
                "Empresa"           to spnEmpresas.getItemAtPosition(spnEmpresas.selectedItemPosition),
                "Centro de costo"   to spnCentroCosto.getItemAtPosition(spnCentroCosto.selectedItemPosition),
                "Direccion Inicio"  to spnDireccionInicio.getItemAtPosition(spnDireccionInicio.selectedItemPosition),
                "Direccion Final"   to spnDireccionFin.getItemAtPosition(spnDireccionFin.selectedItemPosition),
                "Cliente"           to pasajeroEditText.text.toString(),
                "Rut"               to rutText.text.toString(),
                "Firma"             to "Firma.png"
        )

        val contador = db.collection("Vales")
        contador.get().addOnSuccessListener { documentSnapshots ->
        val size = documentSnapshots.size() + 1

        db.collection("Vales").document(size.toString()).set(vale).addOnSuccessListener {
            Toast.makeText(applicationContext, "Vale Creado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("mEmail", mEmail)
            this.startActivity(intent)
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "No Funciono", Toast.LENGTH_SHORT).show()
        }
    }
    }
    fun cargarCenctrodeCostos(){
        val docRef = db.collection("Listas").document("CC")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaCC = document.data.toString()
                listaCC = listaCC.replace("{", "")
                listaCC = listaCC.replace("}", "")
                val arr = listaCC.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (cCosto in arr) {
                    val found = cCosto.indexOf("=");
                    list.add(cCosto.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnCentroCosto.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    fun cargarEmpresas(){
        val docRef = db.collection("Listas").document("Empresas")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaCC = document.data.toString()
                listaCC = listaCC.replace("{", "")
                listaCC = listaCC.replace("}", "")
                val arr = listaCC.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (cCosto in arr) {
                    val found = cCosto.indexOf("=");
                    list.add(cCosto.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnEmpresas.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    fun cargarDirecciones(){
        val docRef = db.collection("Listas").document("Direcciones")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaCC = document.data.toString()
                listaCC = listaCC.replace("{", "")
                listaCC = listaCC.replace("}", "")
                val arr = listaCC.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (cCosto in arr) {
                    val found = cCosto.indexOf("=");
                    list.add(cCosto.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnDireccionInicio.adapter = adapter
                spnDireccionFin.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    fun cargarDatos(){
        val mEmail =intent.getStringExtra("mEmail").toString()

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        val docRef = db.collection("Personas").document(mEmail)
        docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "${document.id} => ${document.data}")

                    choferTextView.text     = document.data?.getValue("Nombres").toString()
                    movilTextView.text      = document.data?.getValue("Movil").toString()
                    patenteTextView.text    = document.data?.getValue("Patente").toString()
                    fechaTextView.text      = currentDate.toString()
                    Log.d("TAG", "******************Todo Correcto******************")
                    Toast.makeText(this, "Se han cargado los datos", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("TAG", "El documento tiene un error document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
            }



    }
}







