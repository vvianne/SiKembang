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

            // Kita map manual agar ID dokumen pasti masuk
            val listData = snapshot.documents.mapNotNull { doc ->
                val posyandu = doc.toObject(AlamatPosyandu::class.java)
                // Copy object dan isi ID-nya dari ID dokumen Firestore
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