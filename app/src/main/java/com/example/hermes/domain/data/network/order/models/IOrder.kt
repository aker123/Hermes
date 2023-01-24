package com.example.hermes.domain.data.network.order.models

class IOrder(
    val uid: String,
    val number: String,
    val date: String,
    var amount: String,
    var quantity: String,
    val comment: String,
    val status: String,
    val method: String,
    val shopUid: String,
    val clientUid: String,
    val deliveryUid: String
)