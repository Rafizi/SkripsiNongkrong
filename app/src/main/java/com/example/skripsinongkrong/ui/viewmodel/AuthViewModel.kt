package com.example.skripsinongkrong.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _userName = MutableStateFlow("Penguat")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userEmail = MutableStateFlow("-")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _userPhotoUrl = MutableStateFlow<String?>(null)
    val userPhotoUrl: StateFlow<String?> = _userPhotoUrl.asStateFlow()
    val currentUser = repository.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        checkLoginStatus()
    }

    // 3. Login Status (Untuk navigasi ke Home)
    val isUserLoggedIn: StateFlow<Boolean> = repository.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = repository.isUserLoggedIn() // Nilai awal ambil dari status saat ini
        )

    // Cek siapa yang login saat aplikasi dibuka
    fun checkLoginStatus() {
        // Ambil data terbaru dari Repository
        _userName.value = repository.getUserName()
        _userEmail.value = repository.getUserEmail()
        _userPhotoUrl.value = repository.getUserPhotoUrl()
    }

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null // Reset error lama

                Log.w("LOGIN_DEBUG", "1. Proses Login Dimulai...")

                // 2. AMBIL TOKEN DARI TASK (INI YANG TADI HILANG)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    val result = repository.loginWithGoogle(idToken)
                    if (result.isSuccess) {
                        Log.d("LOGIN_DEBUG", "3. Firebase Login SUKSES!")

                        checkLoginStatus()
                    } else {
                    }
                }
                // ...
            } catch (e: Exception) {
                // ...
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.logout()
            // Setelah repo logout, isUserLoggedIn otomatis berubah jadi FALSE.

            // Reset data UI
            _userEmail.value = "Pengguna Email"
            _userName.value = "Pengguna"
            _userPhotoUrl.value = null
            _isLoading.value = false
        }
    }

}