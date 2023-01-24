package com.example.hermes.domain.usecase.save

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.repository.OrderRepository

class UpdateOrdersUseCase(
    private val orderRepository: OrderRepository
) {

    fun execute(orders: List<Order>) {
        return orderRepository.updateOrderDB(orders)
    }
}