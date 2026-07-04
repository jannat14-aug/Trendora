package com.example.trendora

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ProfileReelAdapter(
    private val reelList: ArrayList<ProfileReel>
) : RecyclerView.Adapter<ProfileReelAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val reelImage: ImageView =
            itemView.findViewById(R.id.reelImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReelViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_reel, parent, false)

        return ReelViewHolder(view)
    }

    override fun getItemCount(): Int = reelList.size

    override fun onBindViewHolder(
        holder: ReelViewHolder,
        position: Int
    ) {

        val reel = reelList[position]

        try {

            val retriever = MediaMetadataRetriever()

            retriever.setDataSource(reel.videoUrl)
            val bitmap: Bitmap? =
                retriever.frameAtTime

            holder.reelImage.setImageBitmap(bitmap)

            retriever.release()

        } catch (e: Exception) {

            holder.reelImage.setImageResource(R.drawable.profile_demo)

        }

    }
}