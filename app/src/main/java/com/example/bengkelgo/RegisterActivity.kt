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
                            val user = auth.currentUser
                            user?.let { firebaseUser -> // Ubah 'it' menjadi 'firebaseUser' untuk kejelasan
                                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name) // 'name' adalah nilai dari edtName dari input user
                                    .build()

                                firebaseUser.updateProfile(profileUpdates)
                                    .addOnCompleteListener { profileTask ->
                                        if (profileTask.isSuccessful) {
                                            Toast.makeText(this, "Registration successful and profile updated", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this, "Registration successful but profile update failed: ${profileTask.exception?.message}", Toast.LENGTH_LONG).show()
                                        }
                                        // Tetap pindah ke halaman konfirmasi setelah update profil (baik berhasil atau gagal)
                                        val intent = Intent(this, CongratulationsActivity::class.java)
                                        intent.putExtra("TYPE", "REGISTER")
                                        startActivity(intent)
                                        finish()
                                    }
                            } ?: run {
                                Toast.makeText(this, "Registration successful, but user object is null.", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, CongratulationsActivity::class.java)
                                intent.putExtra("TYPE", "REGISTER")
                                startActivity(intent)
                                finish()
                            }
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