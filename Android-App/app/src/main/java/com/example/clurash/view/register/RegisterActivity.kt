package com.example.clurash.view.register

import android.content.Context
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import com.example.clurash.databinding.ActivityRegisterBinding
import com.example.clurash.view.login.MainActivity
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

class RegisterActivity : AppCompatActivity() {
    private  lateinit var binding: ActivityRegisterBinding
    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
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

    private fun setupAction() {
        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            registerViewModel.postRegister(username,email, password)
        }

        registerViewModel.responseStatus.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        registerViewModel.isLoading.observe(this, ::showLoading)

        registerViewModel.state.observe(this) {
            if (it) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            card.visibility = if (!isLoading) View.VISIBLE else View.INVISIBLE
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
}