package com.mana.transportesruta7app

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mana.transportesruta7app.databinding.ActivityCheckEmailBinding
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.ref.PhantomReference

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val db = Firebase.firestore
    companion object {
        private const val TAG = "EmailPassword"
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {

    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }
    /*val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)*/

    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var txtName:EditText
    private lateinit var txtlastName:EditText
    private lateinit var txtRut:EditText
    private lateinit var txtPhone:EditText
    private lateinit var txtTipoUsuario:SpinnerAdapter
    private lateinit var auth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        setup()
    }

    private fun setup(){
        cargarPerfiles()
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }
    private fun createAccount(email: String, password: String) {
        // [START create_user_with_email]
        val rut = rutEditText.text
        val name = nombresEditText.text
        val lastName = apellidosEditText.text
        val phone = telefonoEditText.text
        val userType = tipoUsuariospn.selectedItem;

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user!!.sendEmailVerification().addOnCompleteListener(this) { task ->
                        Log.d(TAG, "******************SUSSESS******************")
                        if (task.isSuccessful) {
                            Log.d(TAG, "******************ENVIAR A******************")
                            Toast.makeText(this, "Se envio un correo de verificación", Toast.LENGTH_SHORT).show()
                        }
                    }
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    //val user = auth.currentUser
                    //updateUI(user)

                    // Demas datos Persona
                    val persona = hashMapOf(
                        "Nombres" to name.toString(),
                        "Apellidos" to lastName.toString(),
                        "RUT" to rut.toString(),
                        "telefono" to phone.toString(),
                        "Tipo de Usuario" to userType.toString()
                    )

                    db.collection("Personas").document(email).set(persona).addOnSuccessListener {
                            Toast.makeText(applicationContext, "**********Funciono************", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(applicationContext, "***********No Funciono*********", Toast.LENGTH_SHORT).show()
                        }

                } else {
                    //updateUI()
                }
            }
        // [END create_user_with_email]
    }


    fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }



    fun register(view: View){
        createAccount(emailEditText.text.toString(),passwordEditText.text.toString())
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun cancelar(view: View){
        onBackPressed()

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