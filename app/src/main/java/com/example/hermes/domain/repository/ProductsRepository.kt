package com.example.hermes.domain.repository

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.network.products.ProductsApiManager
import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductsRepository(
    private val productsApiManager: ProductsApiManager,
    private val productsDao: ProductsDao
) {

    fun getSelectedProducts(): List<Product> {
        return mapBaseProductToProduct(productsDao.getSelectedProducts())
    }

    fun saveProducts(shop: Shop,products: List<Product>) {
        productsDao.clearingProducts()
        val productsEntity = mapProductToBaseProduct(shop, products)
        productsDao.insertProducts(productsEntity)

    }

    fun getProducts(shop: Shop): MutableLiveData<List<Product>?> {
        val products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        productsApiManager.getProducts(mapToNetworkShop(shop), object : Callback<List<IProduct?>?> {
            override fun onResponse(
                call: Call<List<IProduct?>?>,
                response: Response<List<IProduct?>?>
            ) {
                if (response.isSuccessful) {
                    val body: List<IProduct?>? = response.body()
                    products.value = mapNetworkProductsToProducts(body)
                } else {
                    products.postValue(null)
                }
            }

            override fun onFailure(call: Call<List<IProduct?>?>, t: Throwable) {
                products.postValue(null)
            }
        })

        return products
    }

    private fun mapBaseProductToProduct(productsEntity: List<ProductEntity>): List<Product> {
        val products: MutableList<Product> = mutableListOf()
        productsEntity.forEach {
            val product = Product(
                it.uid,
                it.shopUid,
                it.name,
                it.price,
                it.amount,
                it.quantity
            )
            products.add(product)
        }
        return products
    }

    private fun mapProductToBaseProduct(shop:Shop,product: List<Product>): List<ProductEntity> {
        val productsEntity: MutableList<ProductEntity> = mutableListOf()
        product.forEach {
            val productEntity = ProductEntity(
                it.uid,
                shop.uid,
                it.name,
                it.price,
                it.amount,
                it.quantity
            )
            productsEntity.add(productEntity)
        }
        return productsEntity
    }


    private fun mapToNetworkShop(shop: Shop): IShop {
        return IShop(
            shop.uid,
            shop.name,
            shop.physicalAddress,
            shop.legalAddress,
            shop.phoneNumber
        )
    }

    private fun mapNetworkProductsToProducts(i_products: List<IProduct?>?): List<Product> {
        if (i_products == null) return listOf()
        val products: MutableList<Product> = mutableListOf()
        i_products.forEach {
            if (it == null) return@forEach
            val product = Product(
                it.uid,
                it.shopUID,
                it.name,
                it.price.toLong(),
                0,
                0
            )
            products.add(product)
        }
        return products
    }
}