package com.mana.transportesruta7app

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.AbsSavedState
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

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

        }

    private fun Login(view: View)
    {
        loginUser()
    }

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
    }
}
