package com.example.skripsinongkrong.ui.screens.home // Sesuaikan package

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.TempatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TempatRepository
) : ViewModel() {

    // ⚠️ PENTING: Masukkan API Key Google Places Anda di sini
    private val API_KEY = "AIzaSyD9Sca1UbjLrAKb5oTe3Ps_UfAJ7Gqi5yA"

    // ⚠️ PENTING: Masukkan Place ID tempat-tempat di Jakarta Timur yang sudah Anda cari
    private val targetPlaceIds = listOf(
        "ChIJUR0-gtHtaS4R-2URuESqHT4", // Contoh ID 1
        "ChIJmQWdy63taS4Rwvp3D16wFrI", // Contoh ID 2
        "ChIJ691qE_LtaS4RdQLOAf_rNT0", // Contoh ID 2
        "ChIJbWywWH3taS4R-m07fPfkA58", // Contoh ID 2
        "ChIJ7-3zQnrtaS4R8WzySJquDcw", // Contoh ID 2
        // ... Tambahkan ID lainnya di sini (pisahkan dengan koma)
    )

    fun jalankanPengisianDatabase() {
        viewModelScope.launch {
            Log.d("Admin", "=== MULAI PROSES CACHE ===")

            targetPlaceIds.forEach { placeId ->
                // Panggil repository untuk setiap ID
                repository.cachePlaceData(placeId, API_KEY)
            }

            Log.d("Admin", "=== SELESAI PROSES CACHE ===")
        }
    }
}