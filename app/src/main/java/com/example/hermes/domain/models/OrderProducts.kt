package com.example.hermes.domain.models

class OrderProducts(
    val shop: Shop,
    val user: User,
    val products: List<Product>?
) {
}