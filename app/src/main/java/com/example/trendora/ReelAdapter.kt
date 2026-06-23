package com.example.trendora

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView

class ReelAdapter(
    private val videoList: ArrayList<VideoModel>
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val playerView: PlayerView =
            itemView.findViewById(R.id.playerView)

        val username: TextView =
            itemView.findViewById(R.id.txtUsername)

        val caption: TextView =
            itemView.findViewById(R.id.txtCaption)

        val playPauseIcon: ImageView =
            itemView.findViewById(R.id.playPauseIcon)

        val btnLike: ImageView =
            itemView.findViewById(R.id.btnLike)

        val btnComment: ImageView =
            itemView.findViewById(R.id.btnComment)

        val btnShare: ImageView =
            itemView.findViewById(R.id.btnShare)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReelViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.reel_item,
                parent,
                false
            )

        return ReelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    override fun onBindViewHolder(
        holder: ReelViewHolder,
        position: Int
    ) {

        val video = videoList[position]

        holder.username.text = video.username
        holder.caption.text = video.caption

        val player = ExoPlayer.Builder(
            holder.itemView.context
        ).build()

        holder.playerView.player = player

        player.addListener(object : Player.Listener {

            override fun onPlayerError(
                error: PlaybackException
            ) {
                Log.e(
                    "TRENDORA",
                    "Video Error: ${error.message}"
                )
            }
        })

        val mediaItem = MediaItem.fromUri(
            "android.resource://${holder.itemView.context.packageName}/${video.videoResId}"
        )

        player.setMediaItem(mediaItem)

        player.repeatMode =
            ExoPlayer.REPEAT_MODE_ALL

        player.prepare()

        player.playWhenReady = true

        Log.d("TRENDORA", "Video loading")

        holder.playerView.setOnClickListener {

            if (player.isPlaying) {

                player.pause()

                holder.playPauseIcon.visibility =
                    View.VISIBLE

            } else {

                player.play()

                holder.playPauseIcon.visibility =
                    View.GONE
            }
        }
        holder.btnLike.setOnClickListener {

            android.widget.Toast.makeText(
                holder.itemView.context,
                "Liked ❤️",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        holder.btnComment.setOnClickListener {

            android.widget.Toast.makeText(
                holder.itemView.context,
                "Comments 💬",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

        holder.btnShare.setOnClickListener {

            android.widget.Toast.makeText(
                holder.itemView.context,
                "Share ↗",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
}