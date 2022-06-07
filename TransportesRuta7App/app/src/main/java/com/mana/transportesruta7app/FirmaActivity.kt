package com.mana.transportesruta7app

import android.annotation.SuppressLint
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
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_firma.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.widget.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_crear_vale_empresa.*


class FirmaActivity : AppCompatActivity() {
    private var btnClear: Button? = null
    private var btnSave: Button? = null
    private var file: File? = null
    private var canvasLL: LinearLayout? = null
    private var view: View? = null
    private var mSignature: signature? = null
    private var bitmap: Bitmap? = null

    val db = Firebase.firestore

    companion object {
        const val STROKE_WIDTH = 5f
        const val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
    }
    // Creando un Directorio aparte para guardar imagenes generadas
    private var directory = Environment.getExternalStorageDirectory().path + "/Firma/"
    private var pic_name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    private var StoredPath = "$directory$pic_name.png"

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firma)


        val bundle          = intent.extras
        val email           = bundle?.getString("email").toString()
        val provider        = bundle?.getString("provider").toString()
        val vale_fecha      = bundle?.getString("vale_fecha").toString()
        val vale_Chofer     = bundle?.getString("vale_Chofer").toString()
        val vale_Patente    = bundle?.getString("vale_Patente").toString()
        val vale_Movil      = bundle?.getString("vale_Movil").toString()
        val vale_Empresa    = bundle?.getString("vale_Empresa").toString()
        val vale_CC         = bundle?.getString("vale_CC").toString()
        val vale_Tipo       = bundle?.getString("vale_Tipo").toString()
        val vale_Inicio     = bundle?.getString("vale_Inicio").toString()
        val vale_Fin        = bundle?.getString("vale_Fin").toString()

        setup(email)

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
            val createdAt = FieldValue.serverTimestamp()
            val vale = hashMapOf(
                "timeStamp"             to createdAt,
                "vale_Email"            to email,
                "vale_Fecha"            to vale_fecha,
                "vale_Chofer"           to vale_Chofer,
                "vale_Patente"          to vale_Patente,
                "vale_Movil"            to vale_Movil,
                "vale_Empresa"          to vale_Empresa,
                "vale_CC"               to vale_CC,
                "vale_Tipo"             to vale_Tipo,
                "vale_Inicio"           to vale_Inicio,
                "vale_Fin"              to vale_Fin,
                "vale_nombre_cliente"   to nombreClienteText.text.toString(),
                "vale_rut_cliente"      to rutClienteText.text.toString()
            )
            val sdf = SimpleDateFormat("yyyy")
            val ano = sdf.format(Date())
            val sdf2 = SimpleDateFormat("M")
            var mes = sdf2.format(Date())

            if (mes == "1"){ mes = "Enero" }
            else if (mes == "2"){ mes = "Febrero" }
            else if (mes == "3"){ mes = "Marzo" }
            else if (mes == "4"){ mes = "Abril" }
            else if (mes == "5"){ mes = "Mayo" }
            else if (mes == "6"){ mes = "Junio" }
            else if (mes == "7"){ mes = "Julio" }
            else if (mes == "8"){ mes = "Agosto" }
            else if (mes == "9"){ mes = "Septiembre" }
            else if (mes == "10"){ mes = "Octubre" }
            else if (mes == "11"){ mes = "Noviembre" }
            else  { mes = "Diciembre" }

            db.collection("Vales").document(ano).collection(mes).document().set(vale).addOnSuccessListener {
                Toast.makeText(applicationContext, "Vale Creado", Toast.LENGTH_SHORT).show()
                Toast.makeText(applicationContext, "Guardado con exito", Toast.LENGTH_SHORT).show()

                showHome(email, ProviderType.BASIC)
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "No Funciono", Toast.LENGTH_SHORT).show()
            }


        }

        // Metodo para crear Directorio , Si el Directorio no exixte
        file = File(directory)
        if (!file!!.exists()) {
            file!!.mkdir()
        }
    }
    private fun setup(email: String) {

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
                (dirtyRect.left - Companion.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.top - Companion.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.right + Companion.HALF_STROKE_WIDTH).toInt(),
                (dirtyRect.bottom + Companion.HALF_STROKE_WIDTH).toInt()
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


        //Boton Crear
        /*
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
        */
        init {
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = Companion.STROKE_WIDTH
        }
    }

    private fun showHome(email: String, provider: ProviderType) {
        val homeIntent = Intent(this,HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
    }
}