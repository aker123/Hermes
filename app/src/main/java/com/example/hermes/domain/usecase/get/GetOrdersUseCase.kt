package com.example.hermes.domain.usecase.get

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.OrderRepository
import com.example.hermes.domain.repository.ShopsRepository

class GetOrdersUseCase(
    private val orderRepository: OrderRepository
) {

    fun execute(shop: Shop): MutableLiveData<List<Order>?> {
        return orderRepository.getOrders(shop)
    }
}