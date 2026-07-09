package com.example.trendora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import com.example.trendora.network.UploadResponse
class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private var selectedVideoUri: Uri? = null

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->

            if (uri != null) {

                val intent = Intent(this, UploadActivity::class.java)
                intent.putExtra("videoUri", uri.toString())
                startActivity(intent)

            }

        }
    private fun uploadVideo(uri: Uri) {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ViewPager
        viewPager = findViewById(R.id.viewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.offscreenPageLimit=1


        val videoList = ArrayList<VideoModel>()

        val adapter = ReelAdapter(videoList)
        viewPager.adapter = adapter

        viewPager.offscreenPageLimit = 1

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.playVideoAt(position)
            }
        })
        //viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        //
        //            override fun onPageSelected(position: Int) {
        //                super.onPageSelected(position)
        //
        //                (viewPager.adapter as ReelAdapter).playVideoAt(position)
        //            }
        //
        //        })

        val database = FirebaseDatabase.getInstance(
            "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("reels").child("reels")

        database.get().addOnSuccessListener { snapshot ->

            videoList.clear()

            android.util.Log.d("FIREBASE", "Children = ${snapshot.childrenCount}")

            for (child in snapshot.children) {

                android.util.Log.d("FIREBASE", "KEY = ${child.key}")
                android.util.Log.d("FIREBASE", "VALUE = ${child.value}")

                val reel = child.getValue(VideoModel::class.java)

                if (
                    reel != null &&
                    reel.videoUrl.isNotEmpty()
                ) {
                    videoList.add(reel)
                } else {
                    android.util.Log.d("FIREBASE", "NULL OBJECT")
                }
            }

            Toast.makeText(
                this,
                "Loaded ${videoList.size} reels",
                Toast.LENGTH_LONG
            ).show()

            viewPager.adapter?.notifyDataSetChanged()

        }.addOnFailureListener {

            Toast.makeText(
                this,
                "Firebase Error: ${it.message}",
                Toast.LENGTH_LONG
            ).show()
        }

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