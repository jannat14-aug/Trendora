package com.example.trendora

data class ProfileReel(
    var reelId: String = "",
    var ownerId: String = "",
    var username: String = "",
    var caption: String = "",
    var videoUrl: String = "",
    var thumbnail: String = "",
    var profileImageUrl: String = "",
    var likes: Long = 0,
    var comments: Long = 0,
    var timestamp: Long = 0
)