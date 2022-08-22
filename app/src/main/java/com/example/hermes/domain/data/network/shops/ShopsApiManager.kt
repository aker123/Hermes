package com.example.hermes.domain.data.network.shops

import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.Shop
import retrofit2.Callback

class ShopsApiManager(
    private val shopsApi: IShopsApi
) {
    fun getShops(callback: Callback<List<IShop?>?>) {
        val response = shopsApi.getShops()
        response?.enqueue(callback)
    }

    fun getShop(uid: String): IShop? {
        val response = shopsApi.getShop(uid)?.execute()
        return response?.body()
    }
}