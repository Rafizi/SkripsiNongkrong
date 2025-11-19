package com.example.skripsinongkrong.data.remote

import com.example.skripsinongkrong.data.model.PlacesApiResponse
import retrofit2.Response

import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApiService {
    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String
    ): Response<PlacesApiResponse>
}