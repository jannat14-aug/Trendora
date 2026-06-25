package com.example.trendora

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class HomeActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        viewPager = findViewById(R.id.viewPager)
        viewPager.orientation= ViewPager2.ORIENTATION_VERTICAL

        val json = assets.open("reels.json")

        val reader = InputStreamReader(json)

        val type = object : TypeToken<ArrayList<VideoModel>>() {}.type

        val videoList: ArrayList<VideoModel> =
            Gson().fromJson(reader, type)



        viewPager.adapter =
            ReelAdapter(videoList)

        val profileBtn =
            findViewById<ImageView>(R.id.profileBtn)

        val uploadBtn =
            findViewById<ImageView>(R.id.uploadBtn)

        profileBtn.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            )
        }

        uploadBtn.setOnClickListener {

            Toast.makeText(
                this,
                "Upload Coming Soon 🚀",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}