package com.example.skripsinongkrong.ui.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.model.Review
import com.example.skripsinongkrong.data.model.TempatNongkrong
import com.example.skripsinongkrong.data.repository.AuthRepository
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TempatViewModel @Inject constructor(
    private val repository: TempatRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // --- KONFIGURASI ADMIN (DATA DARI CODE LAMA KAMU) ---
    private val API_KEY = "AIzaSyD9Sca1UbjLrAKb5oTe3Ps_UfAJ7Gqi5yA" // API Key Kamu

    // List ID Tempat yang ingin kamu masukkan ke database
    private val targetPlaceIds = listOf(
        "ChIJUR0-gtHtaS4R-2URuESqHT4",
        "ChIJmQWdy63taS4Rwvp3D16wFrI",
        "ChIJ691qE_LtaS4RdQLOAf_rNT0",
        "ChIJbWywWH3taS4R-m07fPfkA58",
        "ChIJ7-3zQnrtaS4R8WzySJquDcw",
        "ChIJJRsgMADtaS4RBoKbsBc_PTc"
    )
    // ----------------------------------------------------

    private var lastKnownLocation: Location? = null
    private var originalList: List<TempatNongkrong> = emptyList()

    private val _tempatList = MutableStateFlow<List<TempatNongkrong>>(emptyList())
    val tempatList: StateFlow<List<TempatNongkrong>> = _tempatList

    private val _selectedTempat = MutableStateFlow<TempatNongkrong?>(null)
    val selectedTempat: StateFlow<TempatNongkrong?> = _selectedTempat

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _submitStatus = MutableStateFlow<Boolean?>(null)
    val submitStatus: StateFlow<Boolean?> = _submitStatus

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchTempatList()
    }

    private fun fetchTempatList() {
        viewModelScope.launch {
            repository.getAllTempat().collect { daftarTempat ->
                originalList = daftarTempat
                if (lastKnownLocation != null) {
                    updateListDenganJarak(lastKnownLocation!!)
                } else {
                    _tempatList.value = daftarTempat.sortedByDescending { it.rating }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun hitungJarakLokasiUser() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                lastKnownLocation = location
                updateListDenganJarak(location)
            }
        }
    }

    // --- FUNGSI ADMIN SYNC (Diadaptasi dari Code Lama Kamu) ---
    // Dipanggil dari Tombol Rahasia di Profil
    fun syncDataAdmin() {
        viewModelScope.launch {
            _isLoading.value = true
            Log.d("Admin", "=== MULAI PROSES CACHE (PARALEL) ===")

            // 1. Pindah ke Jalur IO (Internet) agar tidak membebani HP
            withContext(Dispatchers.IO) {
                // 2. Siapkan semua request secara bersamaan (Async)
                val jobs = targetPlaceIds.map { placeId ->
                    async {
                        Log.d("Admin", "🚀 Mengirim Request: $placeId")
                        // Panggil Repository
                        val hasil = repository.cachePlaceData(placeId, API_KEY)

                        // Log Hasil per Item
                        if (hasil) {
                            Log.d("Admin", "✅ SUKSES: $placeId")
                        } else {
                            Log.e("Admin", "❌ GAGAL: $placeId (Cek Log TempatRepo)")
                        }
                    }
                }
                // 3. Tunggu sampai SEMUANYA selesai
                jobs.awaitAll()
            }

            Log.d("Admin", "=== SELESAI SEMUA PROSES ===")
            fetchTempatList() // Refresh data di aplikasi agar langsung muncul
            _isLoading.value = false
        }
    }

    // --- FITUR FILTER ---
    fun filterByKriteria(kriteria: String) {
        val sortedList = when (kriteria) {
            "Terdekat" -> originalList.sortedBy { it.jarakDariUserKm }
            // GANTI getRataRasa() JADI rataRasa (akses variabel langsung)
            "Rasa" -> originalList.sortedByDescending { it.rataRasa }
            "Suasana" -> originalList.sortedByDescending { it.rataSuasana }
            "Kebersihan" -> originalList.sortedByDescending { it.rataKebersihan }
            "Pelayanan" -> originalList.sortedByDescending { it.rataPelayanan }

            "Colokan" -> originalList.filter { it.jumlahVoteColokanBanyak > 0 }
            "Mushola" -> originalList.filter { it.jumlahVoteAdaMushola > 0 }
            else -> originalList.sortedByDescending { it.rating }
        }
        _tempatList.value = sortedList
    }

    fun loadDetail(placeId: String) {
        viewModelScope.launch {
            repository.getTempatDetail(placeId).collect { detail ->
                _selectedTempat.value = detail
            }
        }
        viewModelScope.launch {
            repository.getReviews(placeId).collect { reviewList ->
                _reviews.value = reviewList
            }
        }
    }

    // --- FUNGSI SUBMIT REVIEW ---
    fun submitReview(
        placeId: String,
        rasa: Double,
        suasana: Double,
        kebersihan: Double,
        pelayanan: Double,
        ulasanText: String,
        adaColokan: Boolean,
        adaMushola: Boolean,
        adaWifi: Boolean // <-- TAMBAHAN
    ) {
        viewModelScope.launch {
            val userId = authRepository.getUserId()
            val userName = authRepository.getUserName()
            val finalName = if (userName.isNotEmpty()) userName else "Pengunjung"

            val result = repository.submitReview(
                placeId, userId, finalName, rasa, suasana,
                kebersihan, pelayanan, ulasanText, adaColokan, adaMushola, adaWifi
            )

            _isLoading.value = false

            if (result.isSuccess) {
                _submitStatus.value = true
                fetchTempatList()
                loadDetail(placeId)
            } else {
                _submitStatus.value = false
            }
        }
    }

    fun resetSubmitStatus() {
        _submitStatus.value = null
    }

    private fun updateListDenganJarak(location: Location) {
        val listBaru = originalList.map { tempat ->
            val latTempat = tempat.lokasi?.latitude ?: 0.0
            val lngTempat = tempat.lokasi?.longitude ?: 0.0
            val hasilJarak = FloatArray(1)
            Location.distanceBetween(
                location.latitude, location.longitude,
                latTempat, lngTempat, hasilJarak
            )
            tempat.copy().apply { jarakDariUserKm = hasilJarak[0] / 1000.0 }
        }
        originalList = listBaru
        _tempatList.value = listBaru.sortedBy { it.jarakDariUserKm }
    }
}