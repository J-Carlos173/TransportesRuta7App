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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.linealMapaValeFragment
import java.text.SimpleDateFormat
import java.util.*
import com.google.maps.model.LatLng as MapsLatLng


class CrearValeEmpresaActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {

    companion object {
        private const val ZOOM_SIZE = 14f
        private const val POLYLINE_WIDTH = 12f
        const val REQUEST_CODE_LOCATION = 0
    }

    private var lat1 :Double? = 0.0
    private var long1 :Double? = 0.0
    private var lat2 :Double? = 0.0
    private var long2 :Double? = 0.0
    //private var mMap: GoogleMap? = null
    private lateinit var mMap: GoogleMap
    private var polyline: Polyline? = null
    private val overview = 0
    private val viewModel by viewModels<MapsViewModel>()

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

    private fun cargarSpnRutas(){
        val list : MutableList<String> = ArrayList()
        list.add("Seleccionar Ruta")
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
                println(ruta)
                if (ruta == "Seleccionar Ruta") {

                } else {


                    db.collection("RutasEmpresa").document(ruta.toString())
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val ruta_nombre     = document.get("ruta_nombre")
                                val ruta_empresa    = document.get("ruta_empresa")
                                var ruta_inicio     = document.get("ruta_inicio")
                                val ruta_fin    = document.get("ruta_fin")
                                val origen      = document.get("origen")
                                val destino         = document.get("destino")


                                // Se trabajan las latitudes de origen
                                var latInicio =
                                    ruta_inicio.toString().replace("[", "").replace("]", "")
                                        .replace(" ", "")
                                var numeroCaracteres = latInicio.length
                                var indexComa = latInicio.indexOf(",")
                                val latInicioDef = latInicio.subSequence(0, indexComa - 1)
                                val longInicioDef =
                                    latInicio.subSequence(indexComa + 1, numeroCaracteres)


                                // Se trabajan las latitudes de destino
                                var latFin = ruta_fin.toString().replace("[", "").replace("]", "")
                                    .replace(" ", "")
                                numeroCaracteres = latFin.length
                                indexComa = latFin.indexOf(",")
                                val latFinDef = latFin.subSequence(0, indexComa - 1)
                                val longFinDef = latFin.subSequence(indexComa + 1, numeroCaracteres)


                                fun String.fullTrim() = trim().replace("\uFEFF", "")
                                lat1 = latInicioDef.toString().fullTrim().toDouble()
                                long1 = longInicioDef.toString().fullTrim().toDouble()
                                lat2 = latFinDef.toString().fullTrim().toDouble()
                                long2 = longFinDef.toString().fullTrim().toDouble()


                                val ruta_ccosto = document.get("ruta_centrocosto")
                                centroCostoText.setText(ruta_ccosto.toString())
                                EmpresaText.setText(ruta_empresa.toString())
                                rutaInicioText.setText(origen.toString())
                                rutaFinText.setText(destino.toString())

                                Log.d("Latitude Inicio", longInicioDef.toString())
                                Log.d("Longitude Inicio", longInicioDef.toString())
                                //Obtener nombre a partir de latitude


                                mostrarMapa()

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
        val vale_CC         = centroCostoText.text.toString()
        val vale_Empresa    = EmpresaText.text.toString()
        val vale_Tipo       = "Empresa"
        val ruta_Inicio     = rutaInicioText.text.toString()
        val ruta_Fin        = rutaFinText.text.toString()

        showFirma(vale_Email, ProviderType.BASIC,vale_Fecha,vale_Chofer,vale_Patente,vale_Movil,vale_Empresa,vale_CC,vale_Tipo,ruta_Inicio,ruta_Fin)


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
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapaValeFragmentEmpresa) as SupportMapFragment
            mapFragment.getMapAsync(this)
            observeLiveData()
    }

    private fun observeLiveData() {
        viewModel.directionsResult.observe(this, Observer {
            Log.d(CrearValeEmpresaActivity::class.java.simpleName, "result: $it")
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
        val Marcador1 = lat1?.let { long1?.let { it1 -> MapsLatLng(it, it1) } }
        val Marcador2 = lat2?.let { long2?.let { it1 -> MapsLatLng(it, it1) } }
        if (Marcador1 != null) {
            if (Marcador2 != null) {
                viewModel.execute(Marcador1, Marcador2)
            }
        }
        enableLocation()
    }

    private fun moveCamera() {
        // Add a marker in Sydney and move the camera
        val tokyo = lat2?.let { long2?.let { it1 -> LatLng(it, it1) } }

        val marcardor1 = mMap?.apply {
            tokyo?.let { MarkerOptions().position(it).title("Marker in Tokyo") }
                ?.let { addMarker(it) }
            // moveCamera(CameraUpdateFactory.newLatLng(tokyo))
            tokyo?.let { CameraUpdateFactory.newLatLngZoom(it, ValeDirecciones.ZOOM_SIZE) }?.let { moveCamera(it) }
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

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this,"Estas en  ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

}







