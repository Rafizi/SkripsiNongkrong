package com.example.skripsinongkrong.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.map

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow<String?>(repository.currentUser?.email)
    val userEmail: StateFlow<String?> = _userEmail

    private val _userName = MutableStateFlow<String?>(repository.currentUser?.displayName)
    val userName: StateFlow<String?> = _userName

    private val _userPhotoUrl =
        MutableStateFlow<String?>(repository.currentUser?.photoUrl?.toString())
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl
    val currentUser = repository.currentUser

    init {
        checkLoginStatus()
    }

    val isUserLoggedIn = flow {
        emit(repository.isUserLoggedIn())
    }

    // Cek siapa yang login saat aplikasi dibuka
    fun checkLoginStatus() {
        _userEmail.value = repository.getUserEmail()
    }

    // Fungsi Logout (Opsional, buat tombol logout nanti)
    fun loginGoogle(idToken: String, onSuccess: () -> Unit, onError: () -> Unit) {
        viewModelScope.launch {
            val result = repository.loginWithGoogle(idToken)
            if (result.isSuccess) {
                refreshUserData()
                onSuccess()
            } else {
                onError()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            // Panggil logout repository (yang sekarang suspend)
            repository.logout()

            // Reset State Lokal
            _userEmail.value = null
            _userName.value = null
            _userPhotoUrl.value = null
        }
    }

    private fun refreshUserData() {
        // Ambil data terbaru dari repository setelah login sukses
        val user = repository.currentUser
        _userEmail.value = user?.email
        _userName.value = user?.displayName
        _userPhotoUrl.value = user?.photoUrl?.toString()
    }
}