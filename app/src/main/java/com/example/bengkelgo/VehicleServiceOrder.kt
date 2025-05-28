package com.example.bengkelgo

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class VehicleServiceOrder(
    val userId: String? = null,
    val userName: String? = null, // Opsional, bisa diambil dari user auth
    val vehicleType: String? = null,
    val serviceType: String? = null,
    val selectedLocation: String? = null, // Misal: Banda Aceh, Aceh Besar
    val fullAddress: String? = null,
    val serviceSchedule: String? = null, // Format: "dd/MM/yyyy HH:mm"
    val paymentMethod: String? = null,
    @ServerTimestamp // Otomatis diisi oleh Firestore saat data dibuat
    val orderTimestamp: Date? = null,
    val status: String = "Pending" // Status awal pesanan
)