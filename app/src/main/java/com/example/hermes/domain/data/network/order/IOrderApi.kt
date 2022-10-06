package com.example.hermes.domain.data.network.order

import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.order.models.IOrderProduct
import com.example.hermes.domain.data.network.profile.models.IUser
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Call
import retrofit2.http.*

interface IOrderApi {

    @POST("orders")
    fun setOrder(@Body order: IOrder?): Call<IOrder?>?

    @PUT("orders")
    fun updateOrder(@Body order: IOrder?): Call<IOrder?>?

    @POST("orderProducts")
    fun setOrderProducts(@Body orderProduct: List<IOrderProduct>?): Call<IOrderProduct>?

    @POST("deliveries")
    fun setDelivery(@Body delivery: IDelivery?): Call<IDelivery>?

    @GET("orders")
    fun getOrders(@Query("uid") uid: String?): Call<List<IOrder?>?>?

    @GET("orders/history")
    fun getOrderHistory(@Query("uid") uid: String?): Call<List<IOrder?>?>?

    @GET("orders/active")
    fun getActiveOrders(@Query("uid") uid: String?): Call<List<IOrder?>?>?

    @GET("orderProducts")
    fun getOrderProducts(@Query("uid") uid: String?): Call<List<IOrderProduct?>?>?

    @GET("deliveries")
    fun getDelivered(@Query("uid") uid: String?): Call<IDelivery?>?

    @GET("users/uid")
    fun getUser(@Query("uid") uid: String?): Call<IUser?>?

    @GET("shops/uid")
    fun getShop(@Query("uid") uid: String?): Call<IShop?>?
}