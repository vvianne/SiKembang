package com.example.sikembang.data.repository

import com.example.sikembang.data.model.AlamatPosyandu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PosyanduRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("posyandu_lokasi")

    fun getAllPosyandu(): Flow<List<AlamatPosyandu>> = callbackFlow {
        val listener = collection
            .whereEqualTo("status", "AKTIF")
            .orderBy("namaPosyandu", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val posyanduList = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AlamatPosyandu::class.java)
                } ?: emptyList()

                trySend(posyanduList)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getPosyanduById(id: String): Result<AlamatPosyandu?> {
        return try {
            val doc = collection.document(id).get().await()
            val posyandu = doc.toObject(AlamatPosyandu::class.java)
            Result.success(posyandu)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPosyanduTerdekat(
        userLat: Double,
        userLon: Double,
        limit: Int = 10
    ): Result<List<AlamatPosyandu>> {
        return try {
            val snapshot = collection
                .whereEqualTo("status", "AKTIF")
                .get()
                .await()

            val posyanduList = snapshot.documents
                .mapNotNull { it.toObject(AlamatPosyandu::class.java) }
                .sortedBy { it.getJarakDari(userLat, userLon) }
                .take(limit)

            Result.success(posyanduList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun searchPosyandu(
        query: String,
        userLat: Double? = null,
        userLon: Double? = null
    ): Result<List<AlamatPosyandu>> {
        return try {
            val snapshot = collection
                .whereEqualTo("status", "AKTIF")
                .get()
                .await()

            var posyanduList = snapshot.documents
                .mapNotNull { it.toObject(AlamatPosyandu::class.java) }
                .filter { posyandu ->
                    posyandu.namaPosyandu.contains(query, ignoreCase = true) ||
                            posyandu.alamatLengkap.contains(query, ignoreCase = true) ||
                            posyandu.kelurahan.contains(query, ignoreCase = true) ||
                            posyandu.kecamatan.contains(query, ignoreCase = true)
                }

            if (userLat != null && userLon != null) {
                posyanduList = posyanduList.sortedBy { it.getJarakDari(userLat, userLon) }
            }

            Result.success(posyanduList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getPosyanduByKecamatan(
        kecamatan: String,
        userLat: Double? = null,
        userLon: Double? = null
    ): Result<List<AlamatPosyandu>> {
        return try {
            val snapshot = collection
                .whereEqualTo("kecamatan", kecamatan)
                .whereEqualTo("status", "AKTIF")
                .get()
                .await()

            var posyanduList = snapshot.documents
                .mapNotNull { it.toObject(AlamatPosyandu::class.java) }

            if (userLat != null && userLon != null) {
                posyanduList = posyanduList.sortedBy { it.getJarakDari(userLat, userLon) }
            }

            Result.success(posyanduList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPosyanduBuka(
        userLat: Double? = null,
        userLon: Double? = null
    ): Result<List<AlamatPosyandu>> {
        return try {
            val snapshot = collection
                .whereEqualTo("status", "AKTIF")
                .get()
                .await()

            var posyanduList = snapshot.documents
                .mapNotNull { it.toObject(AlamatPosyandu::class.java) }
                .filter { it.isSedangBuka() }

            if (userLat != null && userLon != null) {
                posyanduList = posyanduList.sortedBy { it.getJarakDari(userLat, userLon) }
            }

            Result.success(posyanduList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDaftarKecamatan(): Result<List<String>> {
        return try {
            val snapshot = collection
                .whereEqualTo("status", "AKTIF")
                .get()
                .await()

            val kecamatanList = snapshot.documents
                .mapNotNull { it.toObject(AlamatPosyandu::class.java) }
                .map { it.kecamatan }
                .distinct()
                .sorted()

            Result.success(kecamatanList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun tambahPosyandu(posyandu: AlamatPosyandu): Result<String> {
        return try {
            val docRef = collection.document()
            val newPosyandu = posyandu.copy(id = docRef.id)
            docRef.set(newPosyandu).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updatePosyandu(posyandu: AlamatPosyandu): Result<Unit> {
        return try {
            val updatedPosyandu = posyandu.copy(updatedAt = System.currentTimeMillis())
            collection.document(posyandu.id).set(updatedPosyandu).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deletePosyandu(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}