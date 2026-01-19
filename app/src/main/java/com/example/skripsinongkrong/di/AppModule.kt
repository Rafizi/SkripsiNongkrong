package com.example.skripsinongkrong.di

import android.content.Context
import com.example.skripsinongkrong.R
import com.example.skripsinongkrong.data.remote.PlaceApiService
import com.example.skripsinongkrong.data.repository.AuthRepository
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance(FirebaseApp.getInstance(), "dbtempatnongkrong")

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // --- KEMBALIKAN RETROFIT ---
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providePlacesApiService(retrofit: Retrofit): PlaceApiService =
        retrofit.create(PlaceApiService::class.java)

    // --- INJECT API SERVICE KE REPOSITORY ---
    @Provides
    @Singleton
    fun provideTempatRepository(db: FirebaseFirestore, api: PlaceApiService): TempatRepository {
        return TempatRepository(db, api)
    }



    // --- TAMBAHAN PENTING ---
    @Provides
    @Singleton
    fun provideGoogleSignInOptions(@ApplicationContext context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Pastikan string ini ada
            .requestEmail()
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(
        @ApplicationContext context: Context,
        options: GoogleSignInOptions
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, options)
    }
    // ------------------------

    // Update Provider AuthRepository untuk menerima GoogleSignInClient
    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        googleSignInClient: GoogleSignInClient // <-- Inject ini
    ): AuthRepository {
        return AuthRepository(auth, googleSignInClient)
    }
}