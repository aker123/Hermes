package com.example.hermes.domain.data.network.registration.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class IUser(
    val uid: String,
    val login: String,
    val password: String,
    val surname: String,
    val name: String,
    val phoneNumber: String,
    val mail: String
)