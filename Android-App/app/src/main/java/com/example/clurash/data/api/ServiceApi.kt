package com.example.clurash.data.api

import com.example.clurash.view.home.UpdatePoint
import com.example.clurash.view.login.LoginRequest
import com.example.clurash.view.register.RegistrationRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)
data class UpdatePoint(
    val username: String
)
interface ServiceApi {
    @POST("register")
    fun postRegister(
        @Body request: RegistrationRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun postLogin(
        @Body request: LoginRequest
    ): Call<LoginResponse>
    @PUT("update-points")
    fun updatepoint(
        @Body request: UpdatePoint
    ): Call<PointResponse>
    @Multipart
    @POST("predict")
    fun postPredict(
        @Part image: MultipartBody.Part
    ):Call<PredictResponse>
    @GET("points")
    fun getPoints(
        @Query("username") username: String
    ): Call<GetPointResponse>
}