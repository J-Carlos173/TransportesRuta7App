package com.mana.transportesruta7app

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_vale.*


// Prueba commit
class ValeActivity : AppCompatActivity() {
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        var empresa = ""
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vale)

        crearValeButton.setOnClickListener() {
            crearVale()
        }

        setup()

    }

    private fun setup() {
        cargarCenctrodeCostos()
        cargarDirecciones()
        cargarEmpresas()
        crearVale()

    }

    fun crearVale() {
            val vale = hashMapOf(
                "Fecha"             to fechaTextView.text.toString(),
                "Chofer"            to choferTextView.text.toString(),
                "Movil"             to movilTextView.text.toString(),
                "Patente"           to patenteTextView.text.toString(),
                "Hora Inicio"       to horaTextView.text.toString(),
                "Hora Termino"      to horaTextView.text.toString(),
                "Empresa"           to spnEmpresas.getItemAtPosition(spnEmpresas.selectedItemPosition),
                "Centro de costo"   to spnCentroCosto.getItemAtPosition(spnCentroCosto.selectedItemPosition),
                "Recorrido"         to spnDirecciones.getItemAtPosition(spnDirecciones.selectedItemPosition),
                "Cliente"           to "Rosalia",
                "Rut"               to "18.465.654-9",
                "Firma"             to "Si"
            )
            db.collection("Vales").document("test").set(vale).addOnSuccessListener{
                    Toast.makeText(applicationContext,"Funciono",Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                    Toast.makeText( applicationContext, "No Funciono",Toast.LENGTH_SHORT).show()
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
                spnDirecciones.adapter = adapter
                spnDirecciones2.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }


}




