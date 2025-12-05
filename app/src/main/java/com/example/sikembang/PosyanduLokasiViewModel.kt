package com.example.sikembang.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sikembang.utils.LocationHelper
import com.example.sikembang.data.model.AlamatPosyandu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PosyanduLokasiViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _selectedPosyandu = MutableStateFlow<AlamatPosyandu?>(null)
    val selectedPosyandu: StateFlow<AlamatPosyandu?> = _selectedPosyandu

    private val _userLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val userLocation: StateFlow<Pair<Double, Double>?> = _userLocation

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadPosyanduById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // 🫣 fake load, nanti diganti API / DB
            _selectedPosyandu.value = AlamatPosyandu(
                id = id,
                namaPosyandu = "Posyandu Ceria",
                kecamatan = "Kelurahan Kocak"
            )
            _isLoading.value = false
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            val loc = locationHelper.getCurrentLocation()
            _userLocation.value = loc
        }
    }
}
