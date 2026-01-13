package com.example.skripsinongkrong.data.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    // Cek status login saat aplikasi dibuka
    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    // Ambil ID user (untuk data review nanti)
    fun getUserId(): String = auth.currentUser?.uid ?: ""
    fun getUserName(): String = auth.currentUser?.displayName ?: "Pengunjung"

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

    // Fungsi Logout
    fun signOut(context: Context) {
        auth.signOut()
        // Hapus akses Google di HP biar bisa ganti akun
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        GoogleSignIn.getClient(context, gso).signOut()
    }
}