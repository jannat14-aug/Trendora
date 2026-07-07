package com.example.trendora

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.trendora.network.RetrofitClient
import com.example.trendora.network.UploadResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var btnSave: Button
    private lateinit var etUsername: EditText
    private lateinit var etBio: EditText

    private lateinit var profileImage: ImageView
    private lateinit var changePhoto: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_edit_profile)

        profileImage = findViewById(R.id.profileImage)

        changePhoto=findViewById(R.id.changePhoto)

        changePhoto.setOnClickListener {
            imagePicker.launch("image/*")
        }


        backBtn = findViewById(R.id.backBtn)
        btnSave = findViewById(R.id.btnSave)
        etUsername = findViewById(R.id.etUsername)
        etBio = findViewById(R.id.etBio)

        backBtn.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val bio = etBio.text.toString().trim()

            if (username.isEmpty() || bio.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val profile = ProfileRequest(
                username = username,
                bio = bio
            )

            RetrofitClient.apiService.saveProfile(profile)
                .enqueue(object : Callback<UploadResponse> {

                    override fun onResponse(
                        call: Call<UploadResponse>,
                        response: Response<UploadResponse>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@EditProfileActivity,
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@EditProfileActivity,
                                "Update failed: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                    override fun onFailure(
                        call: Call<UploadResponse>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                })
        }
    }

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

            if (uri != null) {
                profileImage.setImageURI(uri)
                uploadProfileImage(uri)
            }

        }

    private fun uploadProfileImage(uri: Uri) {

        val inputStream = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "profile.jpg")

        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }


        val requestBody =
            file.asRequestBody("image/*".toMediaTypeOrNull())

        val imagePart =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestBody
            )

        RetrofitClient.apiService.uploadProfile(imagePart)
            .enqueue(object : Callback<UploadResponse> {

                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val imageUrl = response.body()!!.imageUrl

                        Toast.makeText(
                            this@EditProfileActivity,
                            imageUrl ?: "Image URL is null",
                            Toast.LENGTH_LONG
                        ).show()

                        if (imageUrl != null) {

                            val prefs = getSharedPreferences("Trendora", MODE_PRIVATE)

                            prefs.edit()
                                .putString("profile_image", imageUrl)
                                .commit()

                        }

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile uploaded successfully!",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(
                    call: Call<UploadResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@EditProfileActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        // Stop here. We'll continue in the next step.
    }
}