package com.example.hermes.domain.data.local.products.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ProductEntity(
    @PrimaryKey
    val uid: String,
    val shopUid: String,
    val name: String,
    val price: Long,
    var amount: Long,
    var quantity: Long,
    val description: String,
    val gender: String,
    val category: String,
    val imagePath: String,
    val size: String,
    val productUid: String,
)