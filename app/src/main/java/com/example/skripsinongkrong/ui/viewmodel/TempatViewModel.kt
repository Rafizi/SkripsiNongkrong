package com.example.skripsinongkrong.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.BuildConfig
import com.example.skripsinongkrong.data.model.Review
import com.example.skripsinongkrong.data.model.TempatNongkrong
import com.example.skripsinongkrong.data.repository.AuthRepository
import com.example.skripsinongkrong.data.repository.TempatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempatViewModel @Inject constructor(
    private val repository: TempatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _tempatList = MutableStateFlow<List<TempatNongkrong>>(emptyList())
    val tempatList: StateFlow<List<TempatNongkrong>> = _tempatList

    private val _selectedTempat = MutableStateFlow<TempatNongkrong?>(null)
    val selectedTempat: StateFlow<TempatNongkrong?> = _selectedTempat

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    init {
        fetchTempatList()
    }

    private fun fetchTempatList() {
        viewModelScope.launch {
            repository.getAllTempat().collect { daftarTempat ->
                _tempatList.value = daftarTempat.sortedByDescending { it.rating } // Sort by Rating Google
            }
        }
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

    // --- FUNGSI ADMIN SYNC ---
    fun syncDataAdmin() {
        viewModelScope.launch {
            val targetPlaceId = "ChIJN1t_tDeuEmsRUsoyG83frY4"
            val apiKey = BuildConfig.MAPS_API_KEY

            Log.d("TempatViewModel", "Mencoba Sync...")
            val success = repository.cachePlaceData(targetPlaceId, apiKey)

            if (success) {
                fetchTempatList()
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
        adaMushola: Boolean
    ) {
        viewModelScope.launch {
            val userId = authRepository.getUserId()
            val userName = authRepository.getUserName()
            val finalName = if (userName.isNotEmpty()) userName else "Pengunjung"

            // GANTI DARI kirimReview JADI submitReview
            repository.submitReview(
                placeId = placeId,
                userId = userId,
                userName = finalName,
                rasa = rasa,
                suasana = suasana,
                kebersihan = kebersihan,
                pelayanan = pelayanan,
                ulasanText = ulasanText,
                adaColokan = adaColokan,
                adaMushola = adaMushola
            )

            fetchTempatList()
            loadDetail(placeId)
        }
    }
}