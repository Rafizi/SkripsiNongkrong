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

    // --- 4. ADMIN SYNC (SMART UPDATE: TIDAK MERESET VOTE USER) ---
    suspend fun cachePlaceData(placeId: String, apiKey: String): Boolean {
        return try {
            Log.d("TempatRepo", "Mencoba request Sync untuk ID: $placeId")

            // 1. Panggil API Google (Sesuai ApiService kamu)
            val response = api.getPlaceDetails(
                placeId = placeId,
                fields = "name,formatted_address,geometry,rating,user_ratings_total,price_level,photos,opening_hours",
                apiKey = apiKey
            )

            if (response.isSuccessful && response.body()?.status == "OK") {
                val result = response.body()!!.result

                if (result != null) {
                    val docRef = db.collection(COLLECTION_NAME).document(placeId)

                    // Cek Data Lama di Firestore
                    val snapshot = docRef.get().await()

                    // Siapkan Data Dasar dari Google (Ini data yang BOLEH di-update kapan saja)
                    val lat = result.geometry?.location?.lat ?: 0.0
                    val lng = result.geometry?.location?.lng ?: 0.0

                    val googleMapData = hashMapOf<String, Any?>(
                        "nama" to (result.name ?: "Tanpa Nama"),
                        "alamat" to (result.formattedAddress ?: "Alamat tidak tersedia"),
                        "lokasi" to GeoPoint(lat, lng),
                        "priceLevel" to (result.priceLevel ?: 0),
                        "photoReference" to (result.photos?.firstOrNull()?.photoReference ?: ""),
                        "isOpenNow" to (result.openingHours?.openNow)
                        // Catatan: Rating Google ("rating") sebaiknya tidak menimpa rating aplikasi ("rating")
                        // jika kita ingin rating murni dari user aplikasi.
                        // Tapi kita bisa simpan "ratingGoogle" terpisah jika mau.
                    )

                    if (snapshot.exists()) {
                        // Cek apakah field fasilitas ada? Jika tidak, inisialisasi ke 0
                        val currentData = snapshot.data
                        if (currentData != null) {
                            if (!currentData.containsKey("jumlahVoteAdaWifi")) {
                                googleMapData["jumlahVoteAdaWifi"] = 0
                                Log.w("TempatRepo", "Field Wifi hilang, menambahkan default 0.")
                            }
                            if (!currentData.containsKey("jumlahVoteColokanBanyak")) {
                                googleMapData["jumlahVoteColokanBanyak"] = 0
                            }
                            if (!currentData.containsKey("jumlahVoteAdaMushola")) {
                                googleMapData["jumlahVoteAdaMushola"] = 0
                            }
                        }

                        docRef.update(googleMapData).await()
                        Log.d("TempatRepo", "UPDATE Metadata Berhasil")
                    } else {
                        // === KASUS 2: DATA BARU (INISIALISASI) ===
                        // Karena belum ada, kita wajib set semua counter ke 0

                        googleMapData["id"] = placeId
                        // Gunakan rating Google sebagai start awal (opsional, bisa juga 0.0)
                        googleMapData["rating"] = result.rating ?: 0.0
                        googleMapData["totalReview"] = 0 // Total review aplikasi mulai dari 0

                        // Inisialisasi Vote User
                        googleMapData["jumlahVoteColokanBanyak"] = 0
                        googleMapData["jumlahVoteAdaMushola"] = 0
                        googleMapData["jumlahVoteAdaWifi"] = 0

                        // Inisialisasi Skor
                        googleMapData["skorRasaTotal"] = 0.0
                        googleMapData["skorSuasanaTotal"] = 0.0
                        googleMapData["skorKebersihanTotal"] = 0.0
                        googleMapData["skorPelayananTotal"] = 0.0

                        // Inisialisasi Rata-rata
                        googleMapData["rataRasa"] = 0.0
                        googleMapData["rataSuasana"] = 0.0
                        googleMapData["rataKebersihan"] = 0.0
                        googleMapData["rataPelayanan"] = 0.0

                        docRef.set(googleMapData).await()
                        Log.d("TempatRepo", "SMART SYNC: Data Baru Dibuat: ${result.name}")
                    }
                    return true
                }
            } else {
                Log.e("TempatRepo", "Gagal API: ${response.errorBody()?.string()} atau Status: ${response.body()?.status}")
            }
            false
        } catch (e: Exception) {
            Log.e("TempatRepo", "Error Exception: ${e.message}")
            false
        }
    }

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
        adaMushola: Boolean,
        adaWifi: Boolean
    ): Result<Boolean> {
        return try {
            val placeRef = db.collection(COLLECTION_NAME).document(placeId)

            // A. Simpan Dokumen Review di Subcollection
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
                "adaMushola" to adaMushola,
                "adaWifi" to adaWifi
            )

            // Simpan Review dulu
            placeRef.collection("reviews").add(reviewData).await()

            // B. UPDATE DATA INDUK DENGAN TRANSACTION (Supaya Aman & Akurat)
            db.runTransaction { transaction ->
                val snapshot = transaction.get(placeRef)

                // 1. Ambil Data Lama (Handle jika null/0)
                val totalReviewLama = snapshot.getLong("totalReview") ?: 0
                val skorRasaLama = snapshot.getDouble("skorRasaTotal") ?: 0.0
                val skorSuasanaLama = snapshot.getDouble("skorSuasanaTotal") ?: 0.0
                val skorKebersihanLama = snapshot.getDouble("skorKebersihanTotal") ?: 0.0
                val skorPelayananLama = snapshot.getDouble("skorPelayananTotal") ?: 0.0

                // 2. Hitung Data Baru
                val totalReviewBaru = totalReviewLama + 1
                val skorRasaBaru = skorRasaLama + rasa
                val skorSuasanaBaru = skorSuasanaLama + suasana
                val skorKebersihanBaru = skorKebersihanLama + kebersihan
                val skorPelayananBaru = skorPelayananLama + pelayanan

                // 3. HITUNG RATA-RATA BARU (PENTING BUAT UI!)
                val rataRasaBaru = skorRasaBaru / totalReviewBaru
                val rataSuasanaBaru = skorSuasanaBaru / totalReviewBaru
                val rataKebersihanBaru = skorKebersihanBaru / totalReviewBaru
                val rataPelayananBaru = skorPelayananBaru / totalReviewBaru

                // Rata-rata total (Bintang Utama)
                val ratingUtamaBaru = (rataRasaBaru + rataSuasanaBaru + rataKebersihanBaru + rataPelayananBaru) / 4.0

                // 4. Siapkan Data Update
                val updates = mutableMapOf<String, Any>(
                    "totalReview" to totalReviewBaru,
                    "skorRasaTotal" to skorRasaBaru,
                    "skorSuasanaTotal" to skorSuasanaBaru,
                    "skorKebersihanTotal" to skorKebersihanBaru,
                    "skorPelayananTotal" to skorPelayananBaru,

                    // Update Field Rata-Rata (Agar UI langsung berubah)
                    "rataRasa" to rataRasaBaru,
                    "rataSuasana" to rataSuasanaBaru,
                    "rataKebersihan" to rataKebersihanBaru,
                    "rataPelayanan" to rataPelayananBaru,
                    "rating" to ratingUtamaBaru // Update bintang utama
                )

                // 5. Update Counter Fasilitas (Increment Manual biar aman)
                if (adaColokan) {
                    val lama = snapshot.getLong("jumlahVoteColokanBanyak") ?: 0
                    updates["jumlahVoteColokanBanyak"] = lama + 1
                }
                if (adaMushola) {
                    val lama = snapshot.getLong("jumlahVoteAdaMushola") ?: 0
                    updates["jumlahVoteAdaMushola"] = lama + 1
                }
                if (adaWifi) {
                    val lama = snapshot.getLong("jumlahVoteAdaWifi") ?: 0
                    updates["jumlahVoteAdaWifi"] = lama + 1
                }

                // Eksekusi Update
                transaction.update(placeRef, updates)
            }.await()

            Log.d("TempatRepo", "Review & Rata-rata Sukses Diupdate!")
            Result.success(true)

        } catch (e: Exception) {
            Log.e("TempatRepo", "Gagal kirim review: ${e.message}")
            Result.failure(e)
        }
    }
}