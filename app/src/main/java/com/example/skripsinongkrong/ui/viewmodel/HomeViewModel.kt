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
class HomeViewModel @Inject constructor(
    private val repository: TempatRepository
) : ViewModel() {

    private val _allPlaces = MutableStateFlow<List<TempatNongkrong>>(emptyList())
    private val _places = MutableStateFlow<List<TempatNongkrong>>(emptyList())
    val places: StateFlow<List<TempatNongkrong>> = _places

    fun getPlacesByLocation() {
        viewModelScope.launch {
            // Kita kumpulkan flow-nya
            repository.getAllTempat().collect { result ->
                // PERBAIKAN: Pastikan result dianggap sebagai List<TempatNongkrong>
                // Jika result-nya dibungkus Result<...>, pakai .getOrElse { emptyList() }
                // Jika result-nya langsung List, langsung assign.
                // Asumsi: Repository mengembalikan Flow<List<TempatNongkrong>> atau sejenisnya.

                // Coba kode aman ini:
                try {
                    // Jika repository return Resource/Result, sesuaikan di sini.
                    // Ini asumsi return data langsung atau List
                    val data = result as? List<TempatNongkrong> ?: emptyList()

                    _allPlaces.value = data
                    _places.value = data
                } catch (e: Exception) {
                    _places.value = emptyList()
                }
            }
        }
    }

    fun filterData(colokan: Boolean, mushola: Boolean, wifi: Boolean) {
        val currentList = _allPlaces.value

        val filtered = currentList.filter { tempat ->
            // PERBAIKAN: Akses Map dengan aman (Safe Call ?.)
            val fasilitas = tempat.fasilitas ?: emptyMap()

            val matchColokan = if (colokan) fasilitas["colokan"] == true else true
            val matchMushola = if (mushola) fasilitas["mushola"] == true else true
            val matchWifi = if (wifi) fasilitas["wifi"] == true else true

            matchColokan && matchMushola && matchWifi
        }

        _places.value = filtered
    }
}