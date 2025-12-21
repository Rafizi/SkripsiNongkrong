package com.example.skripsinongkrong.data.repository

import android.util.Log
import com.example.skripsinongkrong.data.model.TempatNongkrong
import com.example.skripsinongkrong.data.remote.PlaceApiService
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TempatRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val api: PlaceApiService
) {
    fun getAllTempat(): Flow<List<TempatNongkrong>> = callbackFlow {
        val subscription = db.collection("skripsinongkrong")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val places = snapshot.toObjects(TempatNongkrong::class.java)
                    trySend(places)
                }
            }
        awaitClose { subscription.remove() }
    }

    // Mengambil DETAIL satu tempat berdasarkan ID
    fun getTempatDetail(placeId: String): Flow<TempatNongkrong?> = callbackFlow {
        val subscription = db.collection("skripsinongkrong").document(placeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val place = snapshot.toObject(TempatNongkrong::class.java)
                    trySend(place)
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    // Fungsi untuk Admin: Ambil data API & Cache ke Firestore
    suspend fun cachePlaceData(placeId: String, apiKey: String): Boolean {
        try {
            Log.d("TempatRepo", "Mencoba request untuk ID: $placeId")
            // 1. Panggil Google Places API
            val response = api.getPlaceDetails(
                placeId = placeId,
                fields = "name,formatted_address,geometry,rating,user_ratings_total,price_level,photos,opening_hours", // <--- Tambahkan baris ini
                apiKey = apiKey
            )
            Log.d("TempatRepo", "Respon diterima!")

            if (response.isSuccessful && response.body()?.status == "OK") {
                val result = response.body()!!.result

                if (result != null) {
                    // 2. Konversi data API ke Model Firebase kita
                    val lat = result.geometry?.location?.lat ?: 0.0
                    val lng = result.geometry?.location?.lng ?: 0.0
                    // Ambil referensi foto pertama (jika ada)
                    val photoRef = result.photos?.firstOrNull()?.photoReference ?: ""

                    // Ambil status buka (jika ada)
                    val isPlaceOpen = result.openingHours?.openNow
                    val dataBaru = TempatNongkrong(
                        id = placeId,
                        nama = result.name ?: "Tanpa Nama",
                        alamat = result.formattedAddress ?: "Alamat tidak tersedia",
                        lokasi = GeoPoint(lat, lng),
                        rating = result.rating ?: 0.0,
                        totalReview = result.userRatingsTotal ?: 0,
                        priceLevel = result.priceLevel ?: 0,

                        photoReference = photoRef,
                        isOpenNow = isPlaceOpen
                    )

                    // 3. Simpan ke Firestore (Merge = jangan timpa data rating yg sudah ada)
                    // Kita pakai 'placeId' sebagai ID Dokumen biar unik & gampang dicari
                    db.collection("skripsinongkrong")
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

    fun kirimReview(
        placeId: String,
        rasa: Int,
        suasana: Int,
        kebersihan: Int,
        pelayanan: Int,
        harga: Int,
        adaColokan: Boolean,
        adaMushola: Boolean
    ) {
        val updates = hashMapOf<String, Any>(
            // 1. Update Rasa
            "skorRasaTotal" to FieldValue.increment(rasa.toLong()),
            "jumlahPenilaiRasa" to FieldValue.increment(1),

            // 2. Update Suasana
            "skorSuasanaTotal" to FieldValue.increment(suasana.toLong()),
            "jumlahPenilaiSuasana" to FieldValue.increment(1),

            // 3. Update Kebersihan (Field baru akan otomatis dibuat Firestore)
            "skorKebersihanTotal" to FieldValue.increment(kebersihan.toLong()),
            "jumlahPenilaiKebersihan" to FieldValue.increment(1),

            // 4. Update Pelayanan
            "skorPelayananTotal" to FieldValue.increment(pelayanan.toLong()),
            "jumlahPenilaiPelayanan" to FieldValue.increment(1),

            // 5. Update Harga
            "skorHargaTotal" to FieldValue.increment(harga.toLong()),
            "jumlahPenilaiHarga" to FieldValue.increment(1)
        )

        // 6. Update Fasilitas (Hanya nambah jika User bilang "Ada")
        if (adaColokan) {
            updates["jumlahVoteColokanBanyak"] = FieldValue.increment(1)
        }
        if (adaMushola) {
            updates["jumlahVoteAdaMushola"] = FieldValue.increment(1)
        }

        // Eksekusi Update
        db.collection("skripsinongkrong").document(placeId).update(updates)
            .addOnSuccessListener { Log.d("Review", "Sukses kirim review!") }
            .addOnFailureListener { e -> Log.e("Review", "Gagal kirim: ${e.message}") }
    }
}