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


const val NUMERO_VALE    = "object"
const val MOVIL_OBJECT  = "object1"

class SliderFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.activity_fragment_vale, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf { it.containsKey(NUMERO_VALE) }?.apply {
            val slider_text : TextView   = view.findViewById(R.id.slider_id)
            val slider_movil: TextView  = view.findViewById(R.id.slider_movil)
            

            slider_text.text    = getString(NUMERO_VALE).toString()
            slider_movil.text   = getString(MOVIL_OBJECT).toString()
        }

    }
}