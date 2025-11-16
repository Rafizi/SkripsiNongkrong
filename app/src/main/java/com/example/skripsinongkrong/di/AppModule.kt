package com.example.skripsinongkrong.di


import com.example.skripsinongkrong.data.remote.PlacesApiService
import com.example.skripsinongkrong.data.repository.TempatRepository
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore


@Module
@InstallIn(SingletonComponent::class) // Berarti Hilt akan mengelola ini selama aplikasi hidup
object AppModule {

    // 1. Memberitahu Hilt cara membuat instance Firestore
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    // 2. Memberitahu Hilt cara membuat instance Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/") // Base URL untuk Google Places API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // 3. Memberitahu Hilt cara membuat instance PlacesApiService (menggunakan Retrofit)
    @Provides
    @Singleton
    fun providePlacesApiService(retrofit: Retrofit): PlacesApiService =
        retrofit.create(PlacesApiService::class.java)

    // 4. Memberitahu Hilt cara membuat Repository Utama Anda
    @Provides
    @Singleton
    fun provideTempatRepository(
        db: FirebaseFirestore,
        api: PlacesApiService
    ): TempatRepository {
        return TempatRepository(db, api)
    }
}