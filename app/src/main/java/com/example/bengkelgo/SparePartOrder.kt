package com.example.bengkelgo

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class SparePartOrder(
    val userId: String = "",
    val userName: String? = null, // from auth.currentUser.displayName or email
    val items: List<CartItem> = emptyList(), // Store a copy of cart items
    val totalAmount: Double = 0.0,
    val shippingAddress: String = "",
    val paymentMethod: String = "Cash on Delivery", // Default
    @ServerTimestamp
    val orderTimestamp: Date? = null,
    val status: String = "Pending" // e.g., Pending, Processing, Shipped, Delivered, Cancelled
)