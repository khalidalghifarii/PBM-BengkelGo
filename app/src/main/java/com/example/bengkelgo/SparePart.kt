package com.example.bengkelgo

import com.google.firebase.firestore.PropertyName

data class SparePart(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "", // Document ID
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = "",
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("category") @set:PropertyName("category") var category: String = "", // e.g., "Rims", "Tires", "Oil"
    @get:PropertyName("brand") @set:PropertyName("brand") var brand: String = "",
    @get:PropertyName("stock") @set:PropertyName("stock") var stock: Int = 0,
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "", // Placeholder for now, or actual URL if you host them
    @get:PropertyName("imageName") @set:PropertyName("imageName") var imageName: String = "", // e.g., "bbs_f1_r_diamond_black.png"
    @get:PropertyName("features") @set:PropertyName("features") var features: List<String> = emptyList(),
    @get:PropertyName("specifications") @set:PropertyName("specifications") var specifications: Map<String, String> = emptyMap() // e.g., "Color" to "Black"
)