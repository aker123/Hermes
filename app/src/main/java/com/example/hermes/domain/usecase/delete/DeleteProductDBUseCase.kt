package com.example.hermes.domain.usecase.delete

import com.example.hermes.domain.models.Product
import com.example.hermes.domain.repository.ProductsRepository

class DeleteProductDBUseCase(
    private val productsRepository: ProductsRepository
) {

    fun execute(product: Product) {
        return productsRepository.deleteProduct(product)
    }
}