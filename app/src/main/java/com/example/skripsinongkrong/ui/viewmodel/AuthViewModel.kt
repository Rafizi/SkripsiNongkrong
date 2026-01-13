package com.example.skripsinongkrong.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skripsinongkrong.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

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