package com.example.clurash.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.clurash.data.datastore.SessionPreferences
import com.example.clurash.view.login.LoginViewModel
import com.example.clurash.view.setting.SettingViewModel


class ViewModelFactory(private val pref: SessionPreferences) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(SettingViewModel::class.java) -> {
                SettingViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}