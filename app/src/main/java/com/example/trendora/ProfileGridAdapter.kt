package com.example.trendora

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.Log
import androidx.media3.ui.PlayerView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player

class ProfileGridAdapter(
    private val context: Context,
    private val reelList: ArrayList<ProfileReel>
) : RecyclerView.Adapter<ProfileGridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val playerView: PlayerView =
            itemView.findViewById(R.id.playerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_reel, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {

        Log.d(
            "PROFILE",
            "TOTAL REELS = ${reelList.size}"
        )

        return reelList.size
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val player = ExoPlayer.Builder(context).build()

        holder.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            reelList[position].videoUrl
        )

        player.setMediaItem(mediaItem)
        player.volume = 0f
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.play()

        holder.itemView.setOnClickListener {

            val url = reelList[position].videoUrl

            if (url.isNotEmpty()) {

                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )

                context.startActivity(intent)

            }
        }


    }
}