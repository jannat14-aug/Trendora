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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.Glide

class ReelAdapter(
    private val videoList: ArrayList<VideoModel>
) : RecyclerView.Adapter<ReelAdapter.ReelViewHolder>() {

    private val players = HashMap<Int, ExoPlayer>()
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

        val btnSave: ImageView =
            itemView.findViewById(R.id.btnSave)

        val txtComments: TextView =
            itemView.findViewById(R.id.txtComments)

        val btnShare: ImageView =
            itemView.findViewById(R.id.btnShare)

        val bigHeart = itemView.findViewById<ImageView>(R.id.bigHeart)
        val musicDisc = itemView.findViewById<ImageView>(R.id.musicDisc)

        val profileImage =
            itemView.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profileImage)

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

        holder.username.text = video.username
        holder.caption.text = video.caption

        val prefs = holder.itemView.context.getSharedPreferences(
            "Trendora",
            android.content.Context.MODE_PRIVATE
        )

        val imageUrl = prefs.getString("profile_image", null)

        if (!imageUrl.isNullOrEmpty()) {

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.profile_demo)
                .error(R.drawable.profile_demo)
                .into(holder.profileImage)

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.music)
                .error(R.drawable.music)
                .circleCrop()
                .into(holder.musicDisc)

        }

        val database = FirebaseDatabase.getInstance(
            "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        database.child(position.toString())
            .child("comments")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    holder.txtComments.text = snapshot.childrenCount.toString()
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        val player = ExoPlayer.Builder(holder.itemView.context).build()
        holder.playerView.player = player
        players[position] = player
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

            val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(
                holder.itemView.context
            )

            val view = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.comment_bottom_sheet, null)

            dialog.setContentView(view)


            val etComment = view.findViewById<android.widget.EditText>(R.id.etComment)
            val btnSend = view.findViewById<android.widget.Button>(R.id.btnSend)

            val commentRecycler =
                view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.commentRecycler)

            val commentList = ArrayList<CommentModel>()

            val commentAdapter = CommentAdapter(commentList)

            commentRecycler.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(holder.itemView.context)

            commentRecycler.adapter = commentAdapter


            val database = FirebaseDatabase.getInstance(
                "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app"
            ).reference

            val currentPosition = holder.bindingAdapterPosition

            database.child(currentPosition.toString())
                .child("comments")
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        commentList.clear()

                        for (commentSnapshot in snapshot.children) {

                            val comment =
                                commentSnapshot.getValue(CommentModel::class.java)

                            if (comment != null) {
                                commentList.add(comment)
                            }
                        }

                        commentAdapter.notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            btnSend.setOnClickListener {

                android.widget.Toast.makeText(holder.itemView.context,"Send Button Clicked",
                    android.widget.Toast.LENGTH_SHORT).show()

                val comment = etComment.text.toString().trim()

                if (comment.isNotEmpty()) {

                    val currentPosition = holder.bindingAdapterPosition

                    if (currentPosition == androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                        return@setOnClickListener
                    }

                    val database = FirebaseDatabase.getInstance("https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

                    val commentData = CommentModel(
                        username = "@trendora",
                        comment = comment
                    )

                    database.child(currentPosition.toString())
                        .child("comments")
                        .push()
                        .setValue(commentData)
                        .addOnSuccessListener {

                            commentList.add(commentData)
                            commentAdapter.notifyItemInserted(commentList.size - 1)
                            etComment.text.clear()

                            android.widget.Toast.makeText(
                                holder.itemView.context,
                                "Comment added",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener {

                            android.widget.Toast.makeText(
                                holder.itemView.context,
                                "Failed to add comment",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()

                        }
                } else {

                    android.widget.Toast.makeText(
                        holder.itemView.context,
                        "Please write a comment",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }

            dialog.show()

            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.let {

                val behavior =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)

                behavior.state =
                    com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED

                behavior.peekHeight = 0

                it.layoutParams.height =
                    (holder.itemView.resources.displayMetrics.heightPixels * 0.85).toInt()

                it.requestLayout()
            }


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
 //SAVE
        var saved = false

        holder.btnSave.setOnClickListener {

            saved = !saved

            if (saved) {
                holder.btnSave.setImageResource(R.drawable.ic_bookmark_filled)

                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "Saved",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

            } else {

                holder.btnSave.setImageResource(R.drawable.ic_bookmark)

                android.widget.Toast.makeText(
                    holder.itemView.context,
                    "Removed from Saved",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onViewDetachedFromWindow(holder: ReelViewHolder) {
        super.onViewDetachedFromWindow(holder)

        holder.playerView.player?.pause()
    }
    fun playVideoAt(position: Int) {

        players.forEach { (index, player) ->

            if (index == position) {
                player.play()
            } else {
                player.pause()
                player.seekTo(0)
            }
        }
    }    }
