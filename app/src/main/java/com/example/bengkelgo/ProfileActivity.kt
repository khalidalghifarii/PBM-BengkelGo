package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.LinearLayout
import android.app.AlertDialog
import android.view.LayoutInflater // Import LayoutInflater
import android.widget.Button // Import Button untuk dialog kustom
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var btnBack: ImageButton
    private lateinit var llLogout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        tvProfileName = findViewById(R.id.tvProfileName)
        tvProfileEmail = findViewById(R.id.tvProfileEmail)
        btnBack = findViewById(R.id.btnBackProfile)
        llLogout = findViewById(R.id.llLogout)

        loadUserProfile()

        btnBack.setOnClickListener {
            finish()
        }

        llLogout.setOnClickListener {
            showCustomLogoutConfirmationDialog() // Panggil dialog kustom
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            tvProfileName.text = user.displayName ?: "User"
            tvProfileEmail.text = user.email
        } else {
            tvProfileName.text = "Guest"
            tvProfileEmail.text = "Not logged in"
        }
    }

    private fun showCustomLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_logout_confirmation, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)

        val alertDialog = builder.show() // Tampilkan dialog

        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelLogout)
        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmLogout)

        btnCancel.setOnClickListener {
            alertDialog.dismiss() // Tutup dialog
        }

        btnConfirm.setOnClickListener {
            alertDialog.dismiss() // Tutup dialog sebelum logout
            logoutUser()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}