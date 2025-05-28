package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.adapter.CartAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ShoppingCartActivity : AppCompatActivity() {

    private lateinit var rvCartItems: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var cartItemsList: MutableList<CartItem> = mutableListOf()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var userId: String? = null

    private lateinit var tvTotalItems: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var tvEmptyCart: TextView
    private lateinit var btnProceedToCheckout: Button
    private var cartListener: ListenerRegistration? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_cart)

        val toolbar: Toolbar = findViewById(R.id.toolbarCart)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid

        tvTotalItems = findViewById(R.id.tvTotalItems)
        tvTotalPrice = findViewById(R.id.tvTotalPrice)
        tvEmptyCart = findViewById(R.id.tvEmptyCart)
        btnProceedToCheckout = findViewById(R.id.btnProceedToCheckout)

        rvCartItems = findViewById(R.id.rvCartItems)
        rvCartItems.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(this, cartItemsList,
            onQuantityChanged = { cartItem, newQuantity ->
                updateCartItemQuantity(cartItem, newQuantity)
            },
            onItemRemoved = { cartItem ->
                removeCartItem(cartItem)
            }
        )
        rvCartItems.adapter = cartAdapter

        btnProceedToCheckout.setOnClickListener {
            if (cartItemsList.isNotEmpty()) {
                // Navigate to CheckoutActivity (to be created)
                val intent = Intent(this, SparePartCheckoutActivity::class.java)
                // You might want to pass cart total or items if not re-fetching in checkout
                startActivity(intent)
            } else {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show()
            }
        }

        if (userId == null) {
            Toast.makeText(this, "Please log in to view your cart.", Toast.LENGTH_LONG).show()
            // Optionally redirect to login
            finish()
            return
        }
    }

    override fun onStart() {
        super.onStart()
        userId?.let { listenToCartChanges(it) }
    }

    override fun onStop() {
        super.onStop()
        cartListener?.remove()
    }

    private fun listenToCartChanges(uid: String) {
        val cartRef = firestore.collection("userCarts").document(uid).collection("items")
        cartListener = cartRef.addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w("ShoppingCartActivity", "Listen failed.", e)
                Toast.makeText(this, "Error loading cart: ${e.message}", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            cartItemsList.clear()
            for (doc in snapshots!!) {
                val item = doc.toObject(CartItem::class.java)
                cartItemsList.add(item)
            }
            cartAdapter.updateItems(cartItemsList)
            updateCartSummary()
        }
    }

    private fun updateCartItemQuantity(item: CartItem, newQuantity: Int) {
        userId?.let { uid ->
            // Optional: Check stock before updating
            firestore.collection("spareParts").document(item.partId).get()
                .addOnSuccessListener { partDoc ->
                    val stock = partDoc.getLong("stock")?.toInt() ?: 0
                    if (newQuantity <= stock) {
                        val itemRef = firestore.collection("userCarts").document(uid)
                            .collection("items").document(item.partId)
                        itemRef.update("quantity", newQuantity)
                            .addOnSuccessListener { Log.d("ShoppingCart", "Quantity updated") }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating quantity: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("ShoppingCart", "Error updating quantity", e)
                            }
                    } else {
                        Toast.makeText(this, "Not enough stock for ${item.name}.", Toast.LENGTH_SHORT).show()
                        // Revert UI or fetch current cart state again if optimistic update was done
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Could not verify stock.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removeCartItem(item: CartItem) {
        userId?.let { uid ->
            firestore.collection("userCarts").document(uid)
                .collection("items").document(item.partId)
                .delete()
                .addOnSuccessListener { Log.d("ShoppingCart", "Item removed") }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error removing item: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ShoppingCart", "Error removing item", e)
                }
        }
    }

    private fun updateCartSummary() {
        var totalItemCount = 0
        var totalPriceValue = 0.0
        for (item in cartItemsList) {
            totalItemCount += item.quantity
            totalPriceValue += item.price * item.quantity
        }

        tvTotalItems.text = "Total ${totalItemCount} Item${if (totalItemCount == 1) "" else "s"}"
        tvTotalPrice.text = "Rp %,.0f".format(totalPriceValue)

        if (cartItemsList.isEmpty()) {
            tvEmptyCart.visibility = View.VISIBLE
            rvCartItems.visibility = View.GONE
            btnProceedToCheckout.isEnabled = false
        } else {
            tvEmptyCart.visibility = View.GONE
            rvCartItems.visibility = View.VISIBLE
            btnProceedToCheckout.isEnabled = true
        }
    }
}