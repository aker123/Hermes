package com.example.hermes.domain.models

class Address(
    var uid: String,
    var street: String,
    var entrance: String,
    var floor: Long,
    var numberApartment: String,
    var intercom: String,
    var active: Boolean
)