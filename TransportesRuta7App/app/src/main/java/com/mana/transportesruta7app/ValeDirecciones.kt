package com.mana.transportesruta7app

import android.content.ContentValues
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import com.google.maps.model.LatLng as MapsLatLng
import kotlinx.android.synthetic.main.activity_vale_direcciones.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.linealMapaValeFragment


class ValeDirecciones : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val ZOOM_SIZE = 14f
        const val POLYLINE_WIDTH = 12f
    }

    private var validacionOrigen = false
    private var validacionDestino = false
    private var lat1 :Double? = 0.0
    private var long1 :Double? = 0.0
    private var lat2 :Double? = 0.0
    private var long2 :Double? = 0.0


    val db = Firebase.firestore

    private var mMap: GoogleMap? = null
    private var polyline: Polyline? = null
    private val overview = 0
    private val viewModel by viewModels<MapsViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vale_direcciones)

        linealMapaValeFragment.visibility = View.GONE

        // Autocompletado y mapa
        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["api_key"]
        val apiKey = value.toString()

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()
        setup(email)

        // Mapa No Scrollable
        transparent_image2.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    scrollViewViaje.requestDisallowInterceptTouchEvent(true)
                    Log.d(ContentValues.TAG, "ABAJO")
                }
                MotionEvent.ACTION_DOWN -> {
                    scrollViewViaje.requestDisallowInterceptTouchEvent(false)
                    Log.d(ContentValues.TAG, "ARRIBA")
                }
            }
            v?.onTouchEvent(event) ?: true
        }

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        // Initialize Autocomplete Fragments from the main activity layout file
        val autocompleteSupportFragment1 =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment1) as AutocompleteSupportFragment?
        val autocompleteSupportFragment2 =
            supportFragmentManager.findFragmentById(R.id.autocomplete_fragment2) as AutocompleteSupportFragment?

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
                val textView = findViewById<TextView>(R.id.tv2)
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

                validacionOrigen = true
                mostrarMapa()
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
                val textView = findViewById<TextView>(R.id.tv3)

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


                validacionDestino = true
                mostrarMapa()
                /*textView.text = "Name: $name \nAddress: $address \nPhone Number:  \n" +
                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
                        "Rating: $rating \nUser ratings: $userRatings"*/
            }
            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setup(email: String) {
        cargarDatos(email)
    }

    private fun cargarDatos(email: String){

        val docRef = db.collection("Usuarios").document(email)
        docRef.get().addOnSuccessListener { document ->
            if (document != null) {
                Log.d("TAG", "${document.id} => ${document.data}")

                val nombre                  = document.data?.getValue("usuario_nombre").toString()
                val apellido                = document.data?.getValue("usuario_apellido").toString()
                nombresValeViajeEditText.text    = "$nombre $apellido"
                nroMovilViajeEditText.text       = document.data?.getValue("usuario_movil").toString()
                patenteViajeEditText.text        = document.data?.getValue("usuario_patente").toString()
                rutValeViajeEditText.text        = document.data?.getValue("usuario_rut").toString()

            } else {
                Log.d("TAG", "El documento tiene un error document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
            }
    }


    private fun mostrarMapa(){
        if (validacionOrigen and validacionDestino){
            linealMapaValeFragment.visibility = View.VISIBLE
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapaValeFragmentEmpresa) as SupportMapFragment
            mapFragment.getMapAsync(this)
            observeLiveData()
        }else{
            linealMapaValeFragment.visibility = View.GONE
        }
    }

    private fun observeLiveData() {
        viewModel.directionsResult.observe(this, Observer {
            Log.d(ValeDirecciones::class.java.simpleName, "result: $it")
            updatePolyline(it, mMap)
            // カメラ移動.
            moveCamera()
        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val fromTokyo = lat1?.let { long1?.let { it1 -> MapsLatLng(it, it1) } }
        val toKanda = lat2?.let { long2?.let { it1 -> MapsLatLng(it, it1) } }
        if (fromTokyo != null) {
            if (toKanda != null) {
                viewModel.execute(fromTokyo, toKanda)
            }
        }

    }
    private fun moveCamera() {
        // Add a marker in Sydney and move the camera
        val tokyo = lat2?.let { long2?.let { it1 -> LatLng(it, it1) } }

        val marcardor1 = mMap?.apply {
            tokyo?.let { MarkerOptions().position(it).title("Marker in Tokyo") }
                ?.let { addMarker(it) }
            // moveCamera(CameraUpdateFactory.newLatLng(tokyo))
            tokyo?.let { CameraUpdateFactory.newLatLngZoom(it, ZOOM_SIZE) }?.let { moveCamera(it) }
        }
    }

    private fun updatePolyline(directionsResult: DirectionsResult?, googleMap: GoogleMap?) {
        googleMap ?: return
        directionsResult ?: return
        removePolyline()
        addPolyline(directionsResult, googleMap)
    }

    private fun removePolyline() {
        if (mMap != null && polyline != null) {
            polyline?.remove()
        }
    }

    private fun addPolyline(directionsResult: DirectionsResult, map: GoogleMap) {
        val polylineOptions = PolylineOptions()
        polylineOptions.width(POLYLINE_WIDTH)
        // ARGB32bit形式.
        val colorPrimary = ContextCompat.getColor(this, R.color.map_polyline_stroke)
        polylineOptions.color(colorPrimary)
        val decodedPath = PolyUtil.decode(directionsResult.routes[overview].overviewPolyline.encodedPath)
        polyline = map.addPolyline(polylineOptions.addAll(decodedPath))
    }

    private fun addPolyline2(directionsResult: DirectionsResult, map: GoogleMap) {
        val bounds = LatLngBounds.builder()
        val route = directionsResult.routes[0]
        val polylineOptions = PolylineOptions()
        for (latLng in route.overviewPolyline.decodePath()) {
            polylineOptions.add(LatLng(latLng.lat, latLng.lng))
            bounds.include(LatLng(latLng.lat, latLng.lng))
        }
        polylineOptions.width(POLYLINE_WIDTH)
        val colorPrimary = ContextCompat.getColor(this, R.color.map_polyline_stroke)
        polylineOptions.color(colorPrimary)
        polyline = mMap?.addPolyline(polylineOptions)
    }


}