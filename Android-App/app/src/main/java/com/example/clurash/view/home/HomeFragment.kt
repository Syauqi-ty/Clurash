package com.example.clurash.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.clurash.data.api.ConfigApi
import com.example.clurash.data.api.GetPointResponse
import com.example.clurash.data.datastore.SessionPreferences
import com.example.clurash.databinding.FragmentHomeBinding
import com.example.clurash.view.ViewModelFactory
import com.example.clurash.view.login.LoginViewModel
import com.example.clurash.view.login.dataStore
import com.example.clurash.view.setting.SettingActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginViewModel: LoginViewModel
    var username: String =""
    private val _responseStatus = MutableLiveData<String>()
    val responseStatus: LiveData<String> = _responseStatus

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupViewModel()

        loginViewModel.getPrefData().observe(requireActivity()){

            val client = ConfigApi.getApiService().getPoints(it.username)
            client.enqueue(object: Callback<GetPointResponse>{
                override fun onResponse(
                    call: Call<GetPointResponse>,
                    response: Response<GetPointResponse>
                ) {
                    binding.resultPoint.text = response.body()?.points.toString()
                }

                override fun onFailure(call: Call<GetPointResponse>, t: Throwable) {
                    _responseStatus.value=t.message.toString()
                }

            })
            binding.tvUsername.text = it.username
        }
        binding.btnSetting.setOnClickListener {
            val intent = Intent (getActivity(), SettingActivity::class.java)
            getActivity()?.startActivity(intent)
        }
        return binding.root
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreferences.getInstance(requireContext().dataStore))
        )[LoginViewModel::class.java]
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}