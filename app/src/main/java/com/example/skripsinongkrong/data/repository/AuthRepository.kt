package com.example.skripsinongkrong.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient,
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // Fungsi cek login sederhana
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    fun getUserId(): String = auth.currentUser?.uid ?: ""
    fun getUserName(): String = auth.currentUser?.displayName ?: ""

    // --- TAMBAHKAN FUNGSI INI ---
    fun getUserEmail(): String? {
        return auth.currentUser?.email
    }

    // Fungsi Logout (jika belum ada)

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

    suspend fun logout() {
        try {
            // 1. Keluar dari Firebase
            auth.signOut()

            // 2. Keluar dari Google Client (INI KUNCINYA)
            // Ini akan menghapus cache login google di HP
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}