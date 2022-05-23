package com.mana.transportesruta7app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin.*


class AdminActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")

        // Guardado de datos
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

    }

    private fun setup(email: String, provider: String) {

        agregarUsuarioButton.setOnClickListener(){
            showAgregarUsuario(email, provider)
        }
        agregarRutaButton.setOnClickListener(){
            showAgregarRuta(email, provider)
        }
        agregarListaButton.setOnClickListener(){
            showAgregarLista(email, provider)
        }
        desactivarUsuarioButton.setOnClickListener(){
            desactivarUsuario(email, provider)
        }
        valesButton.setOnClickListener(){
            showExportarVales(email, provider)
        }
        cerrarSessionButton.setOnClickListener(){
            cerrarSession()
        }
    }
    private fun cerrarSession(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        showLogin()
    }
    private fun showAgregarUsuario(email: String, provider: String) {
        val homeIntent = Intent(this,PermitirUsuario::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }
    private fun showAgregarRuta(email: String, provider: String) {
        val agregarRutaIntent = Intent(this,AgregarRutaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(agregarRutaIntent)
    }
    private fun showAgregarLista(email: String, provider: String) {
        val agregarListaIntent = Intent(this,AgregarListaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(agregarListaIntent)
    }
    private fun showExportarVales(email: String, provider: String) {
        val exportarValesIntent = Intent(this,ValesActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(exportarValesIntent)
    }
    private fun desactivarUsuario(email: String, provider: String) {
        val homeIntent = Intent(this,DesactivarCuentaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autentificar al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    override fun onBackPressed() {
        finishAffinity()
    }
    private fun showLogin(){
        val LoginActivity = Intent(this, LoginActivity::class.java)
        startActivity(LoginActivity)
    }
}