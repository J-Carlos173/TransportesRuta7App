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
import kotlinx.android.synthetic.main.activity_fragment_vale.*


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
        val id_list : MutableList<String> = mutableListOf()
        val data_list : MutableList<String> = mutableListOf()
        db.collection("Vales").whereEqualTo("Email", "krlos173@hotmail.com").get().addOnSuccessListener { documents ->
            for (document in documents) {
                id_list.add(document.id)
                data_list.add(document.data.toString())
            }

            arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
                val slider_id: TextView = view.findViewById(R.id.slider_id)
                slider_id.text          = getInt(ARG_OBJECT).toString()

                // CREAR ARRAY
                var cadena          = data_list[getInt(ARG_OBJECT)-1]
                cadena              = cadena.replace("}", "")
                cadena              = cadena.replace("{", "")
                var cadenaSeparada  = cadena.split(",","=");

                // AGREGADO SEGUN POSICION DE ARRAY
                textViewValeID.text         = id_list[getInt(ARG_OBJECT)-1]
                textViewMovil.text          = cadenaSeparada[9]
                textViewCliente.text        = cadenaSeparada[25]
                textViewHoraInicio.text     = cadenaSeparada[1]
                textViewHoraFin.text        = cadenaSeparada[17]
                textViewOrigen.text         = cadenaSeparada[7]
                textViewDestino.text        = cadenaSeparada[7]
                textViewEmpesa.text         = cadenaSeparada[5]
            }
        }


        /*arguments?.takeIf { it.containsKey(ARG_OBJECT_MOVIL) }?.apply {
            val slider_text_movil: TextView = view.findViewById(R.id.slider_movil)
            slider_text_movil.text = getInt(ARG_OBJECT_MOVIL).toString()
            Log.d("TAG", getString(ARG_OBJECT).toString())
        }*/


    }


}