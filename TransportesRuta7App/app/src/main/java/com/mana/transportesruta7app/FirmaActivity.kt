package com.mana.transportesruta7app

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class FirmaActivity : AppCompatActivity() {
    private var btnClear: Button? = null
    private var btnSave: Button? = null
    private var file: File? = null
    private var canvasLL: LinearLayout? = null
    private var view: View? = null
    private var mSignature: signature? = null
    private var bitmap: Bitmap? = null


    companion object {
        const val STROKE_WIDTH = 5f
        const val HALF_STROKE_WIDTH = STROKE_WIDTH / 2
    }
    // Creando un Directorio aparte para guardar imagenes generadas
    private var directory = Environment.getExternalStorageDirectory().path + "/Firma/"
    private var pic_name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    private var StoredPath = "$directory$pic_name.png"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firma)
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




        init {
            paint.isAntiAlias = true
            paint.color = Color.BLACK
            paint.style = Paint.Style.STROKE
            paint.strokeJoin = Paint.Join.ROUND
            paint.strokeWidth = Companion.STROKE_WIDTH
        }
    }
}