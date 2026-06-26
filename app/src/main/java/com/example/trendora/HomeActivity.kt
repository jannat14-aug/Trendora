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
        val uploadBtn = findViewById<FloatingActionButton>(R.id.uploadBtn)

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

            startActivity(
                Intent(
                    this,
                    UploadActivity::class.java
                )
            )

        }

    }

}