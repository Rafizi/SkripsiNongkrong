package com.example.skripsinongkrong.data.repository

import com.example.skripsinongkrong.data.remote.PlacesApiService
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
class TempatRepository @Inject constructor (
    private val db: FirebaseFirestore,
    private val api: PlacesApiService
){
    suspend fun isiDatabaseAwal(placeId: String) {
        // ... (Logika memanggil api dan menyimpan ke db)
    }

    suspend fun kirimReview(placeId: String, reviewData: Map<String, Any>) {
        // ... (Logika menyimpan review ke db)
    }
}