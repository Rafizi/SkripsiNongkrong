package com.example.skripsinongkrong.data.model

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",
    val userId: String = "",
    val userName: String = "Pengunjung", // <--- PENTING: Nama User
    val text: String = "",

    // Rating Spesifik (Skripsi Crowdsourcing)
    val ratingRasa: Double = 0.0,
    val ratingSuasana: Double = 0.0,
    val ratingKebersihan: Double = 0.0,
    val ratingPelayanan: Double = 0.0,

    val timestamp: Timestamp? = null
) {
    // Helper untuk format tanggal (Opsional)
    fun getFormattedDate(): String {
        if (timestamp == null) return ""
        val date = timestamp.toDate()
        val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
        return format.format(date)
    }
}