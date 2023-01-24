package com.example.hermes.domain.repository

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.network.order.models.IOrder
import com.example.hermes.domain.data.network.products.ProductsApiManager
import com.example.hermes.domain.data.network.products.models.IProduct
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProductsRepository(
    private val productsApiManager: ProductsApiManager,
    private val productsDao: ProductsDao,
    private val mapper: Mapper
) {


    fun deleteProduct(product: Product) {
        productsDao.deleteProduct(mapper.mapProductToBaseProduct(product))
    }

    fun deleteProducts() {
        productsDao.clearingProducts()
    }

    fun getSelectedProducts(): List<Product> {
        val products = productsDao.getSelectedProducts()
        return mapper.mapBaseProductToProduct(products, productsDao.getSizesProduct())
    }

    fun saveProducts(shop: Shop, products: List<Product>) {
        val productsEntity = mapper.mapProductToBaseProduct(shop, products)
        productsDao.insertProducts(productsEntity)

    }

    fun saveProduct(product: Product) {
        val productsEntity = mapper.mapProductToBaseProduct(product)
        productsDao.insertProduct(productsEntity)
    }

    fun getProducts(shop: Shop): MutableLiveData<List<Product>?> {
        val products: MutableLiveData<List<Product>?> = MutableLiveData<List<Product>?>()
        productsApiManager.getProducts(
            mapper.mapToNetworkShop(shop),
            object : Callback<List<IProduct?>?> {
                override fun onResponse(
                    call: Call<List<IProduct?>?>,
                    response: Response<List<IProduct?>?>
                ) {
                    if (response.isSuccessful) {
                        val productsList: MutableList<Product> = mutableListOf()
                        val body: List<IProduct?>? = response.body()

                        body?.forEach {
                            if (it == null) return@forEach
                            var sizes: List<Size> = listOf()

                            runBlocking {
                                val task =
                                    launch(Dispatchers.IO) {
                                        sizes = getSizes(it)
                                    }
                                task.join()
                            }

                            productsList.add(mapper.mapNetworkProductsToProducts(it, sizes))
                        }

                        products.value = productsList
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

    fun getSizes(iProduct: IProduct): List<Size> {
        val sizes = mapper.mapNetworkSizesToSizes(productsApiManager.getSizes(iProduct.uid))
        productsDao.insertSizes(mapper.mapSizesToBaseSizes(iProduct.uid, sizes))
        return sizes
    }
}