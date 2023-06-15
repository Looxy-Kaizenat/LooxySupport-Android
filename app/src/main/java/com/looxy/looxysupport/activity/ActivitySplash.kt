package com.looxy.looxysupport.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.looxy.looxysupport.R

class ActivitySplash : AppCompatActivity() {

    var context : Context = this@ActivitySplash
    lateinit var img_logo : ImageView

    var registerToken: String? = "1234567890qwertyuiop"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val sharedPreference =  getSharedPreferences("registerAdminDetails", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("registerToken",registerToken)
        editor.apply()

        Log.e("testing", "registerToken is $registerToken")

        img_logo = findViewById(R.id.img_logo)

        var zoomOutAnimation : Animation = AnimationUtils.loadAnimation(context,
            R.anim.zoom_in_animation
        )
        img_logo.startAnimation(zoomOutAnimation)

        Handler(Looper.getMainLooper()).postDelayed({

            startActivity(Intent(context, MainActivity::class.java))
            finish()

        }, 3000)
    }
}