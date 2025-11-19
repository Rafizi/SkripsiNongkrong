package com.example.skripsinongkrong.di // Sesuaikan package Anda

import com.example.skripsinongkrong.data.remote.PlacesApiService
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/") // Base URL Google Places
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePlacesApiService(retrofit: Retrofit): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)

    @Provides
    @Singleton
    fun provideTempatRepository(db: FirebaseFirestore, api: PlacesApiService): TempatRepository {
        return TempatRepository(db, api)
    }
}