package com.d11.space_cow_notes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locations_main)


        // Logo Effect
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)
        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.rotate)
        ivIcon.startAnimation(zoomIn)


        // start OverviewActivity after 4 seconds
        val intent = Intent(this, OverviewActivity::class.java)
        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(intent)
            }
        }, 4000)



    }
}

