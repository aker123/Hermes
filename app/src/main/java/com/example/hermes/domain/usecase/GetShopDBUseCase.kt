package com.example.hermes.domain.usecase

import androidx.lifecycle.MutableLiveData
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.repository.ShopsRepository

class GetShopDBUseCase(
    private val shopsRepository: ShopsRepository
) {

    fun execute(uid: String): Shop? {
        return shopsRepository.getShopDB(uid)
    }
}