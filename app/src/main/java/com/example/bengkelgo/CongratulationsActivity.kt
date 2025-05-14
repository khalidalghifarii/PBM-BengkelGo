package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CongratulationsActivity : AppCompatActivity() {
    private lateinit var tvMessage: TextView
    private lateinit var btnLoginPage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congratulations)

        // Inisialisasi views
        tvMessage = findViewById(R.id.tvMessage)
        btnLoginPage = findViewById(R.id.btnLoginPage)

        // Tentukan pesan berdasarkan tipe aktivitas
        val type = intent.getStringExtra("TYPE")
        if (type != null && type == "RESET_PASSWORD") {
            tvMessage.setText(R.string.password_reset_success)
        } else {
            tvMessage.setText(R.string.register_success)
        }

        // Set click listener
        btnLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}