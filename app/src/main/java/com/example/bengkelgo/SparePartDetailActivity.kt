package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class SparePartDetailActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentPart: SparePart? = null
    private lateinit var partId: String

    private lateinit var tvPartDetailName: TextView
    private lateinit var tvPartDetailPrice: TextView
    private lateinit var tvPartDetailOverview: TextView
    private lateinit var ivPartDetailImage: ImageView
    private lateinit var btnAddToCart: Button
    private lateinit var tabLayoutDetail: TabLayout
    private lateinit var layoutPartDetailFeatures: LinearLayout
    private lateinit var layoutPartDetailSpecifications: LinearLayout
    private lateinit var tvSpecContent: TextView
    private lateinit var tvPartStock: TextView
    private lateinit var collapsingToolbar: CollapsingToolbarLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spare_part_detail)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbarDetail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        collapsingToolbar = findViewById(R.id.collapsingToolbarDetail)
        // val partNameFromIntent = intent.getStringExtra("PART_NAME") ?: "Part Details"
        // collapsingToolbar.title = partNameFromIntent // Set title on collapsingToolbar

        ivPartDetailImage = findViewById(R.id.ivPartDetailImage)
        tvPartDetailName = findViewById(R.id.tvPartDetailName)
        tvPartDetailPrice = findViewById(R.id.tvPartDetailPrice)
        tvPartDetailOverview = findViewById(R.id.tvPartDetailOverview)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        tabLayoutDetail = findViewById(R.id.tabLayoutDetail)
        layoutPartDetailFeatures = findViewById(R.id.layoutPartDetailFeatures)
        layoutPartDetailSpecifications = findViewById(R.id.layoutPartDetailSpecifications)
        tvSpecContent = findViewById(R.id.tvSpecContent)
        tvPartStock = findViewById(R.id.tvPartStock)


        partId = intent.getStringExtra("PART_ID") ?: ""
        if (partId.isEmpty()) {
            Toast.makeText(this, "Part ID not found.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        fetchPartDetails(partId)
        setupTabs()

        btnAddToCart.setOnClickListener {
            currentPart?.let { part ->
                if (part.stock > 0) {
                    addItemToCart(part)
                } else {
                    Toast.makeText(this, "This item is out of stock.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupTabs() {
        tabLayoutDetail.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { // Overview
                        tvPartDetailOverview.visibility = View.VISIBLE
                        layoutPartDetailFeatures.visibility = View.GONE
                        layoutPartDetailSpecifications.visibility = View.GONE
                    }
                    1 -> { // Features
                        tvPartDetailOverview.visibility = View.GONE
                        layoutPartDetailFeatures.visibility = View.VISIBLE
                        layoutPartDetailSpecifications.visibility = View.GONE
                    }
                    2 -> { // Specifications
                        tvPartDetailOverview.visibility = View.GONE
                        layoutPartDetailFeatures.visibility = View.GONE
                        layoutPartDetailSpecifications.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchPartDetails(partId: String) {
        firestore.collection("spareParts").document(partId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    currentPart = document.toObject(SparePart::class.java)
                    currentPart?.id = document.id
                    displayPartDetails(currentPart)
                } else {
                    Toast.makeText(this, "Part details not found.", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error fetching details: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("SparePartDetail", "Error fetching part", exception)
                finish()
            }
    }

    private fun displayPartDetails(part: SparePart?) {
        part?.let {
            collapsingToolbar.title = it.name
            tvPartDetailName.text = it.name
            tvPartDetailPrice.text = "Rp %,.0f".format(it.price)
            tvPartDetailOverview.text = it.description
            tvPartStock.text = "Stock: ${it.stock}"

            // TODO: Load image using Glide/Picasso
            val imageResId = resources.getIdentifier(it.imageName.substringBefore("."), "drawable", packageName)
            if (imageResId != 0) {
                ivPartDetailImage.setImageResource(imageResId)
            } else {
                ivPartDetailImage.setImageResource(R.drawable.placeholder_part_large)
            }


            // Populate Features (simple example, can be more complex)
            val featuresContainer = findViewById<LinearLayout>(R.id.layoutPartDetailFeatures)
            featuresContainer.removeAllViews() // Clear previous views if any
            if (it.features.isNotEmpty()) {
                it.features.forEach { featureText ->
                    val featureTextView = TextView(this)
                    featureTextView.text = featureText
                    featureTextView.textSize = 16f
                    featureTextView.setPadding(0, 4, 0, 4)
                    featuresContainer.addView(featureTextView)
                }
            } else {
                val noFeatureTextView = TextView(this)
                noFeatureTextView.text = "No specific features listed."
                featuresContainer.addView(noFeatureTextView)
            }


            // Populate Specifications
            val specBuilder = StringBuilder()
            if (it.specifications.isNotEmpty()){
                it.specifications.forEach { (key, value) ->
                    specBuilder.append("$key: $value\n")
                }
                tvSpecContent.text = specBuilder.toString().trim()
            } else {
                tvSpecContent.text = "No specifications listed."
            }


            btnAddToCart.isEnabled = it.stock > 0
            btnAddToCart.text = if (it.stock > 0) "Add To Cart" else "Out of Stock"
        }
    }

    private fun addItemToCart(part: SparePart) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "You need to be logged in to add items to cart.", Toast.LENGTH_SHORT).show()
            // Optionally redirect to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        val cartItemRef = firestore.collection("userCarts").document(userId)
            .collection("items").document(part.id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(cartItemRef)
            val newQuantity: Int
            if (snapshot.exists()) {
                val existingCartItem = snapshot.toObject(CartItem::class.java)
                newQuantity = (existingCartItem?.quantity ?: 0) + 1
            } else {
                newQuantity = 1
            }

            if (newQuantity > part.stock) {
                throw FirebaseFirestoreException("Not enough stock available.", FirebaseFirestoreException.Code.ABORTED)
            }

            val cartItem = CartItem(
                partId = part.id,
                name = part.name,
                price = part.price,
                quantity = newQuantity,
                imageName = part.imageName
            )
            transaction.set(cartItemRef, cartItem)
            null // Transaction must return null or a result
        }.addOnSuccessListener {
            Toast.makeText(this, "${part.name} added to cart.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            if (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.ABORTED) {
                Toast.makeText(this, "Failed to add to cart: Not enough stock.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to add to cart: ${e.message}", Toast.LENGTH_LONG).show()
            }
            Log.e("SparePartDetail", "Error adding to cart", e)
        }
    }
}