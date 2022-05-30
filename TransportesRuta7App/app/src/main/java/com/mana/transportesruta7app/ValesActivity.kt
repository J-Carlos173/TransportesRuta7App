package com.mana.transportesruta7app


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_vales.*
import org.json.JSONArray
import java.io.File
import java.io.OutputStream


class ValesActivity : AppCompatActivity() {


    val storage = Firebase.storage
    val db = Firebase.firestore
    var counter = 1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vales)

        setup()
    }


    private fun setup() {
        exportarButton.setOnClickListener() {
            chooseDirectory()
        }
    }

    fun chooseDirectory(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        //startActivityForResult(intent, 42) // Deprecated
        resultLauncher.launch(intent)
    }
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val treeUri: Uri? = data?.data
            val pickedDir = DocumentFile.fromTreeUri(this!!, treeUri!!) // change "this" for your context

            // Create a new file and write into it
            saveFile(pickedDir)
        }
    }

    fun saveFile(pickedDir: DocumentFile?) {
        if (isStoragePermissionGranted()) { // check or ask permission
            val fname = "fileName.csv"
            val file = pickedDir?.createFile("text/csv", fname) // There you can change the file's type to your preference
            // For example, if you want save a txt file, you write "text/txt" and change the fileName's extension to fileName.txt

            try {

                val list: MutableList<String> = ArrayList()
                db.collection("Vales").get().addOnSuccessListener { documents ->

                val out: OutputStream = this!!.contentResolver.openOutputStream(file?.uri!!)!! // change "this" for your context
                val csvWriter = CSVWriter(out)


                    val vale_nombre         = ""
                    val vale_rut_cliente    = ""
                    val vale_tipo           = ""
                    val vale_patente        = ""
                    val vale_movil          = ""
                    val vale_fecha          = ""
                    val vale_empresa        = ""
                    val vale_email          = ""
                    val vale_chofer         = ""
                    val vale_cc             = ""
                    val vale_inicio         = ""
                    val vale_fin            = ""

                    list.add("cliente,rut cliente,tipo vale,patente,movil,fecha,empresa,email,chofer,cc, inicio, fin")
                    val array: Array<String> = list.toTypedArray()
                    csvWriter.writeNext(array)
                    list.clear()

                    for (document in documents) {

                    list.add(document.data.get("vale_nombre_cliente").toString())
                    list.add(document.data.get("vale_rut_cliente").toString())
                    list.add(document.data.get("vale_Tipo").toString())
                    list.add(document.data.get("vale_Patente").toString())
                    list.add(document.data.get("vale_Movil").toString())
                    list.add(document.data.get("vale_Fecha").toString())
                    list.add(document.data.get("vale_Empresa").toString())
                    list.add(document.data.get("vale_Email").toString())
                    list.add(document.data.get("vale_Chofer").toString())
                    list.add(document.data.get("vale_CC").toString())
                    list.add(document.data.get("vale_Inicio").toString())
                    list.add(document.data.get("vale_Fin").toString())

                    val array: Array<String> = list.toTypedArray()
                    csvWriter.writeNext(array)
                    list.clear()

                    }




                out.close()
                Toast.makeText(this, "Report saved successfully", Toast.LENGTH_LONG).show()

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
    fun isStoragePermissionGranted(): Boolean {
        val tag = "Storage Permission"
        return if (Build.VERSION.SDK_INT >= 23) {
            if (this?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted")
                true
            } else {
                Log.v(tag, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this!!, // change "this" for your activity
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            Log.v(tag, "Permission is granted")
            true
        }
    }



}


