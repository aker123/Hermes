package com.example.hermes.domain.repository

import com.example.hermes.domain.data.network.order.OrderApiManager
import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.models.Delivery
import com.example.hermes.domain.models.Order

class OrderRepository(
    private val orderApiManager: OrderApiManager,
) {

    fun sendOrder(order: Order) {

        orderApiManager.setOrder(mapToNetworkOrder(order))

        orderApiManager.setOrderProducts(mapToNetworkOrderProducts(order))

        orderApiManager.setDelivery(mapToNetworkDelivery(order.delivery))
    }

    private fun mapToNetworkDelivery(delivery: Delivery): IDelivery {
        return IDelivery(
            delivery.uid,
            delivery.street,
            delivery.entrance,
            delivery.floor.toString(),
            delivery.numberApartment,
            delivery.intercom
        )
    }

    private fun mapToNetworkOrder(order: Order): IOrder {
        return IOrder(
            order.uid,
            order.amount.toString(),
            order.quantity.toString(),
            order.comment,
            order.shop.uid,
            order.user.uid,
            order.delivery.uid
        )
    }

    private fun mapToNetworkOrderProducts(order: Order): List<IOrderProduct> {
        val orderProducts: MutableList<IOrderProduct> = mutableListOf()
        order.products.forEach {
            val product = IOrderProduct(
                it.uid,
                it.name,
                it.price.toString(),
                it.amount.toString(),
                it.quantity.toString(),
                order.uid
            )
            orderProducts.add(product)
        }
        return orderProducts
    }
}