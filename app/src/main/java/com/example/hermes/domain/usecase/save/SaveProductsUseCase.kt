package com.example.hermes.domain.usecase.save

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ProductsRepository

class SaveProductsUseCase(
    private val productsRepository: ProductsRepository
)  {

    fun execute(shop: Shop,products: List<Product>){
        return productsRepository.saveProducts(shop, products)
    }
}