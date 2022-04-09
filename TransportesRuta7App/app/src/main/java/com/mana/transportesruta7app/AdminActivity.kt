package com.mana.transportesruta7app

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_admin.*


class AdminActivity : AppCompatActivity() {
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
        desactivarUsuarioButton.setOnClickListener(){
            desactivarUsuario(email, provider)
        }

        valesButton.setOnClickListener(){
            vales(email, provider)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
    private fun showAgregarUsuario(email: String, provider: String) {
        val homeIntent = Intent(this,PermitirUsuario::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }
    private fun desactivarUsuario(email: String, provider: String) {
        val homeIntent = Intent(this,DesactivarCuentaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(homeIntent)
    }
    private fun vales(email: String, provider: String) {
        val homeIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        //startActivity(homeIntent)
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al autentificar al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}