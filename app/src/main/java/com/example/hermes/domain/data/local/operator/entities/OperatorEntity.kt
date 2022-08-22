package com.example.hermes.domain.data.local.operator.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hermes.domain.models.Shop

@Entity
class OperatorEntity(
    @PrimaryKey
    val uid: String,
    val login: String,
    val password: String,
    val surname: String,
    val name: String,
    val phoneNumber: String,
    val mail: String,
    val shopUid: String
)