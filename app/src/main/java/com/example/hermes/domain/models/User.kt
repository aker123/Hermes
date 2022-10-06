package com.example.hermes.domain.models


class User(
    val uid: String,
    val login: String,
    var password: String,
    var surname: String,
    var name: String,
    var phoneNumber: String,
    var mail: String
)