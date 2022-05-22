package com.mana.transportesruta7app

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Polyline
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_agregar_ruta.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.*


class AgregarRutaActivity : AppCompatActivity() {


    companion object {
        private const val ZOOM_SIZE = 14f
        private const val POLYLINE_WIDTH = 12f
    }
    private var lat1 :Double? = 0.0
    private var long1 :Double? = 0.0
    private var lat2 :Double? = 0.0
    private var long2 :Double? = 0.0


    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ruta)

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        setup(email ?: "", provider ?: "")



        // Autocompletado y mapa
        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["api_key"]
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Initialize Autocomplete Fragments from the main activity layout file
        val autocompleteSupportFragment1 =
            supportFragmentManager.findFragmentById(R.id.rutaInicioText) as AutocompleteSupportFragment?
        val autocompleteSupportFragment2 =
            supportFragmentManager.findFragmentById(R.id.rutaFinText) as AutocompleteSupportFragment?

        // Information that we wish to fetch after typing the location and clicking on one of the options
        autocompleteSupportFragment1!!.setPlaceFields(
            listOf(

                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL

            )
        )

        autocompleteSupportFragment2!!.setPlaceFields(
            listOf(

                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.PHONE_NUMBER,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.RATING,
                Place.Field.USER_RATINGS_TOTAL

            )
        )

        // Display the fetched information after clicking on one of the options
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                // Text view where we will append the information that we fetch

                // Information about the place
                val name = place.name
                val address = place.address
                //val phone = place.phoneNumber.toString()
                val latlng = place.latLng
                val latitude1 = latlng?.latitude
                val longitude1 = latlng?.longitude
                lat1 = latitude1
                long1 = longitude1

                val isOpenStatus : String = if(place.isOpen == true){
                    "Open"
                } else {
                    "Closed"
                }

                val rating = place.rating
                val userRatings = place.userRatingsTotal



                /*textView.text = "Name: $name \nAddress: $address \nPhone Number:  \n" +
                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
                        "Rating: $rating \nUser ratings: $userRatings"*/
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })

        // Display the fetched information after clicking on one of the options
        autocompleteSupportFragment2.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                // Text view where we will append the information that we fetch


                // Information about the place
                val name = place.name
                val address = place.address
                //val phone = place.phoneNumber.toString()
                val latlng = place.latLng
                var latitude2 = latlng?.latitude
                var longitude2 = latlng?.longitude
                lat2 = latitude2
                long2 = longitude2

                val isOpenStatus : String = if(place.isOpen == true){
                    "Open"
                } else {
                    "Closed"
                }

                val rating = place.rating
                val userRatings = place.userRatingsTotal



                /*textView.text = "Name: $name \nAddress: $address \nPhone Number:  \n" +
                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
                        "Rating: $rating \nUser ratings: $userRatings"*/
            }
            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun cargarCenctrodeCostos(){
        val docRef = db.collection("Listas").document("CC")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaCC = document.data.toString()
                listaCC = listaCC.replace("{", "")
                listaCC = listaCC.replace("}", "")
                val arr = listaCC.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (cCosto in arr) {
                    val found = cCosto.indexOf("=");
                    list.add(cCosto.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnCentroCosto.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    private fun cargarEmpresas(){
        val docRef = db.collection("Listas").document("Empresas")
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                var listaCC = document.data.toString()
                listaCC = listaCC.replace("{", "")
                listaCC = listaCC.replace("}", "")
                val arr = listaCC.split(",")
                println(arr)
                val list : MutableList<String> = ArrayList()
                for (cCosto in arr) {
                    val found = cCosto.indexOf("=");
                    list.add(cCosto.substring(found + 1))
                }
                val adapter = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, list)
                spnEmpresas.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    private fun setup(email: String, provider: String) {
        cargarCenctrodeCostos()

        cargarEmpresas()
        AgregarRutaButton.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val rutaEmpresa = hashMapOf(
                "ruta_nombre"       to rutaText.text.toString(),
                "ruta_inicio"       to "["+lat1.toString()+","+long1.toString()+"]",
                "ruta_fin"          to "["+lat2.toString()+","+long2.toString()+"]",
                "ruta_empresa"      to spnEmpresas.getItemAtPosition(spnEmpresas.selectedItemPosition),
                "ruta_centrocosto"  to spnCentroCosto.getItemAtPosition(spnCentroCosto.selectedItemPosition)
            )

                db.collection("RutasEmpresa").document(rutaText.text.toString()).set(rutaEmpresa).addOnSuccessListener {
                    showFelicidades()
                    showAdmin(email, provider)
                }.addOnFailureListener {
                    showError()
                }

            showFelicidades()
        }
    }


    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showAdmin(email.toString(), provider.toString())

    }

    private fun showAdmin(email: String, provider: String) {
        val adminIntent = Intent(this,AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider)
        }
        startActivity(adminIntent)
    }
    private fun showFelicidades() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Felicidades")
        builder.setMessage("Se a agregado a la lista")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ocurrio un problema")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}