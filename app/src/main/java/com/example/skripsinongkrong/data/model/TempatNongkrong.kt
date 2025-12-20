package com.example.skripsinongkrong.data.model

import com.google.firebase.firestore.GeoPoint

data class TempatNongkrong(
    val id: String = "", // Kita isi dengan Place ID
    val nama: String = "",
    val alamat: String = "",
    val lokasi: GeoPoint? = null,

    val rating: Double = 0.0,       // Kriteria: Benefit (Semakin besar semakin bagus)
    val totalReview: Int = 0,       // Kriteria: Benefit (Popularitas)
    val priceLevel: Int = 0,        // Kriteria: Cost (0=Gratis, 4=Mahal Banget)

    val photoReference: String = "", // Untuk nanti ambil gambar
    val isOpenNow: Boolean? = null ,  // Buka/Tutup (Opsional)
    // Data Crowdsourcing (Nilai awal 0)
    // Kita siapkan field-nya biar nanti gampang di-update
    val skorRasaTotal: Double = 0.0,
    val jumlahPenilaiRasa: Int = 0,
    val skorSuasanaTotal: Double = 0.0,
    val jumlahPenilaiSuasana: Int = 0,
    // ... tambahkan field lain sesuai kebutuhan nanti

    val updateTerakhir: Long = System.currentTimeMillis()
)