package com.example.sikembang.ui.posyandu

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.data.repository.PosyanduRepository
import com.example.sikembang.utils.LocationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PetaViewModel(
    private val repository: PosyanduRepository = PosyanduRepository(),
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _posyanduList = MutableStateFlow<List<AlamatPosyandu>>(emptyList())
    val posyanduList: StateFlow<List<AlamatPosyandu>> = _posyanduList.asStateFlow()

    private val _posyanduTerdekat = MutableStateFlow<List<AlamatPosyandu>>(emptyList())
    val posyanduTerdekat: StateFlow<List<AlamatPosyandu>> = _posyanduTerdekat.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingLocation = MutableStateFlow(false)
    val isLoadingLocation: StateFlow<Boolean> = _isLoadingLocation.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filterKecamatan = MutableStateFlow<String?>(null)
    val filterKecamatan: StateFlow<String?> = _filterKecamatan.asStateFlow()

    private val _showOnlyBuka = MutableStateFlow(false)
    val showOnlyBuka: StateFlow<Boolean> = _showOnlyBuka.asStateFlow()

    private val _daftarKecamatan = MutableStateFlow<List<String>>(emptyList())
    val daftarKecamatan: StateFlow<List<String>> = _daftarKecamatan.asStateFlow()

    private val _hasLocationPermission = MutableStateFlow(false)
    val hasLocationPermission: StateFlow<Boolean> = _hasLocationPermission.asStateFlow()

    private val _isLocationEnabled = MutableStateFlow(false)
    val isLocationEnabled: StateFlow<Boolean> = _isLocationEnabled.asStateFlow()

    init {
        checkLocationPermission()
        checkLocationEnabled()
        loadDaftarKecamatan()
        loadAllPosyandu()
    }

    fun checkLocationPermission() {
        _hasLocationPermission.value = locationHelper.hasLocationPermission()
    }

    fun checkLocationEnabled() {
        _isLocationEnabled.value = locationHelper.isLocationEnabled()
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            _isLoadingLocation.value = true
            val result = locationHelper.getCurrentLocation()
            result.onSuccess { location ->
                _userLocation.value = location
                _isLoadingLocation.value = false
                loadPosyanduTerdekat()
            }.onFailure { e ->
                _errorMessage.value = when {
                    e is SecurityException -> "Izin lokasi belum diberikan"
                    e.message?.contains("disabled") == true -> "GPS tidak aktif. Mohon aktifkan GPS"
                    else -> "Gagal mendapatkan lokasi: ${e.message}"
                }
                _isLoadingLocation.value = false
            }
        }
    }

    fun startLocationUpdates() {
        viewModelScope.launch {
            locationHelper.getLocationUpdates(10000)
                .catch { e ->
                    _errorMessage.value = "Error location updates: ${e.message}"
                }
                .collect { location ->
                    _userLocation.value = location
                    // Auto refresh posyandu terdekat setiap ada update lokasi
                    loadPosyanduTerdekat()
                }
        }
    }

    fun loadAllPosyandu() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getAllPosyandu()
            result.onSuccess { list ->
                _posyanduList.value = list
                _isLoading.value = false
            }.onFailure { e ->
                _errorMessage.value = "Gagal memuat data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadPosyanduTerdekat(limit: Int = 10) {
        val location = _userLocation.value
        if (location == null) {
            _errorMessage.value = "Lokasi tidak tersedia"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPosyanduTerdekat(
                location.latitude,
                location.longitude,
                limit
            )
            result.onSuccess { list ->
                _posyanduTerdekat.value = list
                _isLoading.value = false
            }.onFailure { e ->
                _errorMessage.value = "Gagal memuat posyandu terdekat: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun loadDaftarKecamatan() {
        viewModelScope.launch {
            val result = repository.getDaftarKecamatan()
            result.onSuccess { list ->
                _daftarKecamatan.value = list
            }
        }
    }


    fun searchPosyandu(query: String) {
        _searchQuery.value = query

        if (query.isEmpty()) {
            loadAllPosyandu()
            return
        }

        val location = _userLocation.value
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.searchPosyandu(
                query,
                location?.latitude,
                location?.longitude
            )
            result.onSuccess { list ->
                _posyanduList.value = list
                _isLoading.value = false
            }.onFailure { e ->
                _errorMessage.value = "Gagal mencari data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun filterByKecamatan(kecamatan: String?) {
        _filterKecamatan.value = kecamatan

        if (kecamatan == null) {
            loadAllPosyandu()
            return
        }

        val location = _userLocation.value
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getPosyanduByKecamatan(
                kecamatan,
                location?.latitude,
                location?.longitude
            )
            result.onSuccess { list ->
                _posyanduList.value = list
                _isLoading.value = false
            }.onFailure { e ->
                _errorMessage.value = "Gagal filter data: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun toggleShowOnlyBuka() {
        _showOnlyBuka.value = !_showOnlyBuka.value

        if (_showOnlyBuka.value) {
            val location = _userLocation.value
            viewModelScope.launch {
                _isLoading.value = true
                val result = repository.getPosyanduBuka(
                    location?.latitude,
                    location?.longitude
                )
                result.onSuccess { list ->
                    _posyanduList.value = list
                    _isLoading.value = false
                }.onFailure { e ->
                    _errorMessage.value = "Gagal memuat data: ${e.message}"
                    _isLoading.value = false
                }
            }
        } else {
            loadAllPosyandu()
        }
    }


    fun sortByDistance() {
        val location = _userLocation.value ?: return
        _posyanduList.value = _posyanduList.value.sortedBy {
            it.getJarakDari(location.latitude, location.longitude)
        }
    }

    fun sortByName() {
        _posyanduList.value = _posyanduList.value.sortedBy { it.namaPosyandu }
    }

    fun sortByRating() {
        _posyanduList.value = _posyanduList.value.sortedByDescending { it.rating }
    }


    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun getJarakFormatted(posyandu: AlamatPosyandu): String {
        val location = _userLocation.value ?: return "Lokasi tidak tersedia"
        return posyandu.getJarakFormatted(location.latitude, location.longitude)
    }

    fun getEstimasiWaktu(posyandu: AlamatPosyandu): String {
        val location = _userLocation.value ?: return "Lokasi tidak tersedia"
        return posyandu.getEstimasiWaktu(location.latitude, location.longitude)
    }
}