package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.OrderRepository

class GetOrdersHeaderUseCase(
    private val orderRepository: OrderRepository
) {

    fun execute(user: User): List<Order> {
        return orderRepository.getOrdersHeader(user)
    }
}