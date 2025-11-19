package com.example.skripsinongkrong.data.repository

import android.util.Log
import com.example.skripsinongkrong.data.model.TempatNongkrong
import com.example.skripsinongkrong.data.remote.PlaceApiService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TempatRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val api: PlaceApiService
) {

    // Fungsi untuk Admin: Ambil data API & Cache ke Firestore
    suspend fun cachePlaceData(placeId: String, apiKey: String): Boolean {
        try {
            // 1. Panggil Google Places API
            val response = api.getPlaceDetails(
                placeId = placeId,
                fields = "name,formatted_address,geometry", // <--- Tambahkan baris ini
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body()?.status == "OK") {
                val result = response.body()!!.result

                if (result != null) {
                    // 2. Konversi data API ke Model Firebase kita
                    val lat = result.geometry?.location?.lat ?: 0.0
                    val lng = result.geometry?.location?.lng ?: 0.0

                    val dataBaru = TempatNongkrong(
                        id = placeId,
                        nama = result.name ?: "Tanpa Nama",
                        alamat = result.formattedAddress ?: "Alamat tidak tersedia",
                        lokasi = GeoPoint(lat, lng)
                    )

                    // 3. Simpan ke Firestore (Merge = jangan timpa data rating yg sudah ada)
                    // Kita pakai 'placeId' sebagai ID Dokumen biar unik & gampang dicari
                    db.collection("TempatNongkrong")
                        .document(placeId)
                        .set(dataBaru, SetOptions.merge())
                        .await()

                    Log.d("TempatRepo", "Berhasil cache: ${result.name}")
                    return true
                }
            } else {
                Log.e("TempatRepo", "Gagal API: ${response.errorBody()?.string()} atau Status: ${response.body()?.status}")
            }
        } catch (e: Exception) {
            Log.e("TempatRepo", "Error Exception: ${e.message}")
        }
        return false
    }
}