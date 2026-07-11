package com.example.trendora

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FollowingAdapter(
    private val userList: ArrayList<FollowingModel>
) : RecyclerView.Adapter<FollowingAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val profileImage: ImageView =
            itemView.findViewById(R.id.profileImage)

        val username: TextView =
            itemView.findViewById(R.id.txtUsername)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_following, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val user = userList[position]

        holder.username.text = user.username

        if (user.profileImage.isNotEmpty()) {

            Glide.with(holder.itemView.context)
                .load(user.profileImage)
                .placeholder(R.drawable.profile_demo)
                .error(R.drawable.profile_demo)
                .into(holder.profileImage)

        } else {

            holder.profileImage.setImageResource(R.drawable.profile_demo)

        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}