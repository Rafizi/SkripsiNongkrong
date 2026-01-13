package com.example.skripsinongkrong.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.TempatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TempatRepository
) : ViewModel() {

    // ⚠️ PENTING: Masukkan API Key Google Places Anda di sini
    private val API_KEY = "AIzaSyD9Sca1UbjLrAKb5oTe3Ps_UfAJ7Gqi5yA"

    // ⚠️ PENTING: Masukkan Place ID tempat-tempat di Jakarta Timur yang sudah Anda cari
    private val targetPlaceIds = listOf(
        "ChIJUR0-gtHtaS4R-2URuESqHT4",
        "ChIJmQWdy63taS4Rwvp3D16wFrI",
        "ChIJ691qE_LtaS4RdQLOAf_rNT0",
        "ChIJbWywWH3taS4R-m07fPfkA58",
        "ChIJ7-3zQnrtaS4R8WzySJquDcw",
        "ChIJJRsgMADtaS4RBoKbsBc_PTc",
        // ... Tambahkan ID lainnya di sini (pisahkan dengan koma)
    )

    fun jalankanPengisianDatabase() {
        viewModelScope.launch {
            Log.d("Admin", "=== MULAI PROSES CACHE (PARALEL) ===")

            // Pindah ke Thread IO (Background) biar tidak macet
            withContext(Dispatchers.IO) {
                // Buat daftar pekerjaan (Jobs) secara serentak
                val jobs = targetPlaceIds.map { placeId ->
                    async {
                        Log.d("Admin", "🚀 Menembak request: $placeId")
                        repository.cachePlaceData(placeId, API_KEY)
                    }
                }

                // Tunggu semua pekerjaan selesai
                jobs.awaitAll()
            }

            Log.d("Admin", "=== SEMUA PROSES SELESAI ===")
        }
    }
}