package com.example.clurash.view.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clurash.data.datastore.SessionPreferences
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SessionPreferences) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}