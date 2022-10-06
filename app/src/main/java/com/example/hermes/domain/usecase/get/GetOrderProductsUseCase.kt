package com.example.hermes.domain.usecase.get

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Order
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.repository.OrderRepository

class GetOrderProductsUseCase(
    private val orderRepository: OrderRepository
) {
    fun execute(order: Order): MutableLiveData<List<Product>?> {
        return orderRepository.getOrderProducts(order)
    }
}