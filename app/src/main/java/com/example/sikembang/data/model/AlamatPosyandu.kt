package com.example.sikembang.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class AlamatPosyandu(
    @DocumentId
    var id: String = "",

    var nama_posyandu: String = "",

    var alamat_lengkap: String = "",

    var kelurahan: String = "",
    var kecamatan: String = "",
    var kota: String = "",
    var provinsi: String = "",

    var kode_pos: String = "",

    var latitude: Double? = null,
    var longitude: Double? = null,

    var telepon: String = "",
    var email: String = "",
    var keterangan: String = "",

    var penanggung_jawab: String = "",

    var rating: Double = 0.0,

    var jumlah_ulasan: Int = 0,

    var status: String = "AKTIF",

    var jam_operasional: JamOperasional = JamOperasional(),

    var fasilitas_tersedia: List<String> = emptyList(),

    var kegiatan_terbaru: String = ""
) {
    data class JamOperasional(
        var senin: String = "Tutup",
        var selasa: String = "Tutup",
        var rabu: String = "Tutup",
        var kamis: String = "Tutup",
        var jumat: String = "Tutup",
        var sabtu: String = "Tutup",
        var minggu: String = "Tutup"
    ) {
        fun getJamHariIni(hari: String): String {
            return when(hari.lowercase()) {
                "senin", "monday" -> senin
                "selasa", "tuesday" -> selasa
                "rabu", "wednesday" -> rabu
                "kamis", "thursday" -> kamis
                "jumat", "friday" -> jumat
                "sabtu", "saturday" -> sabtu
                "minggu", "sunday" -> minggu
                else -> "Tutup"
            }
        }
    }

    // Helper Functions
    fun getAlamatLengkapFormat(): String = "$alamat_lengkap, $kelurahan, $kecamatan, $kota, $provinsi $kode_pos"

    fun getJarakFormat(userLat: Double, userLon: Double): String {
        val jarak = hitungJarak(userLat, userLon)
        return if (jarak == Double.MAX_VALUE) "Tidak tersedia" else String.format("%.1f km", jarak)
    }

    fun getEstimasiWaktu(userLat: Double, userLon: Double): String {
        val jarakKm = hitungJarak(userLat, userLon)
        if (jarakKm == Double.MAX_VALUE) return "-"
        val waktuJam = jarakKm / 40.0
        val waktuMenit = (waktuJam * 60).toInt()
        return "$waktuMenit min"
    }

    fun getGoogleMapsUrl(): String = "http://maps.google.com/maps?daddr=${latitude ?: 0.0},${longitude ?: 0.0}"

    fun getGoogleMapsViewUrl(): String = "geo:${latitude ?: 0.0},${longitude ?: 0.0}?q=${latitude ?: 0.0},${longitude ?: 0.0}($nama_posyandu)"

    private fun hitungJarak(userLat: Double, userLon: Double): Double {
        val lat = latitude
        val lon = longitude
        if (lat == null || lon == null) return Double.MAX_VALUE
        val earthRadius = 6371.0
        val dLat = Math.toRadians(lat - userLat)
        val dLon = Math.toRadians(lon - userLon)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(lat)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    fun getStatusBuka(): String = if (status == "AKTIF") "Buka" else "Tutup"
    fun getStatusColor(): Long = if (status == "AKTIF") 0xFF4CAF50 else 0xFFF44336
}