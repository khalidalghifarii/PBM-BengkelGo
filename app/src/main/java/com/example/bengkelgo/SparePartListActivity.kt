package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.adapter.SparePartAdapter
import com.google.firebase.firestore.FirebaseFirestore

class SparePartListActivity : AppCompatActivity() {

    private lateinit var rvSpareParts: RecyclerView
    private lateinit var sparePartAdapter: SparePartAdapter
    private var allPartsList: MutableList<SparePart> = mutableListOf()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var etSearch: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spare_part_list)

        firestore = FirebaseFirestore.getInstance()

        rvSpareParts = findViewById(R.id.rvSpareParts)
        rvSpareParts.layoutManager = LinearLayoutManager(this)
        sparePartAdapter = SparePartAdapter(this, allPartsList)
        rvSpareParts.adapter = sparePartAdapter

        etSearch = findViewById(R.id.etSearchRims)

        findViewById<ImageButton>(R.id.btnBackSparePartList).setOnClickListener { finish() }
        findViewById<ImageView>(R.id.btnCart).setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java)) // Will create this later
        }

        setupCategoryButtons()
        setupSearch()
        fetchSpareParts()
    }

    private fun setupCategoryButtons() {
        findViewById<Button>(R.id.btnCategoryRims).setOnClickListener { filterByCategory("Rims") }
        findViewById<Button>(R.id.btnCategoryTires).setOnClickListener { filterByCategory("Tires") }
        findViewById<Button>(R.id.btnCategoryDiscbrake).setOnClickListener { filterByCategory("Discbrakes") }
        findViewById<Button>(R.id.btnCategoryCalipers).setOnClickListener { filterByCategory("Calipers") }
        findViewById<Button>(R.id.btnCategoryOil).setOnClickListener { filterByCategory("Oils") }
        findViewById<Button>(R.id.btnCategoryAll).setOnClickListener { filterByCategory(null) } // null means show all
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterParts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterParts(query: String) {
        //val currentCategory = sparePartAdapter.// You might need to store current category filter
        // For simplicity, this search will search all loaded parts,
        // if you want to combine with category, the logic needs to be more complex
        val filteredList = if (query.isEmpty()) {
            allPartsList // Or apply category filter if one is active
        } else {
            allPartsList.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.brand.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true)
            }
        }
        sparePartAdapter.updateParts(filteredList)
    }


    private fun filterByCategory(category: String?) {
        val filteredList = if (category == null) {
            allPartsList
        } else {
            allPartsList.filter { it.category.equals(category, ignoreCase = true) }
        }
        sparePartAdapter.updateParts(filteredList)
        etSearch.setText("") // Clear search when category changes
    }


    private fun fetchSpareParts(category: String? = null) {
        var query = firestore.collection("spareParts")
        // If you want to filter by category directly from Firestore:
        // if (category != null) {
        //     query = query.whereEqualTo("category", category)
        // }

        query.get()
            .addOnSuccessListener { documents ->
                allPartsList.clear() // Clear previous full list
                val tempParts = mutableListOf<SparePart>()
                for (document in documents) {
                    val part = document.toObject(SparePart::class.java)
                    part.id = document.id // Set the document ID
                    tempParts.add(part)
                }
                allPartsList.addAll(tempParts)
                // Initially display all parts, or apply current filter if any
                filterParts(etSearch.text.toString()) // Apply search after fetching
                Log.d("SparePartListActivity", "Fetched ${allPartsList.size} parts.")
            }
            .addOnFailureListener { exception ->
                Log.w("SparePartListActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading parts: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}