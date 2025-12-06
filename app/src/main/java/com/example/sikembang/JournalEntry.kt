package com.example.sikembang

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class JournalEntry(
    val id: String? = null, // ID di-generate otomatis oleh Supabase (UUID)

    @SerialName("tanggal")
    val tanggal: Instant, // Pengganti Timestamp

    @SerialName("tanggalString")
    val tanggalString: String,

    @SerialName("deskripsi")
    val deskripsi: String,

    @SerialName("fotoURL")
    val fotoURL: String,

    @SerialName("cretedAt") // Sesuai typo di screenshot kamu
    val createdAt: Instant
)