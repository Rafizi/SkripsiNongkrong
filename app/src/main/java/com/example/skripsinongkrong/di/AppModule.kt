package com.example.skripsinongkrong.di // Sesuaikan package Anda

import com.example.skripsinongkrong.data.remote.PlaceApiService
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
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
        return FirebaseFirestore.getInstance("skripsinongkrong")
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
    fun providePlacesApiService(retrofit: Retrofit): PlaceApiService =
        retrofit.create(PlaceApiService::class.java)

    @Provides
    @Singleton
    fun provideTempatRepository(db: FirebaseFirestore, api: PlaceApiService): TempatRepository {
        return TempatRepository(db, api)
    }
}