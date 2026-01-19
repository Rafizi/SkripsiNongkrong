package com.example.skripsinongkrong.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    // State untuk menyimpan Email User yang sedang login
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    init {
        checkLoginStatus()
    }

    // Cek siapa yang login saat aplikasi dibuka
    fun checkLoginStatus() {
        _userEmail.value = repository.getUserEmail()
    }

    // Fungsi Logout (Opsional, buat tombol logout nanti)
    fun logout() {
        repository.logout()
        _userEmail.value = null
    }
    fun loginGoogle(idToken: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError()
            }
        }
    }
}