package com.example.clurash.view.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.clurash.data.api.ConfigApi
import com.example.clurash.data.api.PredictResponse
import com.example.clurash.data.api.RegisterResponse
import com.example.clurash.data.datastore.SessionPreferences
import com.example.clurash.databinding.ActivityPredictBinding
import com.example.clurash.view.login.dataStore
import com.example.clurash.view.register.RegisterViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class PredictActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var binding: ActivityPredictBinding
   var username:String=""
    private val viewModels by viewModels<PredictViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnCamera.setOnClickListener {
            requestCameraPermission()
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

    }


    private val CAMERA_PERMISSION_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 200

    // Inside your activity or fragment
    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted
            openCamera()
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                openCamera()
            } else {
                // Permission denied, show an error message or handle it gracefully
            }
        }
    }

    // Open the camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Load the full-sized image from the file
            val photoFile = data?.extras?.get("data") as Bitmap?

            binding.btnPredict.setOnClickListener{
                Toast.makeText(this@PredictActivity, "loading", Toast.LENGTH_SHORT).show()
                photoFile?.let { it1 -> bitmapToRequestBody(it1) }?.let { it2 -> sendPredict(it2) }
            }

            binding.imgView.setImageBitmap(photoFile)

        }
    }

    private fun bitmapToRequestBody(bitmap: Bitmap): RequestBody {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()

        return RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
    }

    private fun sendPredict(imageRequestBody: RequestBody) {


        val imagePart = MultipartBody.Part.createFormData("image", "image.jpg", imageRequestBody)

        viewModels.postPredictTrash(imagePart)

        viewModels.responseAcc.observe(this){ acc ->
            val formattedAcc = String.format("%.2f", acc.toDouble())
            binding.acc.text = "Accuracy: $formattedAcc"
        }

        viewModels.responseClass.observe(this){ class_name ->
            binding.catTrash.text = "Trash Category : $class_name"
        }
        val sessionPreferences = SessionPreferences.getInstance(dataStore)
        lifecycleScope.launch{
            username = sessionPreferences.getData().first().username
            viewModels.updatepoint(username)
            Toast.makeText(this@PredictActivity, "Point Updated", Toast.LENGTH_SHORT).show()
        }
    }

}