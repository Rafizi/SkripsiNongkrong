package com.example.skripsinongkrong.di

import com.example.skripsinongkrong.data.remote.PlaceApiService
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}