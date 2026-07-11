package com.example.trendora

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trendora.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import com.example.trendora.network.UploadResponse
import com.bumptech.glide.Glide
import android.util.Log
import android.content.Intent
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileGridAdapter
    private lateinit var postsCount: TextView

    private lateinit var usernameText: TextView
    private lateinit var bioText: TextView

    private lateinit var profileImage: ImageView

    private val reelList = ArrayList<ProfileReel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImage = findViewById(R.id.profileImage)

        loadProfileImage()

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }
        profileImage.setOnClickListener {
            imagePicker.launch("image/*")
        }



        postsCount = findViewById(R.id.postsCount)

        usernameText = findViewById(R.id.txtUsername)
        bioText = findViewById(R.id.txtBio)

        recyclerView = findViewById(R.id.profileRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ProfileGridAdapter(this, reelList)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.editProfileBtn).setOnClickListener {

            val intent = Intent(
                this,
                EditProfileActivity::class.java
            )

            startActivity(intent)
        }

         loadProfile()


        loadReels()
    }


    override fun onResume() {
        super.onResume()

        loadProfile()
        loadProfileImage()
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
                            this@ProfileActivity,
                            "FROM API: $imageUrl",
                            Toast.LENGTH_LONG
                        ).show()



                        Toast.makeText(
                            this@ProfileActivity,
                            imageUrl ?: "Image URL is null",
                            Toast.LENGTH_LONG
                        ).show()

                        if (imageUrl != null) {

                            val prefs = getSharedPreferences("Trendora", MODE_PRIVATE)

                            prefs.edit()
                                .putString("profile_image", imageUrl)
                                .apply()

                            Glide.with(this@ProfileActivity)
                                .load(imageUrl)
                                .into(profileImage)

                        }

                        Toast.makeText(
                            this@ProfileActivity,
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
                        this@ProfileActivity,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        // Stop here. We'll continue in the next step.
    }

    private fun loadProfileImage() {

        val prefs = getSharedPreferences("Trendora", MODE_PRIVATE)

        val savedUrl = prefs.getString("profile_image", "")

        Log.d("PROFILE_URL", savedUrl ?: "No URL")

        Toast.makeText(
            this,
            savedUrl,
            Toast.LENGTH_LONG
        ).show()

        if (!savedUrl.isNullOrEmpty()) {

            Glide.with(this)
                .load(savedUrl)
                .placeholder(R.drawable.profile_demo)
                .error(R.drawable.profile_demo)
                .into(profileImage)

        } else {

            profileImage.setImageResource(R.drawable.profile_demo)

        }

    }

    private fun loadProfile() {

        RetrofitClient.apiService.getProfile()
            .enqueue(object : Callback<ProfileResponse> {

                override fun onResponse(
                    call: Call<ProfileResponse>,
                    response: Response<ProfileResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val profile = response.body()!!

                        usernameText.text = profile.username
                        bioText.text = profile.bio
                        Toast.makeText(
                            this@ProfileActivity,
                            "Profile image: ${profile.imageUrl}",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

                override fun onFailure(
                    call: Call<ProfileResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        "Failed to load profile",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
    }


    private fun loadReels() {

        val database = FirebaseDatabase.getInstance(
            "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("reels").child("reels")

        database.get().addOnSuccessListener { snapshot ->

            reelList.clear()

            for (child in snapshot.children) {

                Log.d("PROFILE", "KEY = ${child.key}")
                Log.d("PROFILE", "VALUE = ${child.value}")

                val reel = child.getValue(ProfileReel::class.java)

                if (reel?.videoUrl?.isNotEmpty() == true) {
                    reelList.add(reel)
                }
                Log.d("FIREBASE", "KEY = ${child.key}")
                Log.d("FIREBASE", "VALUE = ${child.value}")
                Log.d("FIREBASE", "OBJECT = $reel")

                Log.d(
                    "PROFILE",
                    "REEL = $reel"
                )

                if (reel != null) {

                    Log.d(
                        "PROFILE",
                        "REEL ADDED"
                    )

                    if (reel.ownerId == "user1") {
                        reelList.add(reel)
                    }
                } else {

                    Log.d(
                        "PROFILE",
                        "NULL OBJECT"
                    )
                }
            }

            adapter.notifyDataSetChanged()

            postsCount.text = reelList.size.toString()

        }.addOnFailureListener {

            Toast.makeText(
                this,
                "Failed to load reels",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}