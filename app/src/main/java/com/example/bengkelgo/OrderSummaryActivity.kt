package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class OrderSummaryActivity : AppCompatActivity() {

    private lateinit var tvSummaryVehicleType: TextView
    private lateinit var tvSummaryServiceType: TextView
    private lateinit var tvSummaryLocationArea: TextView
    private lateinit var tvSummaryServiceSchedule: TextView
    private lateinit var edtCompleteAddress: TextInputEditText
    private lateinit var tvSummaryPaymentMethod: TextView
    private lateinit var btnOrderNow: Button
    private lateinit var btnBack: ImageButton

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var vehicleType: String? = null
    private var serviceType: String? = null
    private var locationArea: String? = null
    private var serviceScheduleFormatted: String? = null
    private var paymentMethod: String? = null

    // TAG untuk Log
    private val TAG = "OrderSummaryActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        Log.d(TAG, "onCreate: Activity Dibuat") // Log saat activity dibuat

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inisialisasi view lainnya tetap sama
        tvSummaryVehicleType = findViewById(R.id.tvSummaryVehicleType)
        tvSummaryServiceType = findViewById(R.id.tvSummaryServiceType)
        tvSummaryLocationArea = findViewById(R.id.tvSummaryLocationArea)
        tvSummaryServiceSchedule = findViewById(R.id.tvSummaryServiceSchedule)
        edtCompleteAddress = findViewById(R.id.edtCompleteAddress)
        tvSummaryPaymentMethod = findViewById(R.id.tvSummaryPaymentMethod)
        btnOrderNow = findViewById(R.id.btnOrderNow)
        btnBack = findViewById(R.id.btnBackOrderSummary)

        btnBack.setOnClickListener {
            Log.d(TAG, "Tombol Kembali Diklik")
            finish()
        }

        loadOrderData()

        btnOrderNow.setOnClickListener {
            Log.d(TAG, "btnOrderNow: Tombol Order Now Diklik!")
            submitOrder()
        }
    }

    private fun loadOrderData() {
        Log.d(TAG, "loadOrderData: Memuat data pesanan dari Intent")
        vehicleType = intent.getStringExtra("VEHICLE_TYPE")
        serviceType = intent.getStringExtra("SERVICE_TYPE")
        locationArea = intent.getStringExtra("LOCATION_AREA")
        serviceScheduleFormatted = intent.getStringExtra("SERVICE_SCHEDULE_FORMATTED")
        paymentMethod = intent.getStringExtra("PAYMENT_METHOD")

        Log.d(TAG, "loadOrderData: VehicleType: $vehicleType, ServiceType: $serviceType, Location: $locationArea, Schedule: $serviceScheduleFormatted, Payment: $paymentMethod")

        tvSummaryVehicleType.text = getString(R.string.label_vehicle_type, vehicleType ?: "N/A")
        tvSummaryServiceType.text = getString(R.string.label_service_type, serviceType ?: "N/A")
        tvSummaryLocationArea.text = getString(R.string.label_location_area, locationArea ?: "N/A")
        tvSummaryServiceSchedule.text = getString(R.string.label_service_schedule, serviceScheduleFormatted ?: "N/A")
        tvSummaryPaymentMethod.text = getString(R.string.label_payment_method, paymentMethod ?: "N/A")
        Log.d(TAG, "loadOrderData: Data berhasil ditampilkan di TextViews")
    }

    private fun submitOrder() {
        Log.d(TAG, "submitOrder: Memulai proses submit pesanan")
        val completeAddress = edtCompleteAddress.text.toString().trim()
        Log.d(TAG, "submitOrder: Alamat Lengkap: '$completeAddress'")

        if (completeAddress.isEmpty()) {
            Log.w(TAG, "submitOrder: Validasi gagal - Alamat lengkap kosong.")
            Toast.makeText(this, "Alamat lengkap tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            edtCompleteAddress.error = "Tidak boleh kosong"
            return
        }
        Log.d(TAG, "submitOrder: Validasi alamat lengkap berhasil.")

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w(TAG, "submitOrder: Validasi gagal - Pengguna belum login.")
            Toast.makeText(this, "Anda harus login terlebih dahulu.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
            return
        }
        Log.d(TAG, "submitOrder: Pengguna ditemukan - User ID: ${currentUser.uid}, DisplayName: ${currentUser.displayName}, Email: ${currentUser.email}")

        val order = VehicleServiceOrder(
            userId = currentUser.uid,
            userName = currentUser.displayName ?: currentUser.email,
            vehicleType = vehicleType,
            serviceType = serviceType,
            selectedLocation = locationArea,
            fullAddress = completeAddress,
            serviceSchedule = serviceScheduleFormatted,
            paymentMethod = paymentMethod,
            orderTimestamp = Date() // Bisa juga null jika mengandalkan @ServerTimestamp
        )
        Log.d(TAG, "submitOrder: Objek VehicleServiceOrder dibuat: $order")

        Log.d(TAG, "submitOrder: Menyimpan pesanan ke Firestore...")
        firestore.collection("serviceOrders")
            .add(order)
            .addOnSuccessListener { documentReference ->
                Log.i(TAG, "submitOrder: Pesanan BERHASIL disimpan ke Firestore! ID Dokumen: ${documentReference.id}")
                Toast.makeText(this, getString(R.string.order_submitted_successfully), Toast.LENGTH_LONG).show()
                val intent = Intent(this, OrderConfirmationActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "submitOrder: GAGAL menyimpan pesanan ke Firestore!", e) // Mencetak juga exception 'e'
                Toast.makeText(this, "${getString(R.string.order_submission_error)}: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}