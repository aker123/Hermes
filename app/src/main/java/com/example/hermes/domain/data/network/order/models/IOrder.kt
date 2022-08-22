package com.example.hermes.domain.data.network.order.models

class IOrder(
    val uid: String,
    var amount: String,
    var quantity: String,
    val comment: String,
    val shopUid: String,
    val userUid: String,
    val deliveryUid: String,
)