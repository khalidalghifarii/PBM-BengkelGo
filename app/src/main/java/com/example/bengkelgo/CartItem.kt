package com.example.bengkelgo

import com.google.firebase.firestore.PropertyName

data class CartItem(
    @get:PropertyName("partId") @set:PropertyName("partId") var partId: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("price") @set:PropertyName("price") var price: Double = 0.0,
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Int = 1,
    @get:PropertyName("imageName") @set:PropertyName("imageName") var imageName: String = "" // To display in cart
)