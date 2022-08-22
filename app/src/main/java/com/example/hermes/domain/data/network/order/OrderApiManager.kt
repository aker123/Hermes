package com.example.hermes.domain.data.network.order

import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Callback

class OrderApiManager(
    private val orderApi: IOrderApi
) {

    fun setOrder(i_order: IOrder){
        orderApi.setOrder(i_order)?.execute()
    }

    fun setOrderProducts(i_orderProducts: List<IOrderProduct>){
        orderApi.setOrderProducts(i_orderProducts)?.execute()
    }

    fun setDelivery(i_delivery: IDelivery){
        orderApi.setDelivery(i_delivery)?.execute()
    }

    fun getOrders(i_shop: IShop, callback: Callback<List<IOrder?>?>){
        val response = orderApi.getOrders(i_shop.uid)
        response?.enqueue(callback)
    }

    fun getOrderProducts(i_order: IOrder, callback: Callback<List<IOrderProduct?>?>){
        val response = orderApi.getOrderProducts(i_order.uid)
        response?.enqueue(callback)
    }

    fun getDeliveries(i_order: IOrder, callback: Callback<List<IDelivery?>?>){
        val response = orderApi.getDeliveries(i_order.deliveryUid)
        response?.enqueue(callback)
    }
}