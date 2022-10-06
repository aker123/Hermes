package com.example.hermes.domain.data.local.address.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class AddressEntity(
    @PrimaryKey
    val uid: String,
    val street: String,
    val entrance: String,
    val floor: String,
    val numberApartment: String,
    val intercom: String,
    val active: String
)