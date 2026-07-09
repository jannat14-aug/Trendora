package com.example.trendora

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.trendora.network.RetrofitClient
import com.example.trendora.network.UploadResponse
import com.google.firebase.database.FirebaseDatabase
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import android.widget.Toast
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class UploadActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var btnUpload: Button
    private lateinit var etCaption: EditText

    private lateinit var player: ExoPlayer

    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        playerView = findViewById(R.id.videoPreview)
        btnUpload = findViewById(R.id.btnUpload)
        etCaption = findViewById(R.id.etCaption)

        videoUri = Uri.parse(intent.getStringExtra("videoUri"))

        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(videoUri!!)

        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()

        btnUpload.setOnClickListener {

            val caption = etCaption.text.toString().trim()

            if (caption.isEmpty()) {

                etCaption.error = "Enter Caption"

                return@setOnClickListener

            }

            uploadVideo(videoUri!!, caption)

        }

    }
    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
    private fun uploadVideo(
        uri: Uri,
        caption: String
    ) {

        val inputStream = contentResolver.openInputStream(uri)

        val file = File(
            cacheDir,
            "video_${System.currentTimeMillis()}.mp4"
        )

        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)

        inputStream?.close()
        outputStream.close()

        val requestFile =
            file.asRequestBody("video/mp4".toMediaTypeOrNull())

        val body =
            MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestFile
            )

        val captionBody =
            caption.toRequestBody("text/plain".toMediaTypeOrNull())

        RetrofitClient.apiService.uploadVideo(
            body,
            captionBody
        )
            .enqueue(object : Callback<UploadResponse> {

                override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
                ) {

                    if (response.isSuccessful && response.body() != null) {

                        val filename = response.body()!!.filename

                        val videoUrl =
                            "http://10.190.29.74:8000/uploads/$filename"

                        val database =
                            FirebaseDatabase.getInstance(
                                "https://trendora-1234-default-rtdb.asia-southeast1.firebasedatabase.app/"
                            ).getReference("reels")
                                .child("reels")

                        val reelId = database.push().key!!

                        val reel = VideoModel(

                            reelId = reelId,
                            ownerId = "user1",
                            username = "@trendora",
                            caption = caption,
                            videoUrl = videoUrl,
                            thumbnail = "",
                            likes = 0,
                            comments = 0,
                            timestamp = System.currentTimeMillis()

                        )

                        database.child(reelId)
                            .setValue(reel)
                            .addOnSuccessListener {

                                Toast.makeText(
                                    this@UploadActivity,
                                    "Reel Uploaded Successfully 🎉",
                                    Toast.LENGTH_LONG
                                ).show()

                                finish()

                            }

                            .addOnFailureListener {

                                Toast.makeText(
                                    this@UploadActivity,
                                    it.message,
                                    Toast.LENGTH_LONG
                                ).show()

                            }

                    } else {

                        Toast.makeText(
                            this@UploadActivity,
                            "Upload Failed",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

                override fun onFailure(
                    call: Call<UploadResponse>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        this@UploadActivity,
                        t.message,
                        Toast.LENGTH_LONG
                    ).show()

                }

            })

    }
}