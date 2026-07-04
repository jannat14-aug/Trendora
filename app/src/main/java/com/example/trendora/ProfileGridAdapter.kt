package com.example.trendora

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ProfileGridAdapter(
    private val context: Context,
    private val reelList: ArrayList<ProfileReel>
) : RecyclerView.Adapter<ProfileGridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_reel, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int = reelList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.thumbnail.setImageResource(R.drawable.ic_launcher_foreground)

        holder.itemView.setOnClickListener {

            val intent = Intent(Intent.ACTION_VIEW)

            intent.setDataAndType(
                Uri.parse(reelList[position].videoUrl),
                "video/*"
            )

            context.startActivity(intent)

        }
    }
}