package com.d11.space_cow_notes

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.locations_main)

        // Find views by ID
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // Rotate Icon
        val ivIcon = findViewById<ImageView>(R.id.ivIcon)
        val rotation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        ivIcon.startAnimation(rotation)

        // Set OnClickListener
        btnLogin.setOnClickListener{
            // Show Signed-In-Message
            Toast.makeText(this, getString(R.string.signed_in), Toast.LENGTH_LONG).show()

            // Open OverviewActivity
            val intent = Intent(this, OverviewActivity::class.java)
            startActivity(intent)
        }
    }
}