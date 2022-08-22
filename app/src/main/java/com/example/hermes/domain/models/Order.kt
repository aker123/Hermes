package com.example.hermes.domain.models

class Order(
    val uid: String,
    val amount: Long,
    val quantity: Long,
    val shop: Shop,
    val user: User,
    val delivery: Delivery,
    val products: List<Product>,
    val comment: String
)