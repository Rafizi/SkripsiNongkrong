package com.example.skripsinongkrong.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {

    // Cek status login saat aplikasi dibuka
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getUserId(): String = auth.currentUser?.uid ?: ""
    fun getUserName(): String = auth.currentUser?.displayName ?: ""

    // --- TAMBAHKAN FUNGSI INI ---
    fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    // Fungsi Logout (jika belum ada)
    fun logout() {
        auth.signOut()
    }

    // Fungsi Login ke Firebase pakai Token dari Google
    suspend fun loginWithGoogle(idToken: String): Result<Boolean> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}