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

    // API KEY
    private val API_KEY = "AIzaSyD9Sca1UbjLrAKb5oTe3Ps_UfAJ7Gqi5yA"

    // LIST ID (Pastikan ID ini valid semua)
    private val targetPlaceIds = listOf(
        "ChIJUR0-gtHtaS4R-2URuESqHT4",
        "ChIJmQWdy63taS4Rwvp3D16wFrI",
        "ChIJ691qE_LtaS4RdQLOAf_rNT0",
        "ChIJbWywWH3taS4R-m07fPfkA58",
        "ChIJ7-3zQnrtaS4R8WzySJquDcw",
        "ChIJJRsgMADtaS4RBoKbsBc_PTc"
    )

    fun jalankanPengisianDatabase() {
        viewModelScope.launch {
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
        }
    }
}