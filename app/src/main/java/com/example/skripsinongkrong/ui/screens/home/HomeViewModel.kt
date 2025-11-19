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
    private val API_KEY = "MASUKKAN_API_KEY_ANDA_DISINI"

    // ⚠️ PENTING: Masukkan Place ID tempat-tempat di Jakarta Timur yang sudah Anda cari
    private val targetPlaceIds = listOf(
        "ChIJP1yUHCnPQS0R0iC-Cj8l55g", // Contoh ID 1
        "ChIJybDUu3vPaS0R2L2i8eqN5wE", // Contoh ID 2
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