package com.mana.transportesruta7app

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.google.maps.model.DirectionsResult
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.*
import kotlinx.android.synthetic.main.activity_vale_direcciones.linealMapaValeFragment
import java.text.SimpleDateFormat
import java.util.*
import com.google.maps.model.LatLng as MapsLatLng


class CrearValeEmpresaActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val ZOOM_SIZE = 14f
        private const val POLYLINE_WIDTH = 12f
    }

    private var lat1 :Double? = 0.0
    private var long1 :Double? = 0.0
    private var lat2 :Double? = 0.0
    private var long2 :Double? = 0.0
    private var mMap: GoogleMap? = null
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

        transparent_image.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    scrollView.requestDisallowInterceptTouchEvent(true)
                    Log.d(TAG, "ABAJO")
                }
                MotionEvent.ACTION_DOWN -> {
                    scrollView.requestDisallowInterceptTouchEvent(false)
                    Log.d(TAG, "ARRIBA")
                }
            }
            v?.onTouchEvent(event) ?: true
        }

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
                            var ruta_inicio     = document.get("ruta_inicio")
                            val ruta_fin        = document.get("ruta_fin")
                            val origen          = document.get("origen")
                            val destino          = document.get("destino")


                            // Se trabajan las latitudes de origen
                            var latInicio = ruta_inicio.toString().replace("[", "").replace("]","").replace(" ", "")
                            var numeroCaracteres = latInicio.length
                            var indexComa = latInicio.indexOf(",")
                            val latInicioDef = latInicio.subSequence(0, indexComa -1)
                            val longInicioDef = latInicio.subSequence(indexComa+1, numeroCaracteres)


                            // Se trabajan las latitudes de destino
                            var latFin = ruta_fin.toString().replace("[", "").replace("]","").replace(" ", "")
                            numeroCaracteres = latFin.length
                            indexComa = latFin.indexOf(",")
                            val latFinDef = latFin.subSequence(0, indexComa -1)
                            val longFinDef = latFin.subSequence(indexComa+1, numeroCaracteres)




                            fun String.fullTrim() = trim().replace("\uFEFF", "")
                            lat1 = latInicioDef.toString().fullTrim().toDouble()
                            long1 = longInicioDef.toString().fullTrim().toDouble()
                            lat2 = latFinDef.toString().fullTrim().toDouble()
                            long2 = longFinDef.toString().fullTrim().toDouble()


                            val ruta_ccosto     = document.get("ruta_centrocosto")
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
        // ARGB32bit形式.
        val colorPrimary = ContextCompat.getColor(this, R.color.map_polyline_stroke)
        polylineOptions.color(colorPrimary)
        val decodedPath = PolyUtil.decode(directionsResult.routes[overview].overviewPolyline.encodedPath)
        polyline = map.addPolyline(polylineOptions.addAll(decodedPath))
    }



}







