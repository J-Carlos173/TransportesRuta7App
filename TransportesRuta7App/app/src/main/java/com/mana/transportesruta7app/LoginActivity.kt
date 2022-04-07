package com.mana.transportesruta7app


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_vale.*

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Cosas de FireBase
        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integracion de Firebase Completa ")
        firebaseAnalytics.logEvent("InitScreen", bundle)

        // Setup
        setup()
        session()
    }

    //Layout visible
    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    //Si existe sesion...Iniciar
    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email",null)
        val provider = prefs.getString("provider", null)
        if (email != null && provider != null){
            authLayout.visibility = View.INVISIBLE
            showHome(email, ProviderType.valueOf(provider) )
        }
    }

    //Botones
    private fun setup() {
        //Boton Logear
        singUpButton.setOnClickListener {
            if (correoText.text.isNotEmpty() && contraseñaText.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(correoText.text.toString(),
                        contraseñaText.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful) {
                            println("***********success************")
                            var tipoUsuario = ""
                            //validacion de admin o chofer
                            val docRef = db.collection("Usuarios").document(correoText.text.toString())
                            docRef.get().addOnSuccessListener { document ->
                                if (document != null) {
                                    println("***********tiene datos************")
                                    var usuarioData = document.data.toString()
                                        usuarioData = usuarioData.replace("}", "")
                                        usuarioData = usuarioData.replace("{", "")
                                    var cadenaSeparada  = usuarioData.split(",","=");
                                    for (i in cadenaSeparada.indices) {
                                        println(cadenaSeparada[i])
                                        if (cadenaSeparada[i] == " usuario_tipo") {
                                            tipoUsuario     = cadenaSeparada[i+1]
                                            println("***********************")
                                            println(tipoUsuario)
                                        }
                                    }
                                }
                                    if (tipoUsuario.equals("admin")){
                                        showAdmin(it.result?.user?.email ?: "", ProviderType.BASIC)
                                    }
                                    else if (tipoUsuario.equals("chofer")){
                                        showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                                    }else{
                                        showAlertTipoUsuario()
                                    }

                            }.addOnFailureListener { exception ->
                                println("***********error en login************")
                            }

                        } else {
                            showAlert()
                        }
                    }
            }
        }
        //Boton Registrar
        registerButton.setOnClickListener {
            val RegistrarIntent = Intent(this,RegisterActivity::class.java)
            startActivity(RegistrarIntent)
        }
    }

    //Alerta
    private fun showAlertTipoUsuario() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Existe un problema con el tipo de usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autentificar al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email: String, provider: ProviderType) {

        val productoIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)

        }
        startActivity(productoIntent)
    }
    private fun showAdmin(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}




