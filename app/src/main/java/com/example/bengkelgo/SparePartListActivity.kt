package com.example.bengkelgo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bengkelgo.adapter.SparePartAdapter // Pastikan path adapter benar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SparePartListActivity : AppCompatActivity() {

    // Deklarasi untuk RecyclerView utama (bisa untuk featured atau semua jika satu RV)
    private lateinit var rvSpareParts: RecyclerView
    // Deklarasi untuk RecyclerView grid jika Anda menggunakan dua RecyclerView
    private lateinit var rvGridProducts: RecyclerView

    private lateinit var sparePartAdapter: SparePartAdapter // Adapter utama
    private lateinit var gridSparePartAdapter: SparePartAdapter // Adapter untuk grid jika dipisah

    private var allPartsList: MutableList<SparePart> = mutableListOf()
    private var featuredPartsList: MutableList<SparePart> = mutableListOf()
    private var gridPartsList: MutableList<SparePart> = mutableListOf()

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // Deklarasi View dari layout baru
    private lateinit var btnBackSparePartList: ImageView // Di layout baru, ini ImageView/ImageButton
    private lateinit var imgAppLogo: ImageView
    private lateinit var btnCart: ImageView
    private lateinit var tvGreetingUser: TextView
    private lateinit var tvLookingFor: TextView
    private lateinit var etSearchParts: EditText
    private lateinit var chipGroupCategory: ChipGroup
    private lateinit var tvSeeAll: TextView

    // Untuk menyimpan kategori yang sedang aktif
    private var currentCategoryFilter: String? = null
    private var currentSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan Anda menggunakan nama layout XML yang benar dan sudah diperbarui
        setContentView(R.layout.activity_spare_part_list)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // Inisialisasi Views
        btnBackSparePartList = findViewById(R.id.btnBackSparePartList)
        imgAppLogo = findViewById(R.id.imgAppLogo) // Pastikan ID ini ada di XML Anda
        btnCart = findViewById(R.id.btnCart)
        tvGreetingUser = findViewById(R.id.tvGreetingUser)
        tvLookingFor = findViewById(R.id.tvLookingFor) // Pastikan ID ini ada di XML
        etSearchParts = findViewById(R.id.etSearchParts)
        chipGroupCategory = findViewById(R.id.chipGroupCategory)
        tvSeeAll = findViewById(R.id.tvSeeAll) // Pastikan ID ini ada di XML

        // Setup RecyclerView Utama (untuk item featured)
        rvSpareParts = findViewById(R.id.rvSpareParts) // Ini akan menampilkan item featured
        rvSpareParts.layoutManager = LinearLayoutManager(this)
        // Adapter akan diinisialisasi setelah data featured dipisahkan
        // sparePartAdapter = SparePartAdapter(this, featuredPartsList) // Akan menggunakan featuredPartsList
        // rvSpareParts.adapter = sparePartAdapter

        // Setup RecyclerView Grid (untuk item grid)
        rvGridProducts = findViewById(R.id.rvGridProducts) // RecyclerView untuk produk grid
        rvGridProducts.layoutManager = GridLayoutManager(this, 2) // 2 kolom
        // Adapter untuk grid akan diinisialisasi setelah data grid dipisahkan
        // gridSparePartAdapter = SparePartAdapter(this, gridPartsList) // Akan menggunakan gridPartsList
        // rvGridProducts.adapter = gridSparePartAdapter


        // Setup Listeners
        btnBackSparePartList.setOnClickListener { finish() }
        btnCart.setOnClickListener {
            startActivity(Intent(this, ShoppingCartActivity::class.java))
        }
        tvSeeAll.setOnClickListener {
            // Logika untuk "See All", mungkin hapus filter kategori dan tampilkan semua
            currentCategoryFilter = null
            // Set chip group ke kondisi tidak ada yang terpilih jika ada chip "All" atau logika lain
            chipGroupCategory.clearCheck() // Membersihkan pilihan chip
            applyFiltersAndSearch()
            Toast.makeText(this, "Showing all products", Toast.LENGTH_SHORT).show()
        }

        setupUserGreeting()
        setupSearchListener()
        setupCategoryChipGroupListener()

        // Load data dari Firestore
        fetchAllSpareParts()
    }

    private fun setupUserGreeting() {
        val user = auth.currentUser
        if (user != null) {
            tvGreetingUser.text = "Hi, ${user.displayName ?: user.email?.split("@")?.get(0) ?: "User"}"
        } else {
            tvGreetingUser.text = "Hi, Guest"
            // Pertimbangkan untuk mengarahkan ke login jika pengguna tidak login
        }
    }


    private fun setupSearchListener() {
        etSearchParts.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentSearchQuery = s.toString()
                applyFiltersAndSearch()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupCategoryChipGroupListener() {
        chipGroupCategory.setOnCheckedChangeListener { group, checkedId ->
            if (checkedId != View.NO_ID) {
                val chip: Chip? = group.findViewById(checkedId)
                chip?.let {
                    currentCategoryFilter = when (it.id) {
                        R.id.chipRims -> "Rims"
                        R.id.chipTire -> "Tires"
                        R.id.chipDiscbrake -> "Discbrakes"
                        R.id.chipCaliper -> "Calipers"
                        // R.id.chipAll -> null // Jika ada chip "All"
                        else -> null
                    }
                }
            } else {
                currentCategoryFilter = null // Tidak ada chip yang dipilih
            }
            etSearchParts.setText("") // Reset search query saat kategori berubah
            currentSearchQuery = ""
            applyFiltersAndSearch()
        }
    }

    private fun fetchAllSpareParts() {
        firestore.collection("spareParts").get()
            .addOnSuccessListener { documents ->
                allPartsList.clear()
                for (document in documents) {
                    val part = document.toObject(SparePart::class.java)
                    part.id = document.id
                    allPartsList.add(part)
                }
                Log.d("SparePartListActivity", "Fetched ${allPartsList.size} total parts.")
                // Setelah mengambil semua parts, terapkan filter awal (mungkin kategori default atau semua)
                // Pilih chip "Rims" secara default jika itu yang diinginkan
                chipGroupCategory.check(R.id.chipRims) // Ini akan memicu listener dan applyFiltersAndSearch
                // Atau panggil applyFiltersAndSearch secara manual jika tidak ada default check
                // applyFiltersAndSearch()
            }
            .addOnFailureListener { exception ->
                Log.w("SparePartListActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Error loading parts: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun applyFiltersAndSearch() {
        var filteredList = allPartsList

        // 1. Filter berdasarkan kategori
        currentCategoryFilter?.let { category ->
            filteredList = filteredList.filter {
                it.category.equals(category, ignoreCase = true)
            }.toMutableList()
        }

        // 2. Filter berdasarkan query pencarian
        if (currentSearchQuery.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.name.contains(currentSearchQuery, ignoreCase = true) ||
                        it.brand.contains(currentSearchQuery, ignoreCase = true)
                // Anda bisa menambahkan field lain untuk dicari
            }.toMutableList()
        }

        // Logika untuk memisahkan featured dan grid (contoh sederhana)
        // Anda perlu logika yang lebih baik, mungkin berdasarkan flag di data SparePart
        // atau selalu ambil item pertama dari filteredList sebagai featured jika ada.
        featuredPartsList.clear()
        gridPartsList.clear()

        if (filteredList.isNotEmpty()) {
            // Asumsi: Item pertama dari hasil filter adalah featured jika tidak ada query pencarian
            // dan jika kategori spesifik dipilih (atau semua jika featured tidak bergantung kategori)
            // Ini perlu disesuaikan dengan bagaimana Anda mendefinisikan "featured"
            if (currentSearchQuery.isEmpty() && filteredList.size > 0) {
                // Ambil item pertama sebagai featured. Ini perlu disesuaikan.
                // Misalnya, Anda mungkin ingin item featured hanya muncul jika tidak ada search query
                // dan hanya untuk kategori tertentu atau untuk "See All".
                val potentialFeatured = filteredList.first()
                // Anda bisa menambahkan kondisi di sini, misal if (potentialFeatured.isActuallyFeatured)
                featuredPartsList.add(potentialFeatured)
                if (filteredList.size > 1) {
                    gridPartsList.addAll(filteredList.subList(1, filteredList.size))
                }
            } else {
                // Jika ada search query, semua hasil masuk ke grid
                // Atau jika tidak ada item, semua list kosong
                gridPartsList.addAll(filteredList)
            }
        }

        // Inisialisasi atau update adapter
        // Untuk rvSpareParts (featured)
        if (!::sparePartAdapter.isInitialized) {
            sparePartAdapter = SparePartAdapter(this, featuredPartsList) // Gunakan list yang sesuai
            rvSpareParts.adapter = sparePartAdapter
        } else {
            sparePartAdapter.updateParts(featuredPartsList)
        }

        // Untuk rvGridProducts
        if (!::gridSparePartAdapter.isInitialized) {
            gridSparePartAdapter = SparePartAdapter(this, gridPartsList) // Gunakan list yang sesuai
            rvGridProducts.adapter = gridSparePartAdapter
        } else {
            gridSparePartAdapter.updateParts(gridPartsList)
        }

        // Update visibilitas RecyclerView berdasarkan apakah ada data featured
        rvSpareParts.visibility = if (featuredPartsList.isNotEmpty()) View.VISIBLE else View.GONE
        rvGridProducts.visibility = if (gridPartsList.isNotEmpty()) View.VISIBLE else View.GONE


        Log.d("SparePartListActivity", "Applied filters. Featured: ${featuredPartsList.size}, Grid: ${gridPartsList.size}")
    }
}