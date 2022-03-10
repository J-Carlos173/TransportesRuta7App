package com.mana.transportesruta7app

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*



class MainActivity : AppCompatActivity() {
    val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //setup
        setup()
    }

    private fun setup() {

        savebutton.setOnClickListener() {
            val user = hashMapOf(
                "first" to "Ada",
                "last" to "Lovelace",
                "born" to 1815
            )

// Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Log", "********Funciono************: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Log", "**********noooooooooo*************", e)
                }
        }

        delButton.setOnClickListener(){

        }

    }


}
