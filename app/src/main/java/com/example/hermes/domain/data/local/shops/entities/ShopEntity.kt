package com.example.hermes.domain.data.local.shops.entities

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import com.example.hermes.domain.data.local.products.entities.ProductEntity

@Entity
class ShopEntity(
    @PrimaryKey
    val uid: String,
    val name: String,
    val physicalAddress: String,
    val legalAddress: String,
    val phoneNumber: String
)