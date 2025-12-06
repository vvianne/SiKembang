package com.example.sikembang.ui.posyandu

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
// Pastikan import SupabaseClient benar (sesuaikan package jika perlu)
import com.example.sikembang.data.remote.SupabaseClient
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailPosyanduViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _selectedPosyandu = MutableStateFlow<AlamatPosyandu?>(null)
    val selectedPosyandu = _selectedPosyandu.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 1. Ambil Data dari Supabase
    fun loadPosyanduById(idString: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ubah String ID dari navigasi menjadi Long
                val idTarget = idString.toLongOrNull() ?: 0L

                Log.d("DEBUG_POSYANDU", "Mencari ID: $idTarget")

                val posyandu = withContext(Dispatchers.IO) {
                    SupabaseClient.client
                        .from("posyandu")
                        .select {
                            filter {
                                eq("id", idTarget)
                            }
                        }
                        .decodeSingleOrNull<AlamatPosyandu>()
                }

                if (posyandu != null) {
                    _selectedPosyandu.value = posyandu
                } else {
                    Log.e("DEBUG_POSYANDU", "Data ID $idTarget tidak ditemukan di Supabase")
                }

            } catch (e: Exception) {
                Log.e("DEBUG_POSYANDU", "Error: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 2. Ambil Lokasi
    fun getCurrentLocation() {
        viewModelScope.launch {
            try {
                val coordinatePair = locationHelper.getCurrentLocation()

                if (coordinatePair != null) {
                    val locationObj = Location("gps").apply {
                        latitude = coordinatePair.first
                        longitude = coordinatePair.second
                    }
                    _userLocation.value = locationObj
                }
            } catch (e: Exception) {
                _userLocation.value = Location("dummy").apply {
                    latitude = -7.9826
                    longitude = 112.6308
                }
            }
        }
    }
}

// --- INI BAGIAN PENTING YANG TADI HILANG ---
// Factory ini KHUSUS untuk DetailPosyanduViewModel
class DetailPosyanduViewModelFactory(
    private val locationHelper: LocationHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailPosyanduViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailPosyanduViewModel(locationHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}