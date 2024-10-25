package com.bridesandgrooms.event.UI.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bridesandgrooms.event.ActivityContainer
import com.bridesandgrooms.event.R

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        // Handler to start your app main activity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, ActivityContainer::class.java))
            finish()
        }, 3000)  // Delay in milliseconds
    }
}
