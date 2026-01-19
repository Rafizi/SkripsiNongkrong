package com.example.skripsinongkrong.data.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName

data class TempatNongkrong(
    val id: String = "",
    val nama: String = "",
    val alamat: String = "",
    val lokasi: GeoPoint? = null,

    val rating: Double = 0.0,
    val totalReview: Int = 0,
    val priceLevel: Int = 0,

    val photoReference: String = "",

    @get:PropertyName("isOpenNow")
    @set:PropertyName("isOpenNow")
    var isOpenNow: Boolean? = null,

    // Skor Rata-rata
    val rataRasa: Double = 0.0,
    val rataSuasana: Double = 0.0,
    val rataKebersihan: Double = 0.0,
    val rataPelayanan: Double = 0.0,

    @get:PropertyName("jumlahVoteColokanBanyak")
    @set:PropertyName("jumlahVoteColokanBanyak")
    var jumlahVoteColokanBanyak: Int = 0,

    @get:PropertyName("jumlahVoteAdaMushola")
    @set:PropertyName("jumlahVoteAdaMushola")
    var jumlahVoteAdaMushola: Int = 0,

    @get:PropertyName("jumlahVoteAdaWifi")
    @set:PropertyName("jumlahVoteAdaWifi")
    var jumlahVoteAdaWifi: Int = 0,

    val updateTerakhir: Long = System.currentTimeMillis()
) {
    @get:Exclude @set:Exclude
    var jarakDariUserKm: Double = 0.0
}