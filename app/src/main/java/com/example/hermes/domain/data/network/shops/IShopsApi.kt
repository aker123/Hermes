package com.example.hermes.domain.data.network.shops

import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface IShopsApi {

    @GET("shops/uid")
    fun getShop(@Query("uid") uid: String?): Call<IShop?>?

    @GET("shops")
    fun getShops(): Call<List<IShop?>?>?
}