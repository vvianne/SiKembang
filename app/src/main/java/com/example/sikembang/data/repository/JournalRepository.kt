package com.example.sikembang.data.repository

import android.content.Context
import android.net.Uri
import com.example.sikembang.data.remote.SupabaseClient
import com.example.sikembang.data.model.JournalEntry
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinInstant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

class JournalRepository(private val context: Context) {

    private val client = SupabaseClient.client

    // 1. Upload Photo ke Supabase Storage
    suspend fun uploadPhoto(imageUri: Uri): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val fileName = "journal_photos/${UUID.randomUUID()}.jpg"
                val bucket = client.storage.from("foto_jurnal")

                // Baca file dari Uri
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes() ?: throw Exception("Gagal membaca gambar")
                inputStream.close()

                // Upload
                bucket.upload(fileName, bytes)

                // Ambil URL Publik
                val publicUrl = bucket.publicUrl(fileName)
                Result.success(publicUrl)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 2. Save Journal
    suspend fun saveJournal(
        date: LocalDate,
        deskripsi: String,
        photoUri: Uri
    ): Result<String> {
        return try {
            // A. Upload Foto
            val uploadResult = uploadPhoto(photoUri)
            if (uploadResult.isFailure) return Result.failure(uploadResult.exceptionOrNull()!!)
            val photoUrl = uploadResult.getOrNull()!!

            // B. Data
            val instantTanggal = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toKotlinInstant()
            val instantCreated = Clock.System.now()

            val newEntry = JournalEntry(
                tanggal = instantTanggal,
                tanggalString = date.toString(), // format "YYYY-MM-DD"
                deskripsi = deskripsi,
                fotoURL = photoUrl,
                createdAt = instantCreated
            )

            // C. Insert ke Database
            withContext(Dispatchers.IO) {
                client.from("jurnal_sikembang").insert(newEntry)
            }

            Result.success("Berhasil disimpan")
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // 3. Get Journals By Date
    suspend fun getJournalsByDate(date: LocalDate): Result<List<JournalEntry>> {
        return try {
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toKotlinInstant()
            val endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toKotlinInstant()

            val result = withContext(Dispatchers.IO) {
                client.from("jurnal_sikembang")
                    .select {
                        filter {
                            gte("tanggal", startOfDay)
                            lt("tanggal", endOfDay)
                        }
                        // Order by tanggal descending
                        order("tanggal", Order.DESCENDING)
                    }
                    .decodeList<JournalEntry>()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 4. Get All Journals
    suspend fun getAllJournals(): Result<List<JournalEntry>> {
        return try {
            val result = withContext(Dispatchers.IO) {
                client.from("jurnal_sikembang")
                    .select {
                        order("cretedAt", Order.DESCENDING)
                    }
                    .decodeList<JournalEntry>()
            }
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 5. Delete Journal
    suspend fun deleteJournal(journalId: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                client.from("jurnal_sikembang").delete {
                    filter {
                        eq("id", journalId)
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 6. Update Journal
    suspend fun updateJournal(journalId: String, deskripsiBaru: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                client.from("jurnal_sikembang").update(
                    {
                        set("deskripsi", deskripsiBaru)
                    }
                ) {
                    filter {
                        eq("id", journalId)
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}