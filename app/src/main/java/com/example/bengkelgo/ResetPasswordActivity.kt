package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var edtOldPassword: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        // Inisialisasi views
        edtOldPassword = findViewById(R.id.edtOldPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)

        // Set click listeners
        btnSave.setOnClickListener {
            // Tambahkan validasi reset password di sini
            val oldPassword = edtOldPassword.text.toString().trim()
            val newPassword = edtNewPassword.text.toString().trim()

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Implementasi reset password sesuai kebutuhan (misalnya dengan Firebase)
                // Untuk sementara, kita anggap berhasil reset password
                Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()

                // Pindah ke halaman konfirmasi
                val intent = Intent(this, CongratulationsActivity::class.java)
                intent.putExtra("TYPE", "RESET_PASSWORD")
                startActivity(intent)
                finish()
            }
        }

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }
}