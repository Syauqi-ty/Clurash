package com.example.clurash.view.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.clurash.R
import com.example.clurash.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.home -> replaceFragment(HomeFragment())
                R.id.camera -> replaceFragment(CameraFragment())
                R.id.article -> replaceFragment(ArticleFragment())

                else -> {

                }
            }
            true
        }
    }

     private fun replaceFragment(fragment: Fragment){

         val fragmentManager = supportFragmentManager
         val fragmentTransaction = fragmentManager.beginTransaction()
         fragmentTransaction.replace(R.id.frameLayout, fragment)
         fragmentTransaction.commit()

     }
}