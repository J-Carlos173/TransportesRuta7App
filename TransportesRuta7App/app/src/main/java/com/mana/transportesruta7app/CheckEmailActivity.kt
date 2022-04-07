package com.mana.transportesruta7app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.mana.transportesruta7app.databinding.ActivityCheckEmailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CheckEmailActivity : AppCompatActivity() {
    val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCheckEmailBinding
    val mEmail = intent.getStringExtra("mEmail").toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val user = auth.currentUser

        binding.veficateEmailAppCompatButton.setOnClickListener {
            val profileUpdates = userProfileChangeRequest {  }
            user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    if(user.isEmailVerified) {
                        reload()
                    } else {
                        Toast.makeText(this, "Por favor verifica tu correo", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified) {
                reload()
            } else {
                sendEmailVerification()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Se envio un correo de verificación",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun reload()  {
        //validacion de admin o chofer
        val docRef = db.collection("Personas").document(mEmail)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var tipoPerfil = document.data.toString()
                tipoPerfil              = tipoPerfil.replace("}", "")
                tipoPerfil              = tipoPerfil.replace("{", "")
                var cadenaSeparada  = tipoPerfil.split(",","=");
                var tipoUsuario = ""
                for (i in cadenaSeparada.indices) {
                    println(cadenaSeparada[i])
                    if (cadenaSeparada[i] == "Tipo de Usuario") {
                        var tipoUsuario     = cadenaSeparada[i]
                        Log.d("TAG", tipoUsuario)
                    }
                }


                if (tipoUsuario.equals("admin")){
                    val intent = Intent(this, AdminActivity::class.java)
                    //intent.putExtra("email", email)
                    this.startActivity(intent)
                }

                else if (tipoUsuario.equals("chofer")){
                    val intent = Intent(this, HomeActivity::class.java)
                    //intent.putExtra("email", email)
                    this.startActivity(intent)
                }else{
                    Toast.makeText(this, "Perfil no reconocido", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    this.startActivity(intent)
                }
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}