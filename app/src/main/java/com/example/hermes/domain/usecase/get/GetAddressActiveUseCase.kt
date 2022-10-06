package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Address
import com.example.hermes.domain.repository.AddressRepository

class GetAddressActiveUseCase(
    private val addressRepository: AddressRepository
) {
    fun execute(): Address? {
        return addressRepository.getAddressActive()
    }
}