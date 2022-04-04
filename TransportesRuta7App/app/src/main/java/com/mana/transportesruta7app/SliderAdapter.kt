package com.mana.transportesruta7app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SliderAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 0



    override fun createFragment(position: Int): Fragment {
        val fragment = SliderFragment()
        val db = Firebase.firestore
        val list : MutableList<String> = mutableListOf()



        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT,position+1)
/*            Log.d("TAG", "*****************************"+position)*/
        }

    /*    fragment.arguments = Bundle().apply {
            putString(ARG_OBJECT_MOVIL,"hola")
        }*/





        return fragment
    }
}