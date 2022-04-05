package com.mana.transportesruta7app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mana.transportesruta7app.databinding.ActivityLoginBinding
import kotlinx.android.synthetic.main.activity_fragment_vale.*
import kotlinx.android.synthetic.main.activity_home.*


import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_vale.*


class LoginActivity : AppCompatActivity() {
    val db = Firebase.firestore

    private lateinit var auth: FirebaseAuth

    private lateinit var binding: ActivityLoginBinding
    private var mEmail = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signInAppCompatButton.setOnClickListener {
            mEmail = binding.emailEditText.text.toString()
            val mPassword = binding.passwordEditText.text.toString()
            when {
                mEmail.isEmpty() || mPassword.isEmpty() -> {
                    Toast.makeText(baseContext, "Correo o contraseña icorrectos.",
                        Toast.LENGTH_SHORT).show()
                } else -> {
                login(mEmail, mPassword)
                }
            }

        }

        binding.recoveryAccountTextView.setOnClickListener {
            val intent = Intent(this, AccountRecoveryActivity::class.java)
            startActivity(intent)
        }

        setup()
    }
    private fun setup (){
        testButton.setOnClickListener(){
            /*val message = mEmail
            val intent = Intent(this, AdminActivity::class.java)
            intent.putExtra("mensaje", message)
            this.startActivity(intent)*/
                /*val mEmail = intent.getStringExtra("email")*/

        }
    }

    public override fun onStart() {

        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified) {
                reload()
            } else {
                val intent = Intent(this, CheckEmailActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun login (email : String , password : String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "signInWithEmail:success")
                    reload()
                } else {
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Correo o contraseña icorrectos.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun reload() {
        var tipoUsuario = ""
        //validacion de admin o chofer
        val docRef = db.collection("Personas").document(mEmail)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var tipoPerfil = document.data.toString()
                tipoPerfil              = tipoPerfil.replace("}", "")
                tipoPerfil              = tipoPerfil.replace("{", "")
                var cadenaSeparada  = tipoPerfil.split(",","=");
                for (i in cadenaSeparada.indices) {
                    println("**")
                    println(cadenaSeparada[i])
                    if (cadenaSeparada[i] == " Tipo de Usuario") {
                        tipoUsuario     = cadenaSeparada[i+1]
                        println(cadenaSeparada[i+1])

                    }
                }
                Log.d("TAG", tipoUsuario.toString())
            }

            /*Log.d("TAG", cadenaSeparada.toString())*/


            if (tipoUsuario.equals("admin")){
                val intent = Intent(this, AdminActivity::class.java)
                /*intent.putExtra("email", "isaiasa42@gmail.com")*/
                this.startActivity(intent)
            }

            else if (tipoUsuario.equals("chofer")){
                val intent = Intent(this, HomeActivity::class.java)
                /*intent.putExtra("email", "isaiasa42@gmail.com")*/
                this.startActivity(intent)
            }else{
                Toast.makeText(this, "Perfil no reconocido", Toast.LENGTH_SHORT).show()
            }



        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
}




