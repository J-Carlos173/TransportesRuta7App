package com.mana.transportesruta7app
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        registrar()

    }
    private fun registrar() {
        registrarButton.setOnClickListener() {
            if (nombreText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show()
            }
            else if (apellidosText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un apellido", Toast.LENGTH_SHORT).show()
            }
            else if (rutText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar un rut", Toast.LENGTH_SHORT).show()
            }
            else if (telefonoText.text.isEmpty()){
                Toast.makeText(applicationContext, "Debe ingresar una telefono", Toast.LENGTH_SHORT).show()
            }
            else if (datoText.text.isNotEmpty() && contraseñaText.text.isNotEmpty()) {
                val db = Firebase.firestore
                val docRef = db.collection("Listas").document("Permisos")
                docRef.get().addOnSuccessListener { document ->
                    if (document != null) {
                        var listaPermisos = document.data.toString()
                        listaPermisos = listaPermisos.replace("{", "")
                        listaPermisos = listaPermisos.replace("}", "")
                        val arr = listaPermisos.split(",")
                        println(arr)
                        for (cadaCorreo in arr) {
                            val found = cadaCorreo.indexOf("=");
                            val correo = cadaCorreo.split("=")

                            if (correo[0].trim() == datoText.text.toString().replace(".", "_")) {
                                showFelicidades()
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                                    datoText.text.toString(),
                                    contraseñaText.text.toString()).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        guardar_datos(cadaCorreo.substring(found +1))
                                        if(cadaCorreo.substring(found +1) == "chofer"){
                                            showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                                        }else if(cadaCorreo.substring(found +1) == "admin") {
                                            showAdmin(it.result?.user?.email ?: "", ProviderType.BASIC)
                                        }else {
                                        }
                                    } else {
                                        showAlertTipoUsuario()
                                    }
                                }
                            } else{
                                showAlertCorreoNoTienePermisos()
                            }
                        }
                    }
                } .addOnFailureListener { exception ->
                    showAlert()
                }
            }else{
                Toast.makeText(applicationContext, "Error en correo o contraseña", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun guardar_datos(tipo : String ) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "usuario_nombre" to nombreText.text.toString(),
            "usuario_rut" to rutText.text.toString(),
            "usuario_apellido" to apellidosText.text.toString(),
            "usuario_telefono" to telefonoText.text.toString(),
            "usuario_tipo" to  tipo,
            "usuario_activo" to "true"
        )
        //validar Registro
        if (nombreText.text.toString() == "") {
            Toast.makeText(applicationContext, "Debe ingresar un nombre", Toast.LENGTH_SHORT).show()
        } else if (rutText.text.toString() == "") {
            Toast.makeText(applicationContext, "Debe ingresar un RUT", Toast.LENGTH_SHORT).show()
        } else if (apellidosText.text.toString() == "") {
            Toast.makeText(applicationContext, "Debe ingresar un Apelldio", Toast.LENGTH_SHORT).show()
        } else if (telefonoText.text.toString() == "") {
            Toast.makeText(applicationContext, "Debe ingresar un Telefono", Toast.LENGTH_SHORT).show()
        } else {

            // agregar documento con id manual con minuscula

            db.collection("Usuarios").document(datoText.text.toString().toLowerCase())
                .set(user as Map<String, Any>).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(applicationContext, "Usuario Registrado", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(applicationContext, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    private fun showAdmin(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ingrese un correo valido")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertTipoUsuario() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("El correo creado presenta problema con el tipo de cuenta")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showFelicidades() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Felicidades tu correo tiene permisos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showAlertCorreoNoTienePermisos() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Su correo no esta en la lista de permisos")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    override fun onBackPressed() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

}