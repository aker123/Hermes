package com.example.hermes.domain.usecase.save

import com.example.hermes.domain.models.Product
import com.example.hermes.domain.repository.ProductsRepository

class SaveProductUseCase(
    private val productsRepository: ProductsRepository
)  {

    fun execute(product: Product){
        return productsRepository.saveProduct(product)
    }
}