package com.d11.space_cow_notes

import android.content.Intent
import android.database.CursorWindow
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
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


        // without this database throws SQLiteBlobTooBigException
        // see https://stackoverflow.com/questions/51959944/sqliteblobtoobigexception-row-too-big-to-fit-into-cursorwindow-requiredpos-0-t
        try {
            val field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.setAccessible(true)
            field.set(null, 100 * 1024 * 1024) //the 100MB is the new size
        } catch (e: Exception) {
            e.printStackTrace()
        }



    }
}

