package com.example.sikembang.data.model

import java.text.SimpleDateFormat
import java.util.*

data class AlamatPosyandu(
    val id: String = "",
    val namaPosyandu: String = "",
    val alamatLengkap: String = "",
    val kelurahan: String = "",
    val kecamatan: String = "",
    val kota: String = "",
    val provinsi: String = "",
    val kodePos: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val telepon: String = "",
    val email: String = "",

    val jamOperasional: JamOperasional = JamOperasional(),
    val kegiatanTerbaru: String = "",
    val fasilitasTersedia: List<String> = emptyList(),
    val penanggungJawab: String = "",
    val kapasitas: Int = 0,
    val fotoPosyandu: String = "",
    val rating: Double = 0.0,
    val jumlahUlasan: Int = 0,
    val status: String = "AKTIF",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    data class JamOperasional(
        val senin: String = "08:00 - 12:00",
        val selasa: String = "08:00 - 12:00",
        val rabu: String = "08:00 - 12:00",
        val kamis: String = "08:00 - 12:00",
        val jumat: String = "08:00 - 12:00",
        val sabtu: String = "Tutup",
        val minggu: String = "Tutup"
    ) {
        fun getJamHariIni(): String {
            val calendar = Calendar.getInstance()
            return when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> senin
                Calendar.TUESDAY -> selasa
                Calendar.WEDNESDAY -> rabu
                Calendar.THURSDAY -> kamis
                Calendar.FRIDAY -> jumat
                Calendar.SATURDAY -> sabtu
                Calendar.SUNDAY -> minggu
                else -> "Tutup"
            }
        }

        fun isBukaHariIni(): Boolean {
            return getJamHariIni() != "Tutup"
        }
    }

    fun getAlamatLengkapFormat(): String {
        return "$alamatLengkap, $kelurahan, $kecamatan, $kota, $provinsi $kodePos"
    }

    fun getJarakDari(userLat: Double, userLon: Double): Double {
        val earthRadius = 6371.0 // radius bumi dalam km

        val dLat = Math.toRadians(latitude - userLat)
        val dLon = Math.toRadians(longitude - userLon)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

    fun getJarakFormatted(userLat: Double, userLon: Double): String {
        val jarak = getJarakDari(userLat, userLon)
        return when {
            jarak < 1.0 -> "${(jarak * 1000).toInt()} m"
            else -> String.format("%.1f km", jarak)
        }
    }

    fun getEstimasiWaktu(userLat: Double, userLon: Double): String {
        val jarak = getJarakDari(userLat, userLon)
        val waktuJam = jarak / 30.0 // asumsi 30 km/jam
        val waktuMenit = (waktuJam * 60).toInt()

        return when {
            waktuMenit < 60 -> "$waktuMenit menit"
            else -> {
                val jam = waktuMenit / 60
                val menit = waktuMenit % 60
                if (menit > 0) "$jam jam $menit menit" else "$jam jam"
            }
        }
    }

    fun getGoogleMapsUrl(): String {
        return "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"
    }


    fun getGoogleMapsViewUrl(): String {
        return "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
    }


    fun isSedangBuka(): Boolean {
        if (!jamOperasional.isBukaHariIni()) return false

        val jamHariIni = jamOperasional.getJamHariIni()
        if (jamHariIni == "Tutup") return false

        try {
            val parts = jamHariIni.split(" - ")
            if (parts.size != 2) return false

            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val jamBuka = sdf.parse(parts[0].trim()) ?: return false
            val jamTutup = sdf.parse(parts[1].trim()) ?: return false

            val sekarang = Calendar.getInstance()
            val jamSekarang = sdf.parse("${sekarang.get(Calendar.HOUR_OF_DAY)}:${sekarang.get(Calendar.MINUTE)}")

            return jamSekarang?.after(jamBuka) == true && jamSekarang.before(jamTutup)
        } catch (e: Exception) {
            return false
        }
    }

    fun getStatusBuka(): String {
        return when {
            status != "AKTIF" -> "Tidak Beroperasi"
            !jamOperasional.isBukaHariIni() -> "Tutup Hari Ini"
            isSedangBuka() -> "Buka Sekarang"
            else -> "Tutup Sekarang"
        }
    }

    fun getStatusColor(): Long {
        return when {
            status != "AKTIF" -> 0xFFFF5252 // Red
            !jamOperasional.isBukaHariIni() -> 0xFF757575 // Gray
            isSedangBuka() -> 0xFF4CAF50 // Green
            else -> 0xFFFFA726 // Orange
        }
    }
}