package com.example.sikembang.data.repository

import android.util.Log
import com.example.sikembang.data.model.AlamatPosyandu
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PosyanduRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("posyandu_Sikembang") // Pastikan nama SAMA

    suspend fun getAllPosyandu(): List<AlamatPosyandu> {
        return try {
            val snapshot = collection.get().await()

            val listData = snapshot.documents.mapNotNull { doc ->
                val posyandu = doc.toObject(AlamatPosyandu::class.java)

                // --- TAMBAHKAN LOG INI ---
                Log.d("DEBUG_REPO", "ID: ${doc.id}")
                Log.d("DEBUG_REPO", "Raw Data Firestore: ${doc.data}") // Lihat nama field asli di sini
                Log.d("DEBUG_REPO", "Hasil Mapping: $posyandu") // Lihat apakah properti terisi atau masih default ("")
                // -------------------------

                posyandu?.copy(id = doc.id)
            }

            Log.d("REPO_FIREBASE", "Repository fetch success: ${listData.size} items")
            listData
        } catch (e: Exception) {
            Log.e("REPO_FIREBASE", "Error fetch: ${e.message}")
            emptyList()
        }
    }
}