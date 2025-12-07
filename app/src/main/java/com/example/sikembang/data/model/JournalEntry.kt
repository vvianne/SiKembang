package com.example.sikembang.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JournalEntry(
    val id: String? = null,

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