package com.example.trendora.network

data class UploadResponse(
    val status: String,
    val filename: String?=null,
    val imageUrl: String?=null
)