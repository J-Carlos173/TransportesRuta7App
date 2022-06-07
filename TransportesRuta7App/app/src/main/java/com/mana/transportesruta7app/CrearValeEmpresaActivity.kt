package com.mana.transportesruta7app

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import kotlinx.android.synthetic.main.activity_firma.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.linealMapaValeFragment
import java.text.SimpleDateFormat
import java.util.*
import com.google.maps.model.LatLng as MapsLatLng


class CrearValeEmpresaActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    private var lat1 :Double?       = 0.0
    private var long1 :Double?      = 0.0
    private var lat2 :Double?       = 0.0
    private var long2 :Double?      = 0.0
    private val overview            = 0
    private var polyline: Polyline? = null

    private var origenNombre:String?    = ""
    private var destinoNombre :String?  = ""


    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient

    val db = Firebase.firestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_vale_empresa)

        // Autocompletado y mapa
        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager
            .getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
        val value = ai.metaData["api_key"]
        val apiKey = value.toString()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }

        val bundle = intent.extras
        val email = bundle?.getString("email").toString()
        val provider = bundle?.getString("provider").toString()
        setup(email)


        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
        val autocompleteSupportFragment1 = supportFragmentManager.findFragmentById(R.id.rutaInicioText) as AutocompleteSupportFragment?
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
        autocompleteSupportFragment1.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                // Informacion del lugar
                val name        = place.name
                val address     = place.address
                val latlng      = place.latLng
                val latitude1   = latlng?.latitude
                val longitude1  = latlng?.longitude
                lat1            = latitude1
                long1           = longitude1
                origenNombre    = address



            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Setup
    private fun setup(email: String) {
        cargarDatos(email)
        //cargarSpnRutas()
        //cargarRuta()
        mostrarMapa()

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
                nroMovilEditText.text       = document.data?.getValue("usuario_movil").toString()
                patenteEditText.text        = document.data?.getValue("usuario_patente").toString()
                rutValeEditText.text        = document.data?.getValue("usuario_rut").toString()

                  } else {
                Log.d("TAG", "El documento tiene un error document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
            }
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
    private fun showFirma(email: String, provider: ProviderType, fecha: String, chofer: String, patente: String, movil: String, empresa: String, cc: String, tipo: String, inicio: String, fin: String) {
        val firmaIntent = Intent(this,FirmaActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
            putExtra("vale_fecha", fecha)
            putExtra("vale_Chofer", chofer)
            putExtra("vale_Patente", patente)
            putExtra("vale_Movil", movil)
            putExtra("vale_Empresa", empresa)
            putExtra("vale_Tipo", tipo)
            putExtra("vale_CC", cc)
            putExtra("vale_Inicio", inicio)
            putExtra("vale_Fin", fin)
        }
        startActivity(firmaIntent)
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showHome(email.toString(), ProviderType.BASIC)
    }

    private fun mostrarMapa(){
            linealMapaValeFragment.visibility = View.VISIBLE
            val mapFragment = supportFragmentManager.findFragmentById(R.id.mapaValeFragmentEmpresa) as SupportMapFragment
            mapFragment.getMapAsync(this)

    }


    private fun isLocationPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    private fun enableLocation(){
        if(!::mMap.isInitialized) return
        if(isLocationPermissionGranted()){
            mMap.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajutes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                } else {
                    Toast.makeText(
                        this,
                        "Para activar la localizacion ve a ajustes y acepta los permisos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            else -> {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        fusedLocation.lastLocation.addOnSuccessListener { location ->
            if (location != null){
                val ubicacion = LatLng(location.latitude,location.longitude)
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(ubicacion,15f))
            }

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
        polylineOptions.width(ValeDirecciones.POLYLINE_WIDTH)
        //Color de la linea
        val colorPrimary = ContextCompat.getColor(this, R.color.black)
        polylineOptions.color(colorPrimary)
        val decodedPath = PolyUtil.decode(directionsResult.routes[overview].overviewPolyline.encodedPath)
        polyline = map.addPolyline(polylineOptions.addAll(decodedPath))
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::mMap.isInitialized) return
        if(isLocationPermissionGranted()){
            mMap.isMyLocationEnabled = true
            Toast.makeText(this,"Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        return false
        Toast.makeText(this,"Obteniendo Ubicacion...", Toast.LENGTH_SHORT).show()
    }

    override fun onMyLocationClick(p0: Location)  {
        Toast.makeText(this,"Estas en  ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()

        var ubicacionActual = LatLng(p0.latitude,-p0.longitude)

    }



}







