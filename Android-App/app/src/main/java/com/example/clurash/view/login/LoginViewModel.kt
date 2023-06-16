package com.example.clurash.view.login

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.clurash.data.api.ConfigApi
import com.example.clurash.data.api.LoginResponse
import com.example.clurash.data.datastore.SessionModel
import com.example.clurash.data.datastore.SessionPreferences

data class LoginRequest(
    val email: String,
    val password: String
)
class LoginViewModel(private val pref: SessionPreferences) : ViewModel() {
    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseStatus = MutableLiveData<String>()
    val responseStatus: LiveData<String> = _responseStatus

    private val _state = MutableLiveData<Boolean>()
    val state: LiveData<Boolean> = _state



    fun postLogin(email: String, password: String): LoginResponse? {
        _isLoading.value = true
        _state.value = false

        val loginResponse: LoginResponse? = null
        val request = LoginRequest(email,password)
        val client = ConfigApi.getApiService().postLogin(request)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _state.value = true
                    _login.value = response.body()
                    _responseStatus.value = response.body()?.message


                    viewModelScope.launch {
                        response.body()?.username?.let { pref.login(it) }
                    }
                } else {
                    val jsonError = response.errorBody()?.string()?.let { JSONObject(it) }
                    _responseStatus.value = jsonError?.getString("error")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _responseStatus.value = t.message
            }
        })

        return loginResponse
    }

    fun getPrefData(): LiveData<SessionModel> {
        return pref.getData().asLiveData()
    }
}