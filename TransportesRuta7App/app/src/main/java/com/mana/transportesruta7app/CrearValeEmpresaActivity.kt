package com.mana.transportesruta7app


import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class CrearValeEmpresaActivity : AppCompatActivity(),AdapterView.OnItemSelectedListener {


    private var btnClear: Button? = null
    private var btnSave: Button? = null
    private var file: File? = null
    private var canvasLL: LinearLayout? = null
    private var view: View? = null
    private var mSignature: CrearValeEmpresaActivity.signature? = null
    private var bitmap: Bitmap? = null
    val db = Firebase.firestore

    companion object {
        const val STROKE_WIDTH = 5f
        const val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        println("item seleccionado")
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        println("nada seleccionado")
    }
    private fun cargarRuta(){

        val spinner: Spinner = findViewById(R.id.spnRuta)
        spinner.onItemSelectedListener = this
        val db = FirebaseFirestore.getInstance()
        val ruta = spnRuta.getItemAtPosition(spnRuta.selectedItemPosition)
        val docRef = db.collection("RutasEmpresa").document(ruta.toString())

    }

    // Creando un Directorio aparte para guardar imagenes generadas
    private var directory = Environment.getExternalStorageDirectory().path + "/Firma/"
    private var pic_name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    private var StoredPath = "$directory$pic_name.png"

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


        canvasLL = findViewById<View>(R.id.canvasLL) as LinearLayout
        mSignature = signature(applicationContext, null)
        mSignature!!.setBackgroundColor(Color.WHITE)
        canvasLL!!.addView(
            mSignature,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        btnClear = findViewById<View>(R.id.btnclear) as Button
        btnSave = findViewById<View>(R.id.btnsave) as Button
        view = canvasLL
        btnClear!!.setOnClickListener { mSignature!!.clear() }
        btnSave!!.setOnClickListener {
            view!!.isDrawingCacheEnabled = true
            mSignature!!.save(view, StoredPath)
            Toast.makeText(applicationContext, "Guardado con exito", Toast.LENGTH_SHORT).show()
        }

        // Metodo para crear Directorio , Si el Directorio no exixte
        file = File(directory)
        if (!file!!.exists()) {
            file!!.mkdir()
        }
    }
    private fun setup(email: String) {
        //cargarCenctrodeCostos()
        //cargarEmpresas()
        cargarDirecciones()

        cargarDatos(email)
        cargarSpnRutas()
        cargarRuta()

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
        // [END get_m
    }
    private fun crearVale(email: String) {

        //val sdf = SimpleDateFormat("dd/M/yyyy")
        val sdf = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss ss")
        val currentDate = sdf.format(Date())


        val vale = hashMapOf(

                "vale_Email"            to email,
                "vale_Fecha"            to currentDate.toString(),
                "vale_Chofer"           to nombresValeEditText.text.toString(),
                "vale_Rut"              to rutValeEditText.text.toString(),
                "vale_Patente"          to patenteEditText.text.toString(),
                "vale_Movil"            to nroMovilEditText.text.toString(),
                "vale_Empresa"          to centroCostoText.text.toString(),
                "vale_CC"               to EmpresaText.text.toString(),
                "vale_Inicio"           to spnRuta.getItemAtPosition(spnRuta.selectedItemPosition),
                "vale_Final"            to spnRuta.getItemAtPosition(spnRuta.selectedItemPosition),
                "vale_cliente_nombre"   to nombreClienteText.text.toString(),
                "vale_cliente_rut"      to rutClienteText.text.toString(),
                "vale_Tipo"             to "Empresa",
                "Firma"                 to "Firma.png"
        )

        val contador = db.collection("Vales")
        contador.get().addOnSuccessListener { documentSnapshots ->
        //val size = documentSnapshots.size() + 1

        db.collection("Vales").document().set(vale).addOnSuccessListener {
            Toast.makeText(applicationContext, "Vale Creado", Toast.LENGTH_SHORT).show()
            showHome(email, ProviderType.BASIC)
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "No Funciono", Toast.LENGTH_SHORT).show()
        }
    }
    }
    /*
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
    }*/
    /*private fun cargarEmpresas(){
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
    }*/
    private fun cargarDirecciones(){
        val docRef = db.collection("Listas").document("Direcciones")
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
                //spnDireccionInicio.adapter = adapter
                //spnDireccionFin.adapter = adapter
            }
            else {
                Log.d("TAG", "No such document")
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "get failed with ", exception)
        }
    }
    private fun cargarDatos(email: String){
        println("*****************************")
        println(email)
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

                    Log.d("TAG", "******************Todo Correcto******************")
                    Toast.makeText(this, "Se han cargado los datos", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("TAG", "El documento tiene un error document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "No se encontro el Documento", exception)
            }
    }
    override fun onBackPressed() {
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        showHome(email.toString(), ProviderType.BASIC)
    }
    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
    inner class signature(context: Context?, attrs: AttributeSet?) :
        View(context, attrs) {
        private val paint = Paint()
        private val path = Path()
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private val dirtyRect = RectF()
        fun save(v: View?, StoredPath: String?) {
            Log.v("log_tag", "Width: " + v!!.width)
            Log.v("log_tag", "Height: " + v.height)
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(canvasLL!!.width, canvasLL!!.height, Bitmap.Config.RGB_565)
            }
            val canvas = Canvas(bitmap!!)
            try {
                // exportar el archivo
                val mFileOutStream = FileOutputStream(StoredPath)
                v.draw(canvas)

                // convertir el archivo exportado a .png
                bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream)
                mFileOutStream.flush()
                mFileOutStream.close()
            } catch (e: Exception) {
                Log.v(" ", e.toString())
            }
        }

        fun clear() {
            path.reset()
            invalidate()
        }

        override fun onDraw(canvas: Canvas) {
            canvas.drawPath(path, paint)
        }

        override fun onTouchEvent(event: MotionEvent): Boolean {
            val eventX = event.x
            val eventY = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    path.moveTo(eventX, eventY)
                    lastTouchX = eventX
                    lastTouchY = eventY
                    return true
                }
                MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {
                    resetDirtyRect(eventX, eventY)
                    val historySize = event.historySize
                    var i = 0
                    while (i < historySize) {
                        val historicalX = event.getHistoricalX(i)
                        val historicalY = event.getHistoricalY(i)
                        expandDirtyRect(historicalX, historicalY)
                        path.lineTo(historicalX, historicalY)
                        i++
                    }
                    path.lineTo(eventX, eventY)
                }
                else -> {
                    debug("Envento tactil ignorado: $event")
                    return false
                }
            }
            invalidate(
                (dirtyRect.left - CrearValeEmpresaActivity.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.top - CrearValeEmpresaActivity.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.right + CrearValeEmpresaActivity.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.bottom + CrearValeEmpresaActivity.HALF_STROKE_WIDTH).toInt()
            )
            lastTouchX = eventX
            lastTouchY = eventY
            return true
        }

        private fun debug(string: String) {
            Log.v("log_tag", string)
        }

        private fun expandDirtyRect(historicalX: Float, historicalY: Float) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX
            }
            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY
            }
        }

        private fun resetDirtyRect(eventX: Float, eventY: Float) {
            dirtyRect.left = Math.min(lastTouchX, eventX)
            dirtyRect.right = Math.max(lastTouchX, eventX)
            dirtyRect.top = Math.min(lastTouchY, eventY)
            dirtyRect.bottom = Math.max(lastTouchY, eventY)
        }




        init {
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = CrearValeEmpresaActivity.STROKE_WIDTH
        }
    }
}







