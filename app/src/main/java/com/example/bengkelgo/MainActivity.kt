package com.example.bengkelgo

import android.app.AlertDialog // Tambahkan import AlertDialog
import android.content.Intent
import android.net.Uri // Tambahkan import Uri
import android.os.Bundle
import android.view.LayoutInflater // Tambahkan import LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.widget.TextView
import android.widget.ImageView
import android.widget.Button // Tambahkan import Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvUsername: TextView
    private lateinit var imgProfileIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        tvUsername = findViewById(R.id.tvUsername)
        imgProfileIcon = findViewById(R.id.imgProfileIcon)

        val user = auth.currentUser
        if (user != null) {
            tvUsername.text = user.displayName ?: user.email ?: "User"
        } else {
            tvUsername.text = "Guest"
        }

        imgProfileIcon.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        setupServiceCards()
    }

    private fun setupServiceCards() {
        // Vehicle Repair
        findViewById<CardView>(R.id.cardVehicleRepair).setOnClickListener {
            // Toast.makeText(this, "Vehicle Repair selected", Toast.LENGTH_SHORT).show() // Hapus atau komentari ini
            startActivity(Intent(this, VehicleRepairFormActivity::class.java))
        }

        // Emergency Call
        findViewById<CardView>(R.id.cardEmergencyCall).setOnClickListener {
            // Ganti Toast dengan pemanggilan dialog
            // Toast.makeText(this, "Emergency Call selected", Toast.LENGTH_SHORT).show()
            showEmergencyCallDialog()
        }

        findViewById<CardView>(R.id.cardSparePartShop).setOnClickListener {
            startActivity(Intent(this, SparePartListActivity::class.java))
        }

        findViewById<CardView>(R.id.cardViewNearest).setOnClickListener {
            Toast.makeText(this, "View Nearest selected", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Map/Location activity/fragment
        }
    }

    private fun showEmergencyCallDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_emergency_call, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Pengguna harus memilih salah satu tombol

        val alertDialog = builder.create()

        // Mengatur background dialog agar transparan sehingga background custom dari XML terlihat (opsional)
        // alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val tvEmergencyDescription: TextView = dialogView.findViewById(R.id.tvEmergencyDescription)
        val tvEmergencyNumber: TextView = dialogView.findViewById(R.id.tvEmergencyNumber)
        val btnCancel: Button = dialogView.findViewById(R.id.btnCancelEmergencyCall)
        val btnConfirm: Button = dialogView.findViewById(R.id.btnConfirmEmergencyCall)

        // Set teks dari strings.xml
        tvEmergencyDescription.text = getString(R.string.emergency_call_dialog_description)
        tvEmergencyNumber.text = getString(R.string.emergency_call_dummy_number)

        btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            val phoneNumber = getString(R.string.emergency_call_dummy_number)
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, getString(R.string.no_phone_app_found), Toast.LENGTH_SHORT).show()
            }
            alertDialog.dismiss()
        }

        alertDialog.show()
    }
}