package com.mana.transportesruta7app


import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_vales.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File

class ValesActivity : AppCompatActivity() {



    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vales)

        setup()
    }
    private fun setup() {
        exportarButton.setOnClickListener(){
            val list : MutableList<String> = ArrayList()
            val jsonArray: JSONArray = JSONArray()
            db.collection("Vales").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(document.data.toString())
                }
                val jsArray = JSONArray(list)
                println(jsArray)




            }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents: ", exception)
                }


        }
    }



}

