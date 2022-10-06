package com.example.hermes.domain.usecase.get

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.User
import com.example.hermes.domain.repository.OrderRepository

class GetOrderHistoryUseCase(
    private val orderRepository: OrderRepository
) {

    fun execute(user: User): MutableLiveData<List<Order>?> {
        return orderRepository.getOrderHistory(user)
    }
}