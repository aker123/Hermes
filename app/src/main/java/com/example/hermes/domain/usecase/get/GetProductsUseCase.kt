package com.example.hermes.domain.usecase.get

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ProductsRepository
import com.example.hermes.domain.repository.ShopsRepository

class GetProductsUseCase(
    private val productsRepository: ProductsRepository
)  {

    fun execute(shop: Shop): MutableLiveData<List<Product>?> {
       return productsRepository.getProducts(shop)
    }
}