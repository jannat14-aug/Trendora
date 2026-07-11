package com.example.trendora.network

import com.example.trendora.ProfileReel
import com.example.trendora.ProfileRequest
import com.example.trendora.ProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import okhttp3.RequestBody
import retrofit2.http.Body


interface ApiService {

    @Multipart
    @POST("upload")
    fun uploadVideo(
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    @Multipart
    @POST("upload")
    fun uploadVideo(
        @Part file: MultipartBody.Part,
        @Part("caption") caption: RequestBody
    ): Call<UploadResponse>

    @Multipart
    @POST("uploadProfile")
    fun uploadProfile(
        @Part file: MultipartBody.Part
    ): Call<UploadResponse>

    @POST("save-profile")
    fun saveProfile(
        @Body profile: ProfileRequest
    ): Call<UploadResponse>

    @GET("profile")
    fun getProfile(): Call<ProfileResponse>

    @GET("myreels")
    fun getMyReels(): Call<ArrayList<ProfileReel>>

}