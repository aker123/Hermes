package com.example.hermes.domain.data.local.orders.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hermes.domain.models.*

@Entity
class OrderEntity(
    @PrimaryKey
    val uid: String,
    val number: String,
    val date: String,
    val amount: String,
    val quantity: String,
    val comment: String,
    var status: String,
    val method: String
) {
}