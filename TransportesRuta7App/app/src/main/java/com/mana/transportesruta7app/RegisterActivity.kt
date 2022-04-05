package com.mana.transportesruta7app
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*



class RegisterActivity : AppCompatActivity() {
    val db = Firebase.firestore
    val db2 = Firebase.firestore
    lateinit var auth:FirebaseAuth
    lateinit var usuarioCreado:FirebaseAuth



    private fun reload() {
        val intent = Intent(this, AdminActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        auth = Firebase.auth
        usuarioCreado = Firebase.auth
        setup()
    }
    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun setup(){
        cancelarButton.setOnClickListener {
            onBackPressed()
        }
        registerButton.setOnClickListener{
            createAccount()
        }
        cargarPerfiles()
    }
    private fun createAccount() {
        val email = emailEditText.text
        val password = passwordEditText.text
        println("****************** Funcion crear usuario ******************")
        usuarioCreado.createUserWithEmailAndPassword(email.toString(), password.toString()).addOnCompleteListener(this) { resultado ->
            if (resultado.isSuccessful) {
                Log.d(TAG, "Usuario creado")
                println("******************Usuario creado ******************")

                crearPeronsa(email.toString())

                reload()
            }
            else {
                Log.d("TAG", "******************No se pudo crear la cuenta ******************")
                println("******************No se pudo crear la cuenta ******************")
                Toast.makeText(this, "No se pudo crear la cuenta", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun crearPeronsa(email :String){

        println("****************** Crear persona ******************")
        val rut = rutEditText.text
        val name = nombresEditText.text
        val lastName = apellidosEditText.text
        val phone = telefonoEditText.text
        val userType = tipoUsuariospn.selectedItem;

        println("****************** Despues de variables *****************")

        val persona = hashMapOf(
            "Nombres" to name.toString(),
            "Apellidos" to lastName.toString(),
            "RUT" to rut.toString(),
            "telefono" to phone.toString(),
            "Tipo de Usuario" to userType.toString()
        )

        println("****************** Despues de hashmap *****************")

        db2.collection("Personas").document(email).set(persona).addOnSuccessListener {
            println("****************** FUNCIONO *****************")
            Toast.makeText(applicationContext, "**********Funciono************", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "***********No Funciono*********", Toast.LENGTH_SHORT).show()
            println("****************** NO FUNCIONO *****************")
        }
        usuarioCreado.signOut()
    }
    private fun cargarPerfiles(){
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
                tipoUsuariospn.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }


}