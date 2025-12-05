package com.example.sikembang.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sikembang.data.model.AlamatPosyandu
import com.example.sikembang.utils.LocationHelper
import android.location.Location
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailPosyanduViewModel(
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _selectedPosyandu = MutableStateFlow<AlamatPosyandu?>(null)
    val selectedPosyandu = _selectedPosyandu.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun loadPosyanduById(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000)

            val dataContoh = AlamatPosyandu(
                id = "1",
                namaPosyandu = "Posyandu Melati Indah",
                alamatLengkap = "Jl. Ahmad Yani No. 45, RT 02/RW 05",
                kelurahan = "Cipadung",
                kecamatan = "Cibiru",
                kota = "Bandung",
                provinsi = "Jawa Barat",
                kodePos = "40615",
                telepon = "0812-3456-7890",
                email = "posyandu.melati@example.com",
                penanggungJawab = "Bidan Siti Aminah",
                rating = 4.8,
                jumlahUlasan = 120,
                jamOperasional = AlamatPosyandu.JamOperasional(
                    senin = "08:00 - 12:00",
                    selasa = "08:00 - 12:00",
                    rabu = "08:00 - 14:00 (Imunisasi)",
                    kamis = "08:00 - 12:00",
                    jumat = "Tutup",
                    sabtu = "09:00 - 11:00",
                    minggu = "Tutup"
                ),
                fasilitasTersedia = listOf(
                    "Penimbangan Balita",
                    "Imunisasi Dasar",
                    "Pemeriksaan Ibu Hamil",
                    "Konsultasi Gizi",
                    "Area Bermain Anak"
                ),
                kegiatanTerbaru = "Penyuluhan Vitamin A (Bulan Depan)",
                latitude = -6.914744, // Contoh koordinat (Bandung)
                longitude = 107.609810
            )

            _selectedPosyandu.value = dataContoh
            _isLoading.value = false
        }
    }

    fun getCurrentLocation() {
        // Panggil fungsi locationHelper jika ada
        // locationHelper.getLastLocation { ... }

        // Simulasi lokasi user agar fitur jarak jalan
        val dummyLocation = Location("dummy").apply {
            latitude = -6.920000
            longitude = 107.610000
        }
        _userLocation.value = dummyLocation
    }
}