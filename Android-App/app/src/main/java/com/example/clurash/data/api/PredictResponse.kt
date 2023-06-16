package com.example.clurash.data.api

import com.google.gson.annotations.SerializedName

data class PredictResponse(

	@field:SerializedName("confidence_score")
	val confidenceScore: String,

	@field:SerializedName("class_name")
	val className: String
)
