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

class ProfileActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProfileGridAdapter
    private lateinit var postsCount: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView

    private val reelList = ArrayList<ProfileReel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        postsCount = findViewById(R.id.postsCount)
        followersCount = findViewById(R.id.followersCount)
        followingCount = findViewById(R.id.followingCount)

        recyclerView = findViewById(R.id.profileRecycler)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        adapter = ProfileGridAdapter(this, reelList)
        recyclerView.adapter = adapter

        loadReels()
        val pref = getSharedPreferences("Trendora", MODE_PRIVATE)

        followersCount.text =
            pref.getInt("followers", 0).toString()

        followingCount.text =
            pref.getInt("following", 0).toString()
    }

    private fun loadReels() {

        RetrofitClient.apiService.getMyReels()
            .enqueue(object : Callback<ArrayList<ProfileReel>> {

                override fun onResponse(
                    call: Call<ArrayList<ProfileReel>>,
                    response: Response<ArrayList<ProfileReel>>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        reelList.clear()
                        reelList.addAll(response.body()!!)

                        adapter.notifyDataSetChanged()

                        postsCount.text = reelList.size.toString()

                    }

                }

                override fun onFailure(
                    call: Call<ArrayList<ProfileReel>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@ProfileActivity,
                        "Failed to load reels",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })

    }
    override fun onResume() {
        super.onResume()

        val pref = getSharedPreferences("Trendora", MODE_PRIVATE)

        followersCount.text = pref.getInt("followers", 0).toString()
        followingCount.text = pref.getInt("following", 0).toString()
    }
}