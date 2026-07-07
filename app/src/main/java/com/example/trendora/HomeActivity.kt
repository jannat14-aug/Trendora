package com.example.trendora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import android.view.Gravity
import android.widget.PopupWindow
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import com.example.trendora.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private var selectedVideoUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {

                selectedVideoUri = uri

                Toast.makeText(
                    this,
                    "Video Selected ✔",
                    Toast.LENGTH_SHORT
                ).show()

                uploadVideo(uri)
                // Next step: Upload the video to FastAPI
            }
        }

    private fun uploadVideo(uri: Uri) {

        val inputStream = contentResolver.openInputStream(uri)

        val file = File(
                cacheDir,
        "video_${System.currentTimeMillis()}.mp4"
        )

        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        val requestFile =
            file.asRequestBody("video/mp4".toMediaTypeOrNull())

        val body =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

        RetrofitClient.apiService.uploadVideo(body)
            .enqueue(object : Callback<com.example.trendora.network.UploadResponse> {

                override fun onResponse(
                    call: Call<com.example.trendora.network.UploadResponse>,
                    response: Response<com.example.trendora.network.UploadResponse>
                ) {

                    if (response.isSuccessful) {

                        if (response.isSuccessful && response.body() != null) {

                            val fileName = response.body()!!.filename

                            val videoUrl = "http://10.190.29.74:8000/uploads/$fileName"

                            val prefs = getSharedPreferences("Trendora", MODE_PRIVATE)

                            prefs.edit()
                                .putString("uploaded_reel", videoUrl)
                                .apply()

                            Toast.makeText(
                                this@HomeActivity,
                                "🎉 Reel Uploaded Successfully",
                                Toast.LENGTH_LONG
                            ).show()

                        } else {

                            Toast.makeText(
                                this@HomeActivity,
                                "Upload Failed",
                                Toast.LENGTH_LONG
                            ).show()

                        }

                    } else {

                        Toast.makeText(
                            this@HomeActivity,
                            "Upload Failed",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

                override fun onFailure(
                    call: Call<com.example.trendora.network.UploadResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@HomeActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()

                }

            })

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ViewPager
        viewPager = findViewById(R.id.viewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL


        // Load reels.json
        val reader = InputStreamReader(assets.open("reels.json"))

        val type = object : TypeToken<ArrayList<VideoModel>>() {}.type

        val videoList: ArrayList<VideoModel> =
            Gson().fromJson(reader, type)

        viewPager.adapter = ReelAdapter(videoList)

        // Bottom Navigation
        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        val profileBtn = findViewById<ImageView>(R.id.profileBtn)
        val uploadBtn = findViewById<androidx.cardview.widget.CardView>(R.id.uploadBtn)


        homeBtn.setOnClickListener {

            if (viewPager.currentItem != 0) {
                viewPager.setCurrentItem(0, true)
            }

        }

        profileBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )

        }

        uploadBtn.setOnClickListener {

            val popupView = LayoutInflater.from(this)
                .inflate(R.layout.popup_upload, null)

            val popup = PopupWindow(
                popupView,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                true
            )

            popup.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popup.isOutsideTouchable = true
            popup.elevation = 20f

            val cameraOption =
                popupView.findViewById<LinearLayout>(R.id.cameraOption)

            val galleryOption =
                popupView.findViewById<LinearLayout>(R.id.galleryOption)

            cameraOption.setOnClickListener {

                it.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80)
                    .withEndAction {

                        it.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)

                        val closeAnim = android.view.animation.AnimationUtils.loadAnimation(
                            this,
                            R.anim.popup_close
                        )

                        popupView.startAnimation(closeAnim)

                        popupView.postDelayed({

                            popup.dismiss()

                        },170)

                        Toast.makeText(
                            this,
                            "Camera",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

            }
            galleryOption.setOnClickListener {

                it.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80)
                    .withEndAction {

                        it.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(80)

                        val closeAnim = android.view.animation.AnimationUtils.loadAnimation(
                            this,
                            R.anim.popup_close
                        )

                        popupView.startAnimation(closeAnim)

                        popupView.postDelayed({

                            popup.dismiss()

                            galleryLauncher.launch("video/*")

                        },170)

                    }

            }

            val animation = android.view.animation.AnimationUtils.loadAnimation(
                this,
                R.anim.popup_open
            )

            popupView.startAnimation(animation)

            popup.showAsDropDown(
                uploadBtn,
                -110,
                -345
            )
        }
    }

}