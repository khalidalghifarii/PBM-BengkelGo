package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var edtEmailReset: EditText // Gunakan EditText untuk email, bukan old password
    private lateinit var btnResetPassword: Button // Ubah nama tombol
    private lateinit var btnBack: ImageButton

    private lateinit var auth: FirebaseAuth // Deklarasikan instance FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password) // Mengatur layout untuk aktivitas ini

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi views (sesuaikan dengan layout reset_password yang baru)
        edtEmailReset = findViewById(R.id.edtEmailReset) // Pastikan ID ini ada di layout activity_reset_password
        btnResetPassword = findViewById(R.id.btnResetPassword) // Pastikan ID ini ada di layout activity_reset_password
        btnBack = findViewById(R.id.btnBack)

        // Set click listeners
        btnResetPassword.setOnClickListener {
            val email = edtEmailReset.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show()
            } else {
                // Kirim email reset kata sandi
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_LONG).show()
                            // Pindah ke halaman konfirmasi setelah email dikirim
                            val intent = Intent(this, CongratulationsActivity::class.java)
                            intent.putExtra("TYPE", "RESET_PASSWORD")
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Failed to send reset email: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }
}