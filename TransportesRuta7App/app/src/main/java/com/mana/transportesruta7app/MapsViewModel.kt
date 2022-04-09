package com.mana.transportesruta7app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.maps.model.DirectionsResult
import com.google.maps.model.LatLng
import kotlinx.coroutines.launch

class MapsViewModel(application: Application) : AndroidViewModel(application) {
    private val _directionsResult = MutableLiveData<DirectionsResult?>()
    val directionsResult: LiveData<DirectionsResult?> = _directionsResult
    fun execute(origin: LatLng, destination: LatLng) {
        viewModelScope.launch {
            val result = DirectionsApiHelper().execute(getApplication(), origin, destination)
            _directionsResult.value = result
        }
    }
}