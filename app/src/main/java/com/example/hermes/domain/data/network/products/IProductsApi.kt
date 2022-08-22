package com.example.hermes.domain.data.network.products

import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface IProductsApi {

    @GET("products")
    fun getProducts(@Query("uid") uid: String?): Call<List<IProduct?>?>?
}