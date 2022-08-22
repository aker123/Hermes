package com.example.hermes.domain.usecase

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.repository.OrderRepository

class SendOrderUseCase(
    private val orderRepository: OrderRepository
) {
    fun execute(order: Order) {
        return orderRepository.sendOrder(order)
    }
}