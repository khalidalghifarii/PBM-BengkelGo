package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

class SparePartCheckoutActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null
    private var currentCartItems: MutableList<CartItem> = mutableListOf()
    private var totalOrderAmount: Double = 0.0

    private lateinit var tvCheckoutOrderSummary: TextView
    private lateinit var tvCheckoutTotal: TextView
    private lateinit var edtCheckoutAddress: TextInputEditText
    private lateinit var btnPlaceOrder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spare_part_checkout)

        val toolbar: Toolbar = findViewById(R.id.toolbarCheckout)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        tvCheckoutOrderSummary = findViewById(R.id.tvCheckoutOrderSummary)
        tvCheckoutTotal = findViewById(R.id.tvCheckoutTotal)
        edtCheckoutAddress = findViewById(R.id.edtCheckoutAddress)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        if (userId == null) {
            Toast.makeText(this, "Please log in to proceed.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        loadCartForCheckout(userId!!)

        btnPlaceOrder.setOnClickListener {
            if (validateInputs()) {
                placeOrder()
            }
        }
    }

    private fun loadCartForCheckout(uid: String) {
        firestore.collection("userCarts").document(uid).collection("items")
            .get()
            .addOnSuccessListener { documents ->
                currentCartItems.clear()
                var summaryText = ""
                totalOrderAmount = 0.0

                for (doc in documents) {
                    val item = doc.toObject(CartItem::class.java)
                    currentCartItems.add(item)
                    summaryText += "${item.quantity}x ${item.name} - Rp %,.0f\n".format(item.price * item.quantity)
                    totalOrderAmount += item.price * item.quantity
                }

                if (currentCartItems.isEmpty()) {
                    Toast.makeText(this, "Your cart is empty. Cannot proceed.", Toast.LENGTH_LONG).show()
                    finish()
                    return@addOnSuccessListener
                }

                tvCheckoutOrderSummary.text = summaryText.trim()
                tvCheckoutTotal.text = "Total: Rp %,.0f".format(totalOrderAmount)

                // Try to prefill address from user profile (if it was stored)
                // For now, we don't have it in auth profile, so leave it for manual input
                // auth.currentUser?.let { user ->
                //      // If you had address in user's profile:
                //      // firestore.collection("users").document(user.uid).get()...
                // }

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading cart for checkout: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Checkout", "Error loading cart", e)
                finish()
            }
    }

    private fun validateInputs(): Boolean {
        if (edtCheckoutAddress.text.toString().trim().isEmpty()) {
            edtCheckoutAddress.error = "Shipping address cannot be empty."
            return false
        }
        if (currentCartItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun placeOrder() {
        val user = auth.currentUser ?: return
        val shippingAddress = edtCheckoutAddress.text.toString().trim()

        val order = SparePartOrder(
            userId = user.uid,
            userName = user.displayName ?: user.email,
            items = ArrayList(currentCartItems), // Create a copy
            totalAmount = totalOrderAmount,
            shippingAddress = shippingAddress,
            paymentMethod = "Cash on Delivery", // Hardcoded for now
            status = "Pending"
        )

        // Firestore batch write to create order and clear cart
        val batch = firestore.batch()

        // 1. Add to orders collection
        val newOrderRef = firestore.collection("sparePartOrders").document()
        batch.set(newOrderRef, order)

        // 2. Decrement stock for each item
        currentCartItems.forEach { cartItem ->
            val partRef = firestore.collection("spareParts").document(cartItem.partId)
            // This requires reading the stock first or using FieldValue.increment(-quantity)
            // For simplicity in this example, ensure stock check was thorough before this point
            // A more robust solution would use a Firebase Function for transactional stock update.
            batch.update(partRef, "stock", com.google.firebase.firestore.FieldValue.increment(-cartItem.quantity.toLong()))
        }


        // 3. Clear user's cart
        val cartItemsRef = firestore.collection("userCarts").document(user.uid).collection("items")
        currentCartItems.forEach { item ->
            batch.delete(cartItemsRef.document(item.partId))
        }

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, OrderConfirmationActivity::class.java) // Reuse existing
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                // You might want to pass an order ID or specific message to OrderConfirmationActivity
                // intent.putExtra("ORDER_TYPE", "SPARE_PART")
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to place order: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("Checkout", "Error placing order", e)
            }
    }
}