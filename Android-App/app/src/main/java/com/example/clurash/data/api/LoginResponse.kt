package com.example.clurash.data.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("username")
	val username: String
)
