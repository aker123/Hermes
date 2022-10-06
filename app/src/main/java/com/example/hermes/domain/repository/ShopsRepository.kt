package com.example.hermes.domain.repository

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.shops.entities.ShopEntity
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.Shop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShopsRepository(
    private val shopsApiManager: ShopsApiManager,
    private val shopsDao: ShopsDao,
    private val mapper: Mapper
) {

    fun getShops(): MutableLiveData<List<Shop>?> {
        val shops: MutableLiveData<List<Shop>?> = MutableLiveData<List<Shop>?>()
        shopsApiManager.getShops(object : Callback<List<IShop?>?> {
            override fun onResponse(call: Call<List<IShop?>?>, response: Response<List<IShop?>?>) {
                if (response.isSuccessful) {
                    val body: List<IShop?>? = response.body()
                    shops.value = mapper.mapNetworkShopsToShops(body)
                } else {
                    shops.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IShop?>?>, t: Throwable) {
                shops.postValue(null)
            }
        })

        return shops
    }

    fun getShopDB(uid: String): Shop?{
        return mapper.mapShopDBToShop(shopsDao.getShop(uid))
    }

    fun saveShops(shops: List<Shop>) {
        val productsEntity = mapper.mapShopToDBShop(shops)
        shopsDao.insertShops(productsEntity)
    }



}