package com.example.hermes.domain.data.network.products

import com.example.hermes.domain.data.network.order.models.IDelivery
import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.products.models.ISize
import com.example.hermes.domain.data.network.shops.IShopsApi
import com.example.hermes.domain.data.network.shops.models.IShop
import retrofit2.Callback

class ProductsApiManager(
    private val productsApi: IProductsApi
) {
    fun getProducts(i_shop: IShop, callback: Callback<List<IProduct?>?>){
        val response = productsApi.getProducts(i_shop.uid)
        response?.enqueue(callback)
    }

    fun getSizes(productUid: String): List<ISize>? {
        val response = productsApi.getSizes(productUid)?.execute()
        return response?.body()
    }
}