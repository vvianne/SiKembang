package com.example.sikembang.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: String? = null, // ID di-generate otomatis oleh Supabase (UUID)

    @SerialName("tanggal")
    val tanggal: Instant,

    @SerialName("tanggalString")
    val tanggalString: String,

    @SerialName("deskripsi")
    val deskripsi: String,

    @SerialName("fotoURL")
    val fotoURL: String,

    @SerialName("cretedAt")
    val createdAt: Instant
)