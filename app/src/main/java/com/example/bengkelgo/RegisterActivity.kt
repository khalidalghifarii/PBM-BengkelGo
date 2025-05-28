package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth // Import Firebase Auth

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAddress: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnContinue: Button
    private lateinit var btnBack: ImageButton

    private lateinit var auth: FirebaseAuth // Deklarasikan instance FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register) // Mengatur layout untuk aktivitas ini

        // Inisialisasi Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi views
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtAddress = findViewById(R.id.edtAddress)
        edtPassword = findViewById(R.id.edtPassword)
        btnContinue = findViewById(R.id.btnContinue)
        btnBack = findViewById(R.id.btnBack)

        // Set click listeners
        btnContinue.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val address = edtAddress.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() ||
                address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Daftarkan pengguna baru dengan Firebase
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Registrasi berhasil
                            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                            // Pindah ke halaman konfirmasi
                            val intent = Intent(this, CongratulationsActivity::class.java)
                            intent.putExtra("TYPE", "REGISTER")
                            startActivity(intent)
                            finish()
                        } else {
                            // Registrasi gagal
                            Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }

        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }
    }
}