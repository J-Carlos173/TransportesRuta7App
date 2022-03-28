package com.mana.transportesruta7app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


const val ARG_OBJECT = "object"
const val ARG_OBJECT_MOVIL = "object"

class SliderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_fragment_vale, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = Firebase.firestore
        val list : MutableList<String> = mutableListOf()
        db.collection("Vales").whereEqualTo("Email", "krlos173@hotmail.com").get().addOnSuccessListener { documents ->
            for (document in documents) {
                list.add(document.id)
            }
            /*Log.d("TAG", list.toString())*/
            arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
                val slider_text: TextView = view.findViewById(R.id.slider_id)
                slider_text.text = getInt(ARG_OBJECT).toString()

                // AGREGADO SEGUN POSICION DE ARRAY
                val slider_movil: TextView = view.findViewById(R.id.slider_movil)
                slider_movil.text = list[getInt(ARG_OBJECT)-1]
            }
        }


        /*arguments?.takeIf { it.containsKey(ARG_OBJECT_MOVIL) }?.apply {
            val slider_text_movil: TextView = view.findViewById(R.id.slider_movil)
            slider_text_movil.text = getInt(ARG_OBJECT_MOVIL).toString()
*//*            Log.d("TAG", getString(ARG_OBJECT).toString())*//*
        }*/


    }


}