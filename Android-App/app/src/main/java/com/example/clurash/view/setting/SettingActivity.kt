package com.example.clurash.view.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.clurash.R
import com.example.clurash.data.datastore.SessionPreferences
import com.example.clurash.databinding.ActivitySettingBinding
import com.example.clurash.view.ViewModelFactory
import com.example.clurash.view.login.MainActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupAction()
        setupActionBar()
    }

    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.setting)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupAction() {
        binding.apply {
            Logout.setOnClickListener {
                Toast.makeText(
                    this@SettingActivity,
                    resources.getString(R.string.logout_success),
                    Toast.LENGTH_SHORT
                ).show()
                settingViewModel.logout()
                val intent = Intent(this@SettingActivity, MainActivity::class.java)
                startActivity(intent)
                finishAffinity()
            }
        }
    }

    private fun setupViewModel() {
        settingViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreferences.getInstance(dataStore))
        )[SettingViewModel::class.java]
    }
}