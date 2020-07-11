package com.cnm.umbrellaalarm.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.cnm.umbrellaalarm.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    companion object {
        private const val SPLASH_DELAY_TIME: Long = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_DELAY_TIME)
    }
}