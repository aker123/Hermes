package com.example.hermes.domain.usecase.save

import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ShopsRepository

class SaveShopsDBUseCase(
    private val shopsRepository: ShopsRepository
) {

    fun execute(shops: List<Shop>) {
        shopsRepository.saveShops(shops)
    }
}