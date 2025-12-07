package com.example.sikembang.ui.posyandu

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.data.repository.PosyanduRepository
import com.example.sikembang.utils.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PetaPosyanduViewModel(
    private val locationHelper: LocationHelper,
    private val repository: PosyanduRepository
) : ViewModel() {

    private val _listPosyandu = MutableStateFlow<List<AlamatPosyandu>>(emptyList())
    val listPosyandu = _listPosyandu.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadAllPosyandu()
        getCurrentLocation()
    }

    fun loadAllPosyandu() {
        viewModelScope.launch {
            _isLoading.value = true
            val data = repository.getAllPosyandu()
            _listPosyandu.value = data
            _isLoading.value = false
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val coords = locationHelper.getCurrentLocation()
                if (coords != null) {
                    _userLocation.value = Location("gps").apply {
                        latitude = coords.first
                        longitude = coords.second
                    }
                }
            } catch (e: Exception) {
                // Dummy location fallback
                _userLocation.value = Location("dummy").apply {
                    latitude = -7.98
                    longitude = 112.63
                }
            }
        }
    }
}

class PetaPosyanduViewModelFactory(
    private val locationHelper: LocationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PetaPosyanduViewModel::class.java)) {
            val repository = PosyanduRepository()
            @Suppress("UNCHECKED_CAST")
            return PetaPosyanduViewModel(locationHelper, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}