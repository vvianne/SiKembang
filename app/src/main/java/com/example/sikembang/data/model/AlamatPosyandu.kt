package com.example.sikembang.data.model

<<<<<<< HEAD
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName

// HAPUS SEMUA import kotlinx.serialization...

@IgnoreExtraProperties
data class AlamatPosyandu(
    @DocumentId // Ini otomatis diisi ID oleh Firestore
    val id: String = "", // UBAH JADI STRING (Wajib!)

    @PropertyName("nama_posyandu")
    val namaPosyandu: String = "",

    @PropertyName("alamat_lengkap")
=======
import java.text.SimpleDateFormat
import java.util.*

data class AlamatPosyandu(
    val id: String = "",
    val namaPosyandu: String = "",
>>>>>>> 65cd3496fad4a1263fda411b766c9e950f185f69
    val alamatLengkap: String = "",
    val kelurahan: String = "",
    val kecamatan: String = "",
    val kota: String = "",
    val provinsi: String = "",
<<<<<<< HEAD

    @PropertyName("kode_pos")
=======
>>>>>>> 65cd3496fad4a1263fda411b766c9e950f185f69
    val kodePos: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val telepon: String = "",
    val email: String = "",

<<<<<<< HEAD
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
    // Class ini harus punya empty constructor (default value sudah cukup)
=======
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
>>>>>>> 65cd3496fad4a1263fda411b766c9e950f185f69
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

    // --- Helper Functions (JANGAN DIHAPUS) ---
    fun getAlamatLengkapFormat(): String = "$alamatLengkap, $kelurahan, $kecamatan, $kota, $provinsi $kodePos"

<<<<<<< HEAD
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

    fun getGoogleMapsUrl(): String = "http://maps.google.com/maps?daddr=$latitude,$longitude"

    fun getGoogleMapsViewUrl(): String = "geo:$latitude,$longitude?q=$latitude,$longitude($namaPosyandu)"

    private fun hitungJarak(userLat: Double, userLon: Double): Double {
        if (latitude == null || longitude == null) return Double.MAX_VALUE
        val earthRadius = 6371.0
=======
    fun getJarakDari(userLat: Double, userLon: Double): Double {
        val earthRadius = 6371.0 // radius bumi dalam km

>>>>>>> 65cd3496fad4a1263fda411b766c9e950f185f69
        val dLat = Math.toRadians(latitude - userLat)
        val dLon = Math.toRadians(longitude - userLon)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(latitude)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }

<<<<<<< HEAD
    fun getStatusBuka(): String = if (status == "AKTIF") "Buka" else "Tutup"
    fun getStatusColor(): Long = if (status == "AKTIF") 0xFF4CAF50 else 0xFFF44336
=======
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
>>>>>>> 65cd3496fad4a1263fda411b766c9e950f185f69
}