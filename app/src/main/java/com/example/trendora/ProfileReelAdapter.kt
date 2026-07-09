package com.example.trendora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.RecyclerView

class ProfileReelAdapter(
    private val reelList: ArrayList<ProfileReel>
) : RecyclerView.Adapter<ProfileReelAdapter.ReelViewHolder>() {

    inner class ReelViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val playerView: PlayerView =
            itemView.findViewById(R.id.playerView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ReelViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_profile_reel,
                parent,
                false
            )

        return ReelViewHolder(view)
    }

    override fun getItemCount(): Int = reelList.size

    override fun onBindViewHolder(
        holder: ReelViewHolder,
        position: Int
    ) {

        val reel = reelList[position]

        val player = ExoPlayer.Builder(
            holder.itemView.context
        ).build()

        holder.playerView.player = player

        val mediaItem = MediaItem.fromUri(
            reel.videoUrl
        )

        player.setMediaItem(mediaItem)

        player.repeatMode = Player.REPEAT_MODE_ONE
        player.volume = 0f
        player.prepare()
        player.play()

    }

    override fun onViewRecycled(holder: ReelViewHolder) {
        super.onViewRecycled(holder)

        holder.playerView.player?.release()
    }
}