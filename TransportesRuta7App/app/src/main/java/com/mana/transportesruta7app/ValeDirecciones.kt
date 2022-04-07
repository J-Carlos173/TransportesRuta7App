package com.mana.transportesruta7app

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

class ValeDirecciones : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vale_direcciones)

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
                val latitude = latlng?.latitude
                val longitude = latlng?.longitude

                val isOpenStatus : String = if(place.isOpen == true){
                    "Open"
                } else {
                    "Closed"
                }

                val rating = place.rating
                val userRatings = place.userRatingsTotal



                textView.text = "Name: $name \nAddress: $address \nPhone Number:  \n" +
                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
                        "Rating: $rating \nUser ratings: $userRatings"
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
                val latitude = latlng?.latitude
                val longitude = latlng?.longitude

                val isOpenStatus : String = if(place.isOpen == true){
                    "Open"
                } else {
                    "Closed"
                }

                val rating = place.rating
                val userRatings = place.userRatingsTotal



                textView.text = "Name: $name \nAddress: $address \nPhone Number:  \n" +
                        "Latitude, Longitude: $latitude , $longitude \nIs open: $isOpenStatus \n" +
                        "Rating: $rating \nUser ratings: $userRatings"
            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }
}