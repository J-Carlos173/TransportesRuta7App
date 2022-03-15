package com.mana.transportesruta7app

import android.R.string
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*


class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pasajero_new)

        //primero
        //setup()
    }

    private fun setup() {

        //Segundo
        cargarCenctrodeCostos()
        cargarDirecciones()
        cargarEmpresas()

    }
        //Tercero
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
    fun cargarDirecciones(){
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
    fun cargarEmpresas(){
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
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
}
