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



}