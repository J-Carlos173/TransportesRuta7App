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
import kotlinx.android.synthetic.main.activity_register.*
import java.lang.ref.PhantomReference
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_vale.*

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    val db = Firebase.firestore

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
        val rut = rutEditText.getText();
        val name = nombresEditText.getText();
        val lastName = apellidosEditText.getText();
        val phone = telefonoEditText.getText();
        val userType = tipoUsuariospn.selectedItem;



        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    sendEmailVerification()
                    // Demas datos Persona
                    val persona = hashMapOf(
                        "Nombres" to name.toString(),
                        "Apellidos" to lastName.toString(),
                        "RUT" to rut.toString(),
                        "telefono" to phone.toString(),
                        "Tipo de Usuario" to userType.toString()

                    )

                    db.collection("Personas").document("test")
                        .set(persona)
                        .addOnSuccessListener {
                            Toast.makeText(
                                applicationContext,
                                "Funciono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                "No Funciono",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
        // [END create_user_with_email]
    }

    private fun sendEmailVerification() {
        // [START send_email_verification]
        val user = auth.currentUser!!
        user.sendEmailVerification()
            .addOnCompleteListener(this) { task ->
                // Email Verification sent
            }
        // [END send_email_verification]
    }

    private fun updateUI(user: FirebaseUser?) {

    }

    private fun reload() {

    }

    companion object {
        private const val TAG = "EmailPassword"
    }

    fun register(view: View){
        createAccount("isaiasa42@gmail.com","123456")
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    fun cancelar(view: View){
        onBackPressed()

    }



}