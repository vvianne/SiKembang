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
    val status: Status = Status.AKTIF,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    enum class Status {
        AKTIF, NONAKTIF
    }

    fun getAlamatLengkapFormat(): String {
        return "$alamatLengkap, $kelurahan, $kecamatan, $kota, $provinsi $kodePos"
    }

    fun getJarak(userLat: Double, userLon: Double): Double {
        if (latitude == null || longitude == null) return Double.MAX_VALUE

        val earthRadius = 6371.0 // dalam km
        val dLat = Math.toRadians(latitude - userLat)
        val dLon = Math.toRadians(longitude - userLon)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}