package com.mana.transportesruta7app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

import kotlinx.android.synthetic.main.activity_vale_direcciones.linealMapaValeFragment
import java.util.*


class CrearValeEmpresaActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }
    private var origenNombre:String?    = ""
    private var destinoNombre :String?  = ""
    private var latDestino :Double?     = 0.0
    private var longDestino :Double?    = 0.0
    private var latOrigen :Double?      = 0.0
    private var longOrigen :Double?     = 0.0
    private val overview                = 0

    private var polyline: Polyline?     = null
    private var validacionOrigen        = false
    private var validacionDestino       = false

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocation: FusedLocationProviderClient
    private val viewModel by viewModels<MapsViewModel>()

    val db = Firebase.firestore

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_vale_empresa)

        // Autocompletado y mapa
        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo = applicationContext.packageManager.getApplicationInfo(applicationContext.packageName, PackageManager.GET_META_DATA)
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

        val direccionAutocompletada = supportFragmentManager.findFragmentById(R.id.rutaInicioText) as AutocompleteSupportFragment?
        direccionAutocompletada!!.setPlaceFields(
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
        direccionAutocompletada.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {

                // Informacion del lugar
                val name        = place.name
                val address     = place.address
                val latlng      = place.latLng
                val latitude1   = latlng?.latitude
                val longitude1  = latlng?.longitude
                latDestino      = latitude1
                longDestino     = longitude1
                origenNombre    = address
                validacionDestino = true

                mostrarMapa()

            }

            override fun onError(status: Status) {
                Toast.makeText(applicationContext,"Some error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Setup
    private fun setup(email: String) {
        cargarDatos(email)
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
        }.addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
        }
    }

    //Cambio de ventana
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
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
            observeLiveData()

    }
    private fun observeLiveData() {
        viewModel.directionsResult.observe(this, Observer {
            Log.d(ValeDirecciones::class.java.simpleName, "result: $it")
            updatePolyline(it, mMap)
            moveCamera()
        })
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
                    Toast.makeText(this, "Para activar la localizacion ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
                }
            else -> {}
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        enableLocation()
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
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,15f))

                val latitude1   = location.latitude
                val longitude1  = location.longitude
                latOrigen       = latitude1
                longOrigen      = longitude1
                //origenNombre    = address
                validacionOrigen = true
            }

       }
        val origen = latOrigen?.let { longOrigen?.let { it1 -> com.google.maps.model.LatLng(it, it1) } }
        val destino = latDestino?.let { longDestino?.let { it1 -> com.google.maps.model.LatLng(it, it1) } }
        if (origen != null) {
            if (destino != null) {
                //viewModel.execute(origen, destino)
            }
        }

    }


    private fun moveCamera() {
        // Add a marker in Sydney and move the camera
        val marcador = latDestino?.let { longDestino?.let { it1 -> LatLng(it, it1) } }

        val marcardor1 = mMap?.apply {
            marcador?.let { MarkerOptions().position(it).title("Marker in Tokyo") }
                ?.let { addMarker(it) }
            // moveCamera(CameraUpdateFactory.newLatLng(tokyo))
            marcador?.let { CameraUpdateFactory.newLatLngZoom(it, ValeDirecciones.ZOOM_SIZE) }?.let { moveCamera(it) }
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
            Toast.makeText(this,"Permisos activos", Toast.LENGTH_SHORT).show()
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







