package com.example.clurash.view.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clurash.data.api.ConfigApi
import com.example.clurash.data.api.PointResponse
import com.example.clurash.data.api.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

data class UpdatePoint(
    val username: String
)
class PredictViewModel: ViewModel() {
    private val _responseClass = MutableLiveData<String>()
    val responseClass: LiveData<String> = _responseClass

    private val _responseAcc = MutableLiveData<String>()
    val responseAcc: LiveData<String> = _responseAcc
    private val _responseStatus = MutableLiveData<String>()
    val responseStatus: LiveData<String> = _responseStatus

    fun postPredictTrash(image : MultipartBody.Part){
        val client = ConfigApi.getApiService().postPredict(image)
        client.enqueue(object : retrofit2.Callback<PredictResponse> {
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {

                if (response.isSuccessful) {

                    _responseClass.value = response.body()?.className
                    _responseAcc.value = response.body()?.confidenceScore.toString()

                } else {

                }
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {

            }
        })
    }
    fun updatepoint(username:String){
        val updateResponse: PointResponse? = null
        val request = UpdatePoint(username)
        val client = ConfigApi.getApiService().updatepoint(request)
        client.enqueue(object : Callback<PointResponse>{
            override fun onResponse(call: Call<PointResponse>, response: Response<PointResponse>) {
                if (response.isSuccessful){
                    _responseStatus.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<PointResponse>, t: Throwable) {
                _responseStatus.value = t.message.toString()
            }

        })
    }
}