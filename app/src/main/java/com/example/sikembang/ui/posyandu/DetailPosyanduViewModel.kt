package com.example.sikembang.ui.posyandu

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper
// PERHATIKAN IMPORT DI BAWAH INI:
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DetailPosyanduViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    // Inisialisasi Firestore
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("posyandu_Sikembang")

    private val _selectedPosyandu = MutableStateFlow<AlamatPosyandu?>(null)
    val selectedPosyandu = _selectedPosyandu.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadPosyanduById(documentId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("DEBUG_POSYANDU", "Fetching ID: $documentId")

                val snapshot = collectionRef.document(documentId).get().await()

                if (snapshot.exists()) {
                    val data = snapshot.toObject(AlamatPosyandu::class.java)
                    val fixedData = data?.copy(id = snapshot.id)
                    _selectedPosyandu.value = fixedData
                } else {
                    Log.e("DEBUG_POSYANDU", "Data tidak ditemukan")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_POSYANDU", "Error: ${e.message}")
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
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