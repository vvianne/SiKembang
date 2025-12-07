package com.example.sikembang.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

@IgnoreExtraProperties
data class AlamatPosyandu(
    @DocumentId
    val id: String = "",

    @PropertyName("nama_posyandu")
    val namaPosyandu: String = "",

    @PropertyName("alamat_lengkap")
    val alamatLengkap: String = "",

    val kelurahan: String = "",
    val kecamatan: String = "",
    val kota: String = "",
    val provinsi: String = "",

    @PropertyName("kode_pos")
    val kodePos: String = "",

    val latitude: Double? = null,
    val longitude: Double? = null,
    val telepon: String = "",
    val email: String = "",
    val keterangan: String = "",

    @PropertyName("penanggung_jawab")
    val penanggungJawab: String = "",

    val rating: Double = 0.0,

    @PropertyName("jumlah_ulasan")
    val jumlahUlasan: Int = 0,

    val status: String = "AKTIF",

    @PropertyName("jam_operasional")
    val jamOperasional: JamOperasional = JamOperasional(),

    @PropertyName("fasilitas_tersedia")
    val fasilitasTersedia: List<String> = emptyList(),

    @PropertyName("kegiatan_terbaru")
    val kegiatanTerbaru: String = ""
) {
    // Nested Class juga harus dibenerin kalau ada property name
    data class JamOperasional(
        val senin: String = "Tutup",
        val selasa: String = "Tutup",
        val rabu: String = "Tutup",
        val kamis: String = "Tutup",
        val jumat: String = "Tutup",
        val sabtu: String = "Tutup",
        val minggu: String = "Tutup"
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

    // Fungsi helper biarin aja
    fun getAlamatLengkapFormat(): String = "$alamatLengkap, $kelurahan, $kecamatan, $kota, $provinsi $kodePos"

    fun getJarakFormat(userLat: Double, userLon: Double): String {
        val jarak = hitungJarak(userLat, userLon)
        return if (jarak == Double.MAX_VALUE) "Tidak tersedia" else String.format("%.1f km", jarak)
    }

    // ... sisa fungsi helper copy aja yang lama ...
    private fun hitungJarak(userLat: Double, userLon: Double): Double {
        if (latitude == null || longitude == null) return Double.MAX_VALUE
        val earthRadius = 6371.0
        val dLat = Math.toRadians(latitude - userLat)
        val dLon = Math.toRadians(longitude - userLon)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }

    fun getStatusBuka(): String = if (status == "AKTIF") "Buka" else "Tutup"
    fun getStatusColor(): Long = if (status == "AKTIF") 0xFF4CAF50 else 0xFFF44336
    fun getGoogleMapsUrl(): String = "http://maps.google.com/maps?daddr=$latitude,$longitude"
    fun getGoogleMapsViewUrl(): String = "geo:$latitude,$longitude?q=$latitude,$longitude($namaPosyandu)"
    fun getEstimasiWaktu(userLat: Double, userLon: Double): String {
        val jarakKm = hitungJarak(userLat, userLon)
        if (jarakKm == Double.MAX_VALUE) return "-"
        val waktuJam = jarakKm / 40.0
        val waktuMenit = (waktuJam * 60).toInt()
        return "$waktuMenit min"
    }
}