package com.example.sikembang.ui.posyandu

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sikembang.data.remote.SupabaseClient
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PetaViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _listPosyandu = MutableStateFlow<List<AlamatPosyandu>>(emptyList())
    val listPosyandu = _listPosyandu.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun getAllPosyandu() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val hasil = withContext(Dispatchers.IO) {
                    SupabaseClient.client
                        .from("posyandu")
                        .select()
                        .decodeList<AlamatPosyandu>()
                }
                _listPosyandu.value = hasil
            } catch (e: Exception) {
                Log.e("PetaViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            val coords = locationHelper.getCurrentLocation()
            if (coords != null) {
                _userLocation.value = Location("gps").apply {
                    latitude = coords.first
                    longitude = coords.second
                }
            } else {
                _userLocation.value = Location("dummy").apply {
                    latitude = -7.9826
                    longitude = 112.6308
                }
            }
        }
    }
}

class PetaViewModelFactory(private val locationHelper: LocationHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Cek kesesuaian class
        if (modelClass.isAssignableFrom(PetaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PetaViewModel(locationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}