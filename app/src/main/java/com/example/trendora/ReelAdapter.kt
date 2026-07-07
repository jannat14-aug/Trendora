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
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import com.google.android.material.button.MaterialButton
import android.widget.Toast

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

        val btnFollow: MaterialButton =
            itemView.findViewById(R.id.btnFollow)

        val bigHeart = itemView.findViewById<ImageView>(R.id.bigHeart)
        val musicDisc = itemView.findViewById<ImageView>(R.id.musicDisc)
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

    private fun showBigHeart(holder: ReelViewHolder) {

        holder.bigHeart.visibility = View.VISIBLE

        holder.bigHeart.scaleX = 0f
        holder.bigHeart.scaleY = 0f
        holder.bigHeart.alpha = 0f

        holder.bigHeart.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(180)
            .withEndAction {

                holder.bigHeart.animate()
                    .alpha(0f)
                    .scaleX(1.5f)
                    .scaleY(1.5f)
                    .setDuration(250)
                    .withEndAction {

                        holder.bigHeart.visibility = View.GONE
                        holder.bigHeart.alpha = 1f
                        holder.bigHeart.scaleX = 1f
                        holder.bigHeart.scaleY = 1f

                    }

            }

    }

    override fun onBindViewHolder(
        holder: ReelViewHolder,
        position: Int
    ) {

        val video = videoList[position]

        var liked = false
        val pref = holder.itemView.context.getSharedPreferences(
            "Trendora",
            android.content.Context.MODE_PRIVATE
        )

        var followed = pref.getBoolean(video.username, false)

        holder.btnFollow.text =
            if (followed) "Following" else "Follow"

        holder.username.text = video.username
        holder.caption.text = video.caption

        val player = ExoPlayer.Builder(holder.itemView.context).build()
        holder.playerView.player = player

        val mediaItem = MediaItem.fromUri(video.videoUrl)

        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        player.playWhenReady = true

        val rotate = ObjectAnimator.ofFloat(
            holder.musicDisc,
            "rotation",
            0f,
            360f
        )

        rotate.duration = 4500
        rotate.repeatCount = ValueAnimator.INFINITE
        rotate.interpolator = LinearInterpolator()
        rotate.start()

        val gestureDetector = GestureDetector(
            holder.itemView.context,
            object : GestureDetector.SimpleOnGestureListener() {

                override fun onDoubleTap(e: MotionEvent): Boolean {

                    showBigHeart(holder)

                    liked = true
                    holder.btnLike.setImageResource(R.drawable.ic_heart_filled)
                    holder.btnLike.setColorFilter(android.graphics.Color.RED)

                    return true
                }

            }
        )

        holder.playerView.setOnTouchListener { _, event ->

            gestureDetector.onTouchEvent(event)

            false
        }
        // Pause / Play

        holder.playerView.setOnClickListener {

            if (player.isPlaying) {

                player.pause()
                holder.playPauseIcon.visibility = View.VISIBLE

            } else {

                player.play()
                holder.playPauseIcon.visibility = View.GONE
            }
        }

        // LIKE BUTTON


        holder.btnLike.setOnClickListener {

            holder.btnLike.animate()
                .scaleX(1.3f)
                .scaleY(1.3f)
                .setDuration(120)
                .withEndAction {

                    holder.btnLike.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                }

            liked = !liked

            if (liked) {

                holder.btnLike.setImageResource(R.drawable.ic_heart_filled)
                holder.btnLike.setColorFilter(android.graphics.Color.RED)

            } else {

                holder.btnLike.setImageResource(R.drawable.ic_heart_outline)
                holder.btnLike.setColorFilter(android.graphics.Color.WHITE)
            }
        }

// COMMENT

        holder.btnComment.setOnClickListener {

            android.widget.Toast.makeText(
                holder.itemView.context,
                "Comments coming soon 💬",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }

// SHARE

        holder.btnShare.setOnClickListener {

            val intent = android.content.Intent(
                android.content.Intent.ACTION_SEND
            )

            intent.type = "text/plain"

            intent.putExtra(
                android.content.Intent.EXTRA_TEXT,
                video.videoUrl
            )

            holder.itemView.context.startActivity(
                android.content.Intent.createChooser(
                    intent,
                    "Share Reel"
                )
            )
        }
        holder.btnFollow.setOnClickListener {

            // Button animation
            holder.btnFollow.animate()
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(120)
                .withEndAction {
                    holder.btnFollow.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(120)
                }

            followed = !followed

            if (followed) {

                val pref = holder.itemView.context.getSharedPreferences(
                    "Trendora",
                    android.content.Context.MODE_PRIVATE
                )

                var followers = pref.getInt("followers", 0)
                var following = pref.getInt("following", 0)
                followers++
                following++

                pref.edit()
                    .putBoolean(video.username, true)
                    .putInt("followers", followers)
                    .putInt("following", following)
                    .apply()

                holder.btnFollow.text = "Following"

                Toast.makeText(
                    holder.itemView.context,
                    "Followed ${video.username}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val pref = holder.itemView.context.getSharedPreferences(
                    "Trendora",
                    android.content.Context.MODE_PRIVATE
                )

                var followers = pref.getInt("followers", 0)
                var following = pref.getInt("following", 0)

                if (followers > 0) {
                    followers--
                }
                if (following > 0) {
                    following--
                }
                pref.edit()
                    .putBoolean(video.username, false)
                    .putInt("followers", followers)
                    .putInt("following", following)
                    .apply()

                holder.btnFollow.text = "Follow"

                Toast.makeText(
                    holder.itemView.context,
                    "Unfollowed ${video.username}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onViewDetachedFromWindow(
        holder: ReelViewHolder
    ) {
        super.onViewDetachedFromWindow(holder)

        holder.playerView.player?.pause()
        holder.playerView.player?.release()
        holder.playerView.player = null
    }

    override fun onViewAttachedToWindow(
        holder: ReelViewHolder
    ) {
        super.onViewAttachedToWindow(holder)

        holder.playerView.player?.play()
    }
}


