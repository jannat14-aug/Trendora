package com.example.trendora.network

import com.example.trendora.ProfileReel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @Multipart
    @POST("upload")
    fun uploadVideo(
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    @GET("myreels")
    fun getMyReels(): Call<ArrayList<ProfileReel>>

}