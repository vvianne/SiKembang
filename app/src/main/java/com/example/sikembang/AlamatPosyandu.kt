package com.example.sikembang.data.model

data class AlamatPosyandu(
    val id: String = "",
    val namaPosyandu: String = "",
    val alamatLengkap: String = "",
    val kelurahan: String = "",
    val kecamatan: String = "",
    val kota: String = "",
    val provinsi: String = "",
    val kodePos: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val telepon: String = "",
    val email: String = "",
    val keterangan: String = "",
    // --- Added missing fields required by UI ---
    val penanggungJawab: String = "",
    val rating: Double = 0.0,
    val jumlahUlasan: Int = 0,
    // -------------------------------------------
    val status: Status = Status.AKTIF,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val jamOperasional: JamOperasional = JamOperasional(),
    val fasilitasTersedia: List<String> = emptyList(),
    val kegiatanTerbaru: String = ""
) {
    data class JamOperasional(
        val senin: String = "Tutup",
        val selasa: String = "Tutup",
        val rabu: String = "Tutup",
        val kamis: String = "Tutup",
        val jumat: String = "Tutup",
        val sabtu: String = "Tutup",
        val minggu: String = "Tutup"
    ){
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

    enum class Status {
        AKTIF, NONAKTIF
    }

    fun getAlamatLengkapFormat(): String {
        return "$alamatLengkap, $kelurahan, $kecamatan, $kota, $provinsi $kodePos"
    }

    fun getJarakFormat(userLat: Double, userLon: Double): String {
        val jarak = hitungJarak(userLat, userLon)
        return if (jarak == Double.MAX_VALUE) "Tidak tersedia"
        else String.format("%.1f km", jarak)
    }

    // 1. Estimasi Waktu
    fun getEstimasiWaktu(userLat: Double, userLon: Double): String {
        val jarakKm = hitungJarak(userLat, userLon)
        if (jarakKm == Double.MAX_VALUE) return "-"
        // Assumption: Average speed 40 km/h
        val waktuJam = jarakKm / 40.0
        val waktuMenit = (waktuJam * 60).toInt()
        return "$waktuMenit min"
    }

    // 2. Maps URL
    fun getGoogleMapsUrl(): String {
        return "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
    }

    // 3. Maps View Intent URL
    fun getGoogleMapsViewUrl(): String {
        return "geo:$latitude,$longitude?q=$latitude,$longitude($namaPosyandu)"
    }

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

    fun getStatusBuka(): String {
        return if (status == Status.AKTIF) "Buka" else "Tutup"
    }

    fun getStatusColor(): Long {
        return if (status == Status.AKTIF) 0xFF4CAF50 else 0xFFF44336
    }
}