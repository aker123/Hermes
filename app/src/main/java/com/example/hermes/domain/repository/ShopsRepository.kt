package com.example.hermes.domain.repository

import androidx.lifecycle.MutableLiveData
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
    private val shopsDao: ShopsDao
) {

    fun getShops(): MutableLiveData<List<Shop>?> {
        val shops: MutableLiveData<List<Shop>?> = MutableLiveData<List<Shop>?>()
        shopsApiManager.getShops(object : Callback<List<IShop?>?> {
            override fun onResponse(call: Call<List<IShop?>?>, response: Response<List<IShop?>?>) {
                if (response.isSuccessful) {
                    val body: List<IShop?>? = response.body()
                    shops.value = mapNetworkShopsToShops(body)
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
        return mapShopDBToShop(shopsDao.getShop(uid))
    }

    fun saveShops(shops: List<Shop>) {
        val productsEntity = mapShopToDBShop(shops)
        shopsDao.insertShops(productsEntity)
    }

    private fun mapShopToDBShop(shops: List<Shop>): List<ShopEntity> {
        val shopsEntity: MutableList<ShopEntity> = mutableListOf()
        shops.forEach {
            val shopEntity = ShopEntity(
                it.uid,
                it.name,
                it.physicalAddress,
                it.legalAddress,
                it.phoneNumber
            )
            shopsEntity.add(shopEntity)
        }
        return shopsEntity
    }

    private fun mapShopDBToShop(shopEntity: ShopEntity?): Shop? {
        if (shopEntity == null) return null
        return Shop(
            shopEntity.uid,
            shopEntity.name,
            shopEntity.physicalAddress,
            shopEntity.legalAddress,
            shopEntity.phoneNumber
        )
    }

    private fun mapNetworkShopsToShops(i_shops: List<IShop?>?): List<Shop> {
        if (i_shops == null) return listOf()
        val shops: MutableList<Shop> = mutableListOf()
        i_shops.forEach {
            if (it == null) return@forEach
            val shop = Shop(
                it.uid,
                it.name,
                it.physicalAddress,
                it.legalAddress,
                it.phoneNumber
            )
            shops.add(shop)
        }
        return shops
    }

}