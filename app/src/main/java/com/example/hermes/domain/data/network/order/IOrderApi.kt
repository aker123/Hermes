package com.example.hermes.domain.data.network.order

import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.models.Delivery
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface IOrderApi {

    @POST("orders")
    fun setOrder(@Body order: IOrder?): Call<IOrder?>?

    @POST("orderProducts")
    fun setOrderProducts(@Body orderProduct: List<IOrderProduct>?): Call<IOrderProduct>?

    @POST("deliveries")
    fun setDelivery(@Body delivery: IDelivery?): Call<IDelivery>?

    @GET("orders")
    fun getOrders(@Query("uid") uid: String?): Call<List<IOrder?>?>?

    @GET("ordersProduct")
    fun getOrderProducts(@Query("uid") uid: String?): Call<List<IOrderProduct?>?>?

    @GET("deliveries")
    fun getDeliveries(@Query("uid") uid: String?): Call<List<IDelivery?>?>?
}