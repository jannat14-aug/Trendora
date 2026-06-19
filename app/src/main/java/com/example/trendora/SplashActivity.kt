package com.example.trendora

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val arrow = findViewById<ImageView>(R.id.arrowImage)
        val logo = findViewById<ImageView>(R.id.logoImage)
        val appName = findViewById<TextView>(R.id.appName)
        val tagline = findViewById<TextView>(R.id.tagline)

        arrow.visibility = View.VISIBLE

        val arrowAnim = AnimationUtils.loadAnimation(this, R.anim.arrow_move)
        arrow.startAnimation(arrowAnim)

        Handler(Looper.getMainLooper()).postDelayed({

            arrow.visibility = View.GONE

            logo.visibility = View.VISIBLE

            val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_zoom)
            logo.startAnimation(logoAnim)

        }, 1500)

        Handler(Looper.getMainLooper()).postDelayed({

            appName.visibility = View.VISIBLE
            tagline.visibility = View.VISIBLE

            val textAnim = AnimationUtils.loadAnimation(this, R.anim.text_fade)

            appName.startAnimation(textAnim)
            tagline.startAnimation(textAnim)

        }, 2800)

        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(Intent(this, LoginActivity::class.java))
            finish()

        }, 5000)
    }
}