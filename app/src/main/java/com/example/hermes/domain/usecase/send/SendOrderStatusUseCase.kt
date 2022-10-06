package com.example.hermes.domain.usecase.send

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.repository.OrderRepository

class SendOrderStatusUseCase(
    private val orderRepository: OrderRepository
) {
    fun execute(order: Order) {
        return orderRepository.sendOrderStatus(order)
    }
}