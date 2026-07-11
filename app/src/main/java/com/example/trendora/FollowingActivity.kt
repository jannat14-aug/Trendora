package com.example.trendora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class FollowingActivity : AppCompatActivity() {

    private lateinit var followingRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        followingRecycler = findViewById(R.id.followingRecycler)
        followingRecycler.layoutManager = LinearLayoutManager(this)

        val followingList = ArrayList<FollowingModel>()
        val adapter = FollowingAdapter(followingList)
        followingRecycler.adapter = adapter

        val database = FirebaseDatabase.getInstance(
            "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        val pref = getSharedPreferences("Trendora", MODE_PRIVATE)

        database.get().addOnSuccessListener { snapshot ->

            followingList.clear()

            for (child in snapshot.children) {

                val uid = child.key ?: ""

                val username = child.child("username").getValue(String::class.java) ?: ""

                val profileImage =
                    child.child("profileImage").getValue(String::class.java) ?: ""

                if (pref.getBoolean(username, false)) {

                    followingList.add(
                        FollowingModel(
                            uid,
                            username,
                            profileImage
                        )
                    )
                }
            }

            adapter.notifyDataSetChanged()
        }
    }
}