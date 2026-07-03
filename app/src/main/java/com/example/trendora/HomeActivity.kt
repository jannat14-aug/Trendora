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

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

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

                popup.dismiss()

                Toast.makeText(
                    this,
                    "Camera",
                    Toast.LENGTH_SHORT
                ).show()
            }

            galleryOption.setOnClickListener {

                popup.dismiss()

                Toast.makeText(
                    this,
                    "Gallery",
                    Toast.LENGTH_SHORT
                ).show()
            }

            popup.showAsDropDown(
                uploadBtn,
                -75,
                -316,
                Gravity.CENTER
            )
        }
    }

}