package com.mana.transportesruta7app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_login.*
import com.google.maps.model.LatLng as MapsLatLng
import kotlinx.android.synthetic.main.activity_vale_direcciones.*



class ValeDirecciones : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val ZOOM_SIZE = 14f
        private const val POLYLINE_WIDTH = 12f
    }

    private var validacionOrigen = false
    private var validacionDestino = false



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
                val latitude2 = latlng?.latitude
                val longitude2 = latlng?.longitude

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

    private fun mostrarMapa(){
        if (validacionOrigen and validacionDestino){

            linealMapaValeFragment.visibility = View.VISIBLE
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapaValeFragment) as SupportMapFragment
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
        val fromTokyo = MapsLatLng(-33.57038785474524, -70.60979397104093)
        val toKanda = MapsLatLng(-33.4446003631385, -70.65485291707162)
        viewModel.execute(fromTokyo, toKanda)
    }
    private fun moveCamera() {
        // Add a marker in Sydney and move the camera
        val tokyo = LatLng(-33.57038785474524, -70.60979397104093)
        mMap?.apply {
            addMarker(MarkerOptions().position(tokyo).title("Marker in Tokyo"))
            // moveCamera(CameraUpdateFactory.newLatLng(tokyo))
            moveCamera(CameraUpdateFactory.newLatLngZoom(tokyo, ZOOM_SIZE))
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