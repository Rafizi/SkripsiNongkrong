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
    val geometry: Geometry?
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


