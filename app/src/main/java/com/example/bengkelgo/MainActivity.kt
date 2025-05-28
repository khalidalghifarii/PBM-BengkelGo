package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.TextView
import android.widget.ImageView // Import ImageView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsername: TextView
    private lateinit var imgProfileIcon: ImageView // Deklarasikan ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        tvUsername = findViewById(R.id.tvUsername)
        imgProfileIcon = findViewById(R.id.imgProfileIcon) // Inisialisasi ImageView

        // Ambil informasi pengguna dari Firebase
        val user = auth.currentUser
        if (user != null) {
            tvUsername.text = user.displayName ?: user.email ?: "User"
        } else {
            tvUsername.text = "Guest"
        }

        // Setup click listener untuk icon profil
        imgProfileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Setup click listeners for service cards
        setupServiceCards()
    }

    private fun setupServiceCards() {
        // Vehicle Repair
        findViewById<CardView>(R.id.cardVehicleRepair).setOnClickListener {
            Toast.makeText(this, "Vehicle Repair selected", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Vehicle Repair activity/fragment
        }

        // Emergency Call
        findViewById<CardView>(R.id.cardEmergencyCall).setOnClickListener {
            Toast.makeText(this, "Emergency Call selected", Toast.LENGTH_SHORT).show()
            // TODO: Handle emergency call functionality
        }

        // Spare Part Shop
        findViewById<CardView>(R.id.cardSparePart).setOnClickListener {
            Toast.makeText(this, "Spare Part Shop selected", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Spare Part Shop activity/fragment
        }

        // View Nearest
        findViewById<CardView>(R.id.cardViewNearest).setOnClickListener {
            Toast.makeText(this, "View Nearest selected", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Map/Location activity/fragment
        }
    }
}