package com.example.clurash.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.json.JSONObject
import retrofit2.Call
import androidx.lifecycle.ViewModel
import com.example.clurash.data.api.RegisterResponse
import retrofit2.Callback
import retrofit2.Response
import com.example.clurash.data.api.ConfigApi


data class RegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)
class RegisterViewModel : ViewModel() {
    private val _responseStatus = MutableLiveData<String>()
    val responseStatus: LiveData<String> = _responseStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean> = _state

    fun postRegister(username: String, email: String, password: String): RegisterResponse? {
        _isLoading.value = true
        _state.value = false

        val registerResponse: RegisterResponse? = null
        var request = RegistrationRequest(username, email, password)
        val client = ConfigApi.getApiService().postRegister(request)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _state.value = true
                    _responseStatus.value = response.body()?.message
                } else {
                    val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }
                    _responseStatus.value = jsonError?.getString("error")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _responseStatus.value = t.message.toString()
            }
        })
        return registerResponse
    }
}