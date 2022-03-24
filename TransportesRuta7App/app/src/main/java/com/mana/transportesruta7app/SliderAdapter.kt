package com.mana.transportesruta7app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SliderAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 25

    override fun createFragment(position: Int): Fragment {
        val db = Firebase.firestore
        val list : MutableList<String> = mutableListOf()
        val fragment = SliderFragment()

        db.collection("Vales").whereEqualTo("Email", "krlos173@hotmail.com").get().addOnSuccessListener { documents ->
            for (document in documents) {
                //val Email = document.data.getValue("Email")

                Log.d("TAG", "${document.id}")
                Log.d("TAG", "${document.data.getValue("Cliente")}")
                list.add(document.id)
            }
            Log.d("TAG", list.toString())
            val movil = "hola"
            fragment.arguments = Bundle().apply {
            putString(NUMERO_VALE,list[position])

            Log.d("TAG","*****************"+position.toString())
                Log.d("TAG","*****************"+list[0])
                Log.d("TAG","*****************"+list[1])
                Log.d("TAG","*****************"+list[2])
                Log.d("TAG","*****************"+list[3])

            putString(MOVIL_OBJECT,movil)

        }

        }.addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
        }
        return fragment
    }

}