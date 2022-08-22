package com.example.hermes.domain.data.local.user.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class UserEntity(
    @PrimaryKey
    val uid: String,
    val login: String,
    val password: String,
    val surname: String,
    val name: String,
    val phoneNumber: String,
    val mail: String
)