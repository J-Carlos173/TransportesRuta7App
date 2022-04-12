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

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()
        setup(email)

        crearValeButton.setOnClickListener(){
            crearVale(email)
        }

    }

    private fun setup(email: String) {
        cargarCenctrodeCostos()
        cargarDirecciones()
        cargarEmpresas()
        cargarDatos(email)


    }
    private fun crearVale(email: String) {

        //val sdf = SimpleDateFormat("dd/M/yyyy")
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss ss")
        val currentDate = sdf.format(Date())


        val vale = hashMapOf(

                "vale_Email"            to email,
                "vale_Fecha"            to currentDate.toString(),
                "vale_Chofer"           to nombresValeEditText.text.toString(),
                "vale_Rut"              to rutValeEditText.text.toString(),
                "vale_Patente"          to patenteEditText.text.toString(),
                "vale_Movil"            to nroMovilEditText.text.toString(),
                "vale_Empresa"          to spnEmpresas.getItemAtPosition(spnEmpresas.selectedItemPosition),
                "vale_CC"               to spnCentroCosto.getItemAtPosition(spnCentroCosto.selectedItemPosition),
                "vale_Inicio"           to autocomplete_fragment1.toString(),
                "vale_Final"            to autocomplete_fragment2.toString(),
                "vale_cliente_nombre"   to nombreClienteText.text.toString(),
                "vale_cliente_rut"      to rutClienteText.text.toString(),
                "vale_Tipo"             to "Empresa",
                "Firma"                 to "Firma.png"
        )

        val contador = db.collection("Vales")
        contador.get().addOnSuccessListener { documentSnapshots ->
        //val size = documentSnapshots.size() + 1

        db.collection("Vales").document().set(vale).addOnSuccessListener {
            Toast.makeText(applicationContext, "Vale Creado", Toast.LENGTH_SHORT).show()
            showHome(email, ProviderType.BASIC)
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "No Funciono", Toast.LENGTH_SHORT).show()
        }
    }
    }
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    private fun cargarCenctrodeCostos(){
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
    private fun cargarEmpresas(){
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
    private fun cargarDirecciones(){
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
                //spnDireccionInicio.adapter = adapter
                //spnDireccionFin.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    private fun cargarDatos(email: String){
        println("*****************************")
        println(email)
        val docRef = db.collection("Usuarios").document(email)
        docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("TAG", "${document.id} => ${document.data}")

                    val nombre                  = document.data?.getValue("usuario_nombre").toString()
                    val apellido                = document.data?.getValue("usuario_apellido").toString()
                    nombresValeEditText.text    = "$nombre $apellido"
                    //nroMovilEditText.text       = document.data?.getValue("usuario_movil").toString()
                    //patenteEditText.text        = document.data?.getValue("usuario_patente").toString()
                    rutValeEditText.text        = document.data?.getValue("usuario_rut").toString()

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
    override fun onBackPressed() {
        val mEmail = intent.getStringExtra("mEmail").toString()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("mEmail", mEmail)
        this.startActivity(intent)
    }
}







