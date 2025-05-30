package com.example.bengkelgo

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class NearestServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearest_service) // Menghubungkan dengan layout XML

        // Inisialisasi Tombol Kembali
        val btnBack: ImageButton = findViewById(R.id.btnBackNearestService)

        // Menambahkan fungsi klik untuk tombol kembali
        btnBack.setOnClickListener {
            // Aksi ketika tombol kembali ditekan, misalnya kembali ke Activity sebelumnya
            finish() // Menutup Activity saat ini dan kembali ke Activity sebelumnya dalam tumpukan
            // atau bisa juga menggunakan onBackPressedDispatcher.onBackPressed() untuk perilaku yang sama dengan tombol back sistem
            // onBackPressedDispatcher.onBackPressed()
        }

        // Anda dapat menambahkan logika lain di sini jika diperlukan,
        // misalnya, jika di masa depan Anda ingin memuat data peta secara dinamis
        // atau menambahkan marker pada peta placeholder.
    }
}