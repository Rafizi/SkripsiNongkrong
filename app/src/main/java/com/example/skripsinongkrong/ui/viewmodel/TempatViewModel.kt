package com.example.skripsinongkrong.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.model.TempatNongkrong
import com.example.skripsinongkrong.data.repository.TempatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TempatViewModel @Inject constructor(
    private val repository: TempatRepository
) : ViewModel() {

    // 1. State untuk menampung List Tempat (Yuk Cari!)
    private val _tempatList = MutableStateFlow<List<TempatNongkrong>>(emptyList())
    val tempatList: StateFlow<List<TempatNongkrong>> = _tempatList

    // 2. State untuk menampung Detail Tempat yang dipilih
    private val _selectedTempat = MutableStateFlow<TempatNongkrong?>(null)
    val selectedTempat: StateFlow<TempatNongkrong?> = _selectedTempat

    init {
        // Otomatis ambil data saat ViewModel ini dibuat pertama kali
        fetchTempatList()
    }

    // Fungsi ambil semua data
    private fun fetchTempatList() {
        viewModelScope.launch {
            repository.getAllTempat().collect { daftarTempat ->
                _tempatList.value = daftarTempat
            }
        }
    }

    // Fungsi ambil detail satu tempat
    fun loadDetail(placeId: String) {
        viewModelScope.launch {
            repository.getTempatDetail(placeId).collect {   detail ->
                _selectedTempat.value = detail
            }
        }
    }
    fun submitReview(
        placeId: String,
        rasa: Int, suasana: Int, kebersihan: Int, pelayanan: Int, harga: Int,
        colokan: Boolean, mushola: Boolean
    ) {
        viewModelScope.launch {
            repository.kirimReview(placeId, rasa, suasana, kebersihan, pelayanan, harga, colokan, mushola)
        }
    }
}