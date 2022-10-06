package com.example.hermes.domain.usecase.delete

import com.example.hermes.domain.repository.ProductsRepository

class ClearBasketUseCase(
    private val productsRepository: ProductsRepository
) {

    fun execute() {
        return productsRepository.deleteProducts()
    }
}