package com.example.clurash.data.api

import com.google.gson.annotations.SerializedName
data class GetPointResponse (
    @field:SerializedName("points")
    val points: Int,

    @field:SerializedName("username")
    val username: String
)