package com.mana.transportesruta7app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import java.text.SimpleDateFormat
import java.util.*

class CrearValeEmpresaActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_vale_empresa)

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()
        setup(email)

        crearValeButton.setOnClickListener(){
            crearVale(email)
        }

    }

    //Setup
    private fun setup(email: String) {
        cargarDatos(email)
        cargarSpnRutas()
        cargarRuta()
    }

    //funciones
    private fun cargarDatos(email: String){

        val docRef = db.collection("Usuarios").document(email)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d("TAG", "${document.id} => ${document.data}")

                val nombre                  = document.data?.getValue("usuario_nombre").toString()
                val apellido                = document.data?.getValue("usuario_apellido").toString()
                nombresValeEditText.text    = "$nombre $apellido"
                //nroMovilEditText.text       = document.data?.getValue("usuario_movil").toString()
                //patenteEditText.text        = document.data?.getValue("usuario_patente").toString()
                rutValeEditText.text        = document.data?.getValue("usuario_rut").toString()

                  } else {
                Log.d("TAG", "El documento tiene un error document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
            }
    }
    private fun cargarSpnRutas(){
        val list : MutableList<String> = ArrayList()
        db.collection("RutasEmpresa").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(document.id)
                }
            val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
            spnRuta.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
            }
    }
    private fun cargarRuta(){
        spnRuta?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val ruta = spnRuta.getItemAtPosition(spnRuta.selectedItemPosition)
                val db = FirebaseFirestore.getInstance()

                db.collection("RutasEmpresa").document(ruta.toString())
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            val ruta_nombre     = document.get("ruta_nombre")
                            val ruta_empresa    = document.get("ruta_empresa")
                            val ruta_inicio     = document.get("ruta_inicio")
                            val ruta_fin        = document.get("ruta_fin")
                            val ruta_ccosto     = document.get("ruta_centrocosto")

                            centroCostoText.setText(ruta_ccosto.toString())
                            EmpresaText.setText(ruta_empresa.toString())
                            rutaInicioText.setText(ruta_inicio.toString())
                            rutaFinText.setText(ruta_fin.toString())
                        } else {
                            showAlert()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Error", "get failed with ", exception)
                    }

                println()


            }


        }
    }

    //Boton Crear
    private fun crearVale(email: String) {

        //val sdf = SimpleDateFormat("dd/M/yyyy")
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss ss")
        val currentDate = sdf.format(Date())


        val vale_Email      = email
        val vale_Fecha      = currentDate.toString()
        val vale_Chofer     = nombresValeEditText.text.toString()
        val vale_Patente    = patenteEditText.text.toString()
        val vale_Movil      = nroMovilEditText.text.toString()
        val vale_Empresa    = centroCostoText.text.toString()
        val vale_CC         = EmpresaText.text.toString()
        val vale_Tipo       = "Empresa"

        showFirma(vale_Email, ProviderType.BASIC,vale_Fecha,vale_Chofer,vale_Patente,vale_Movil,vale_Empresa,vale_CC,vale_Tipo)


    }

    //Alertas
    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error al cargar la ruta")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //Cambio de ventana
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    private fun showFirma(email: String, provider: ProviderType, fecha: String, chofer: String, patente: String, movil: String, empresa: String, cc: String, tipo: String) {
        val firmaIntent = Intent(this,FirmaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("vale_fecha", fecha)
            putExtra("vale_Chofer", chofer)
            putExtra("vale_Patente", patente)
            putExtra("vale_Movil", movil)
            putExtra("vale_Empresa", empresa)
            putExtra("vale_CC", cc)
            putExtra("vale_Tipo", tipo)
        }
        startActivity(firmaIntent)
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showHome(email.toString(), ProviderType.BASIC)
    }
}







