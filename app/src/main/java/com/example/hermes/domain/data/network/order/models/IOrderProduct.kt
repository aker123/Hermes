package com.example.hermes.domain.data.network.order.models

class IOrderProduct(
    val uid: String,
    val name: String,
    val price: String,
    var amount: String,
    var quantity: String,
    val orderUid: String,
    val imagePath: String,
    val size: String
)