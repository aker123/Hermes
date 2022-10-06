package com.example.hermes.domain.data.local.products.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class SizeEntity(
    @PrimaryKey
    val uid: String,
    val value: String,
    val uidProduct: String
)