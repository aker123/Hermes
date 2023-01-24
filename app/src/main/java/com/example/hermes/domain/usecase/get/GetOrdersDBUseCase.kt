package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.repository.OrderRepository

class GetOrdersDBUseCase(
    private val orderRepository: OrderRepository
) {

    fun execute(): List<Order> {
        return orderRepository.getOrdersDB()
    }
}