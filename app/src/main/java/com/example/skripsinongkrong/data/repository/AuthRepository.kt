package com.example.skripsinongkrong.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import android.content.Context
import android.util.Log
import com.example.skripsinongkrong.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    val authState: Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val isLoggedIn = firebaseAuth.currentUser != null
            trySend(isLoggedIn)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose { auth.removeAuthStateListener(authStateListener) }
    }
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // --- [INI PENYELAMATNYA] KEMBALIKAN KODE INI ---

    // 1. Ambil ID User (Dipakai untuk Simpan Vote/Bookmark)
    fun getUserId(): String {
        return auth.currentUser?.uid ?: ""
    }
    fun getUserName(): String {
        return auth.currentUser?.displayName ?: "Pengguna"
    }

    // Mengambil Email (Jika null, ganti "-")
    fun getUserEmail(): String {
        return auth.currentUser?.email ?: "-"
    }

    // Mengambil URL Foto (Tipe datanya Uri, kita ubah ke String)
    fun getUserPhotoUrl(): String? {
        return auth.currentUser?.photoUrl?.toString()
    }

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
        withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepo", "--- 1. Mulai Proses Logout Google ---")

                // [FIX 1] BUAT CLIENT BARU (Tanpa Cek getLastSignedInAccount)
                // Kita buat opsi sign-in yang sama persis dengan saat login
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                val googleClient = GoogleSignIn.getClient(context, gso)

                // [FIX 2] PAKSA SIGNOUT & REVOKE (Blind Execution)
                // Jangan pakai if(account != null). Langsung sikat!
                try {
                    googleClient.signOut().await()
                    googleClient.revokeAccess().await()
                    Log.d("AuthRepo", "--- 2. Google Cache & Access Dihapus Paksa ---")
                } catch (e: Exception) {
                    // Abaikan error "Sign in required" (Code 4), itu artinya memang sudah bersih
                    Log.w("AuthRepo", "Google Cleanup Warning (Aman): ${e.message}")
                }

            } catch (e: Exception) {
                Log.e("AuthRepo", "Fatal Google Logout: ${e.message}")
            } finally {
                // [FIX 3] BARU LOGOUT FIREBASE
                Log.d("AuthRepo", "--- 3. Logout Firebase ---")
                auth.signOut()
                // Begitu ini jalan, authStateListener akan mengirim sinyal FALSE ke ViewModel
            }
        }
    }
}
