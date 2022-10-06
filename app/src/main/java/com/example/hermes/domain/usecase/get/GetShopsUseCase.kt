package com.example.hermes.domain.usecase.get

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ShopsRepository

class GetShopsUseCase(
    private val shopsRepository: ShopsRepository
) {

    fun execute(): MutableLiveData<List<Shop>?> {
        return shopsRepository.getShops()
    }
}