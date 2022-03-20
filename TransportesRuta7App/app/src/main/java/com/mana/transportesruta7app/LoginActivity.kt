package com.mana.transportesruta7app

<<<<<<< HEAD

import android.content.Intent
=======
import android.content.Intent
import android.os.Build
>>>>>>> 9c1e86783531554e223bb1b9d0b6a705e6d5281d
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.AbsSavedState
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.mana.transportesruta7app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity() {

<<<<<<< HEAD
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.signInAppCompatButton.setOnClickListener {
            val mEmail = binding.emailEditText.text.toString()
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

        /*binding.signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }*/

        binding.recoveryAccountTextView.setOnClickListener {
            val intent = Intent(this, AccountRecoveryActivity::class.java)
            startActivity(intent)
=======
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtEmail=findViewById(R.id.emailEditText)
        txtPassword=findViewById(R.id.passwordEditText)
    }
        fun forgotPassword(view: View)
        {

>>>>>>> 9c1e86783531554e223bb1b9d0b6a705e6d5281d
        }

    private fun Login(view: View)
    {
        loginUser()
    }

<<<<<<< HEAD
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
        val intent = Intent (this, HomeActivity::class.java)
        this.startActivity(intent)
=======
    private fun loginUser()
    {
        val user:String=txtEmail.text.toString()
        val password:String=txtPassword.text.toString()

        if(!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password))
        {
            auth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this){
                    task ->

                    if(task.isSuccessful){
                        action()
                    }else{
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_LONG).show()
                    }
                }
        }

    }

    private fun action(){
        startActivity(Intent(this, HomeActivity::class.java))
>>>>>>> 9c1e86783531554e223bb1b9d0b6a705e6d5281d
    }
}