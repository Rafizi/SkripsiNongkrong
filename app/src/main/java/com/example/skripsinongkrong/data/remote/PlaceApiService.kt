package com.example.skripsinongkrong.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceApiService {
    @GET("maps/api/place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String,
        @Query("key") apiKey: String,
        @Query("language") language: String = "id"
    ): Response<PlaceDetailsResponse>
}

data class PlaceDetailsResponse(
    @SerializedName("result") val result: PlaceResult?,
    @SerializedName("status") val status: String,
    @SerializedName("error_message") val errorMessage: String? = null // Penyelamat Debugging
)

data class PlaceResult(
    @SerializedName("name") val name: String?,
    @SerializedName("formatted_address") val formattedAddress: String?,
    @SerializedName("geometry") val geometry: Geometry?,
    @SerializedName("rating") val rating: Double?,
    @SerializedName("user_ratings_total") val userRatingsTotal: Int?,
    @SerializedName("price_level") val priceLevel: Int?,
    @SerializedName("photos") val photos: List<Photo>?,
    @SerializedName("opening_hours") val openingHours: OpeningHours?
)

data class Geometry(@SerializedName("location") val location: Location?)
data class Location(@SerializedName("lat") val lat: Double?, @SerializedName("lng") val lng: Double?)
data class Photo(@SerializedName("photo_reference") val photoReference: String?)
data class OpeningHours(@SerializedName("open_now") val openNow: Boolean?)