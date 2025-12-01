package com.example.sikembang

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.UUID

class JournalRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val journalsCollection = firestore.collection("jurnal_Sikembang")

    // -------------------------
    // UPLOAD PHOTO
    // -------------------------
    suspend fun uploadPhoto(imageUri: Uri): Result<String> {
        return try {
            val fileName = "journal_photos/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // SAVE JOURNAL
    // -------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun saveJournal(
        date: LocalDate,
        deskripsi: String,
        photoUri: Uri
    ): Result<String> {
        return try {
            // 1. Upload photo
            val uploaded = uploadPhoto(photoUri)
            if (uploaded.isFailure) return Result.failure(uploaded.exceptionOrNull()!!)
            val photoUrl = uploaded.getOrNull()!!

            // 2. Convert LocalDate → Timestamp
            val timestamp = Timestamp(
                Date.from(
                    date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )

            // 3. Create journal entry (DISAMAKAN DENGAN FIRESTORE)
            val journalEntry = mapOf(
                "tanggal" to timestamp,
                "tanggalString" to date.toString(),
                "deskripsi" to deskripsi,
                "fotoURL" to photoUrl,
                "cretedAt" to Timestamp.now()  // sesuai Firestore kamu
            )

            // 4. Save to Firestore
            val docRef = journalsCollection.add(journalEntry).await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // GET JOURNAL BY DATE
    // -------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getJournalsByDate(date: LocalDate): Result<List<JournalEntry>> {
        return try {
            val startOfDay = Timestamp(
                Date.from(
                    date.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
            val endOfDay = Timestamp(
                Date.from(
                    date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )

            // QUERY DISAMAKAN: pakai field "tanggal"
            val snapshot = journalsCollection
                .whereGreaterThanOrEqualTo("tanggal", startOfDay)
                .whereLessThan("tanggal", endOfDay)
                .orderBy("tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            val list = snapshot.documents.map { doc ->
                JournalEntry(
                    id = doc.id,
                    date = doc.getTimestamp("tanggal") ?: Timestamp.now(),
                    dateString = doc.getString("tanggalString") ?: "",
                    deskripsi = doc.getString("deskripsi") ?: "",
                    photoUrl = doc.getString("fotoURL") ?: "",
                    cretedAt = doc.getTimestamp("cretedAt") ?: Timestamp.now()
                )
            }

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // GET ALL JOURNALS
    // -------------------------
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getAllJournals(): Result<List<JournalEntry>> {
        return try {
            // ORDER BY sesuai Firestore: "cretedAt"
            val snapshot = journalsCollection
                .orderBy("cretedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val list = snapshot.documents.map { doc ->
                JournalEntry(
                    id = doc.id,
                    date = doc.getTimestamp("tanggal") ?: Timestamp.now(),
                    dateString = doc.getString("tanggalString") ?: "",
                    deskripsi = doc.getString("deskripsi") ?: "",
                    photoUrl = doc.getString("fotoURL") ?: "",
                    cretedAt = doc.getTimestamp("cretedAt") ?: Timestamp.now()  // ✅ Pakai "cretedAt"
                )
            }

            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // DELETE JOURNAL
    // -------------------------
    suspend fun deleteJournal(journalId: String): Result<Unit> {
        return try {
            journalsCollection.document(journalId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------
    // UPDATE JOURNAL
    // -------------------------
    suspend fun updateJournal(
        journalId: String,
        deskripsi: String
    ): Result<Unit> {
        return try {
            val updates = hashMapOf<String, Any>(
                "deskripsi" to deskripsi
            )

            journalsCollection.document(journalId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}