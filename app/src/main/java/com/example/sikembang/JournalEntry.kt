package com.example.sikembang

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

data class JournalEntry(
    val id: String = "",
    val date: Timestamp = Timestamp.now(),
    val dateString: String = "", // Format: "2025-12-10"
    val deskripsi: String = "",
    val photoUrl: String = "",
    val cretedAt: Timestamp = Timestamp.now()  // ✅ Ubah jadi "cretedAt"
) {
    // Konversi Timestamp ke LocalDate
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalDate(): LocalDate? {
        return try {
            date.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        } catch (e: Exception) {
            null
        }
    }

    // Untuk Firestore (SESUAIKAN dengan field name di Repository)
    fun toMap(): Map<String, Any> {
        return hashMapOf(
            "tanggal" to date,
            "tanggalString" to dateString,
            "deskripsi" to deskripsi,
            "fotoURL" to photoUrl,
            "cretedAt" to cretedAt           // ✅ Ubah "createdAt" → "cretedAt"
        )
    }
}