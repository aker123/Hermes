package com.example.hermes.domain.data.network.order

import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.data.network.profile.models.IUser
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Callback

class OrderApiManager(
    private val orderApi: IOrderApi
) {

    fun setOrder(i_order: IOrder){
        orderApi.setOrder(i_order)?.execute()
    }

    fun updateOrder(i_order: IOrder){
        orderApi.updateOrder(i_order)?.execute()
    }

    fun setOrderProducts(i_orderProducts: List<IOrderProduct>){
        orderApi.setOrderProducts(i_orderProducts)?.execute()
    }

    fun setDelivery(i_delivery: IDelivery){
        orderApi.setDelivery(i_delivery)?.execute()
    }

    fun getOrders(shopUid: String, callback: Callback<List<IOrder?>?>){
        val response = orderApi.getOrders(shopUid)
        response?.enqueue(callback)
    }

    fun getOrderHistory(userUid: String, callback: Callback<List<IOrder?>?>){
        val response = orderApi.getOrderHistory(userUid)
        response?.enqueue(callback)
    }

    fun getActiveOrders(userUid: String, callback: Callback<List<IOrder?>?>){
        val response = orderApi.getActiveOrders(userUid)
        response?.enqueue(callback)
    }

    fun getOrderProducts(orderUid: String, callback: Callback<List<IOrderProduct?>?>){
        val response = orderApi.getOrderProducts(orderUid)
        response?.enqueue(callback)
    }

    fun getOrderHistoryProducts(orderUid: String): List<IOrderProduct?>? {
        val response = orderApi.getOrderProducts(orderUid)?.execute()
        return response?.body()
    }

    fun getDelivered(deliveryUid: String): IDelivery? {
        val response = orderApi.getDelivered(deliveryUid)?.execute()
        return response?.body()
    }

    fun getUser(userUid: String): IUser? {
        val response = orderApi.getUser(userUid)?.execute()
        return response?.body()
    }

    fun getShop(shopUid: String): IShop? {
        val response = orderApi.getShop(shopUid)?.execute()
        return response?.body()
    }

}