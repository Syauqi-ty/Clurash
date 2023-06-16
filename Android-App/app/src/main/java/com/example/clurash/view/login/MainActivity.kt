package com.example.clurash.view.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.clurash.data.datastore.SessionPreferences
import com.example.clurash.databinding.ActivityMainBinding
import com.example.clurash.view.ViewModelFactory
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.example.clurash.view.home.HomeActivity
import com.example.clurash.view.register.RegisterActivity

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityMainBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setupView()
        setupViewModel()
        setupAction()

        loginViewModel.getPrefData().observe(this){
            if (it.username.isNotEmpty()) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        currentFocus?.let {
            val closeKeyboard: InputMethodManager = getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as (InputMethodManager)
            closeKeyboard.hideSoftInputFromWindow(it.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            card.visibility = if (!isLoading) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            loginViewModel.postLogin(email, password)

            loginViewModel.state.observe(this) {
                if (it) {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        loginViewModel.responseStatus.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.isLoading.observe(this, ::showLoading)

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(SessionPreferences.getInstance(dataStore))
        )[LoginViewModel::class.java]
    }
}