package com.example.skripsinongkrong.data.repository

import android.util.Log
import com.example.skripsinongkrong.data.model.Review
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
    private val COLLECTION_NAME = "dbtempatnongkrong"

    // =====================================================================
    // BAGIAN 1: READ DATA (Membaca Data dari Firestore)
    // =====================================================================

    // 1. Mengambil Semua Data Tempat
    fun getAllTempat(): Flow<List<TempatNongkrong>> = callbackFlow {
        val subscription = db.collection(COLLECTION_NAME)
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

    // 2. Mengambil Detail Satu Tempat
    fun getTempatDetail(placeId: String): Flow<TempatNongkrong?> = callbackFlow {
        val subscription = db.collection(COLLECTION_NAME).document(placeId)
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

    // 3. Mengambil List Review (Fitur Crowdsourcing)
    fun getReviews(placeId: String): Flow<List<Review>> = callbackFlow {
        val subscription = db.collection(COLLECTION_NAME).document(placeId)
            .collection("reviews")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviewList = snapshot.toObjects(Review::class.java)
                    trySend(reviewList)
                }
            }
        awaitClose { subscription.remove() }
    }

    // =====================================================================
    // BAGIAN 2: WRITE DATA (Update ke Firestore)
    // =====================================================================

    // 4. ADMIN SYNC: Cache Data dari Google Places API ke Firestore
    // (Ini Logic KRUSIAL dari kode lama yang Anda minta dipertahankan)
    suspend fun cachePlaceData(placeId: String, apiKey: String): Boolean {
        try {
            Log.d("TempatRepo", "Mencoba request untuk ID: $placeId")

            val response = api.getPlaceDetails(
                placeId = placeId,
                fields = "name,formatted_address,geometry,rating,user_ratings_total,price_level,photos,opening_hours",
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body()?.status == "OK") {
                val result = response.body()!!.result

                if (result != null) {
                    val lat = result.geometry?.location?.lat ?: 0.0
                    val lng = result.geometry?.location?.lng ?: 0.0
                    val photoRef = result.photos?.firstOrNull()?.photoReference ?: ""
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

                    // Simpan ke Firestore (SetOptions.merge() PENTING agar review lama tidak terhapus saat sync ulang)
                    db.collection(COLLECTION_NAME)
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

    // USER: Kirim Review & Update Skor (INI YANG KAMU CARI)
    suspend fun submitReview(
        placeId: String,
        userId: String,
        userName: String,
        rasa: Double,
        suasana: Double,
        kebersihan: Double,
        pelayanan: Double,
        ulasanText: String,
        adaColokan: Boolean,
        adaMushola: Boolean
    ): Result<Boolean> {
        return try {
            // A. Simpan Dokumen Review (Biar muncul di list)
            val reviewData = hashMapOf(
                "userId" to userId,
                "userName" to userName,
                "ratingRasa" to rasa,
                "ratingSuasana" to suasana,
                "ratingKebersihan" to kebersihan,
                "ratingPelayanan" to pelayanan,
                "text" to ulasanText,
                "timestamp" to FieldValue.serverTimestamp(),
                "adaColokan" to adaColokan,
                "adaMushola" to adaMushola
            )

            // Masukkan ke sub-collection "reviews"
            db.collection(COLLECTION_NAME).document(placeId)
                .collection("reviews")
                .add(reviewData)
                .await()

            // B. Update Agregat (Biar bintangnya berubah)
            val updates = hashMapOf<String, Any>(
                "skorRasaTotal" to FieldValue.increment(rasa),
                "jumlahPenilaiRasa" to FieldValue.increment(1),
                "skorSuasanaTotal" to FieldValue.increment(suasana),
                "jumlahPenilaiSuasana" to FieldValue.increment(1),
                "skorKebersihanTotal" to FieldValue.increment(kebersihan),
                "jumlahPenilaiKebersihan" to FieldValue.increment(1),
                "skorPelayananTotal" to FieldValue.increment(pelayanan),
                "jumlahPenilaiPelayanan" to FieldValue.increment(1),
                // Update total review umum juga (opsional, biar kelihatan ramai)
                "totalReview" to FieldValue.increment(1)
            )

            // C. Update Vote Fasilitas (Hanya jika "Ada")
            if (adaColokan) {
                updates["jumlahVoteColokanBanyak"] = FieldValue.increment(1)
            }
            if (adaMushola) {
                updates["jumlahVoteAdaMushola"] = FieldValue.increment(1)
            }

            // Eksekusi Update ke Dokumen Induk
            db.collection(COLLECTION_NAME).document(placeId)
                .update(updates)
                .await()

            Log.d("TempatRepo", "Review sukses terkirim ke Firebase!")
            Result.success(true)

        } catch (e: Exception) {
            Log.e("TempatRepo", "Gagal kirim review: ${e.message}")
            Result.failure(e)
        }
    }
}