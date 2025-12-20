package com.example.skripsinongkrong.data.model

import com.google.gson.annotations.SerializedName

data class PlacesApiResponse(
    @SerializedName("result")
    val result: PlaceResult?,

    @SerializedName("status")
    val status: String
)

data class PlaceResult(
    @SerializedName("name")
    val name: String?,

    @SerializedName("formatted_address")
    val formattedAddress: String?,

    @SerializedName("geometry")
    val geometry: Geometry?,

    @SerializedName("rating")
    val rating: Double?, // Kriteria TOPSIS: Benefit

    @SerializedName("user_ratings_total")
    val userRatingsTotal: Int?, // Kriteria TOPSIS: Benefit (Popularitas)

    @SerializedName("price_level")
    val priceLevel: Int?, // Kriteria TOPSIS: Cost (0-4)

    // --- DATA VISUAL & OPERASIONAL ---

    @SerializedName("photos")
    val photos: List<Photo>?,

    @SerializedName("opening_hours")
    val openingHours: OpeningHours?
)

data class Geometry(
    @SerializedName("location")
    val location: PlaceLocation?
)

data class PlaceLocation(
    @SerializedName("lat")
    val lat: Double,

    @SerializedName("lng")
    val lng: Double
)

data class Photo(
    @SerializedName("photo_reference")
    val photoReference: String?
)

data class OpeningHours(
    @SerializedName("open_now")
    val openNow: Boolean?
)
