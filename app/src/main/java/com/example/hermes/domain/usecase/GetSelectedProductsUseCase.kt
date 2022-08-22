package com.example.hermes.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Product
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ProductsRepository

class GetSelectedProductsUseCase(
    private val productsRepository: ProductsRepository
) {

    fun execute(): List<Product> {
        return productsRepository.getSelectedProducts()
    }
}