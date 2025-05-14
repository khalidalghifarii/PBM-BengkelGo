package com.example.bengkelgo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set username from intent or Firebase user
        val currentUser = intent.getStringExtra("username") ?: "Dayat"

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