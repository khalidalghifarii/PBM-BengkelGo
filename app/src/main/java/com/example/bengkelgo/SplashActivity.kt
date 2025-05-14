package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val intent = Intent(
                this@SplashActivity,
                LoginActivity::class.java
            )
            startActivity(intent)
            finish()
        }, SPLASH_TIMEOUT.toLong())
    }

    companion object {
        private const val SPLASH_TIMEOUT = 2000 // 2 detik
    }
}