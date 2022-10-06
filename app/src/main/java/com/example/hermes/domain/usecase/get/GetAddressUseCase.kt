package com.example.hermes.domain.usecase.get

import com.example.hermes.domain.models.Address
import com.example.hermes.domain.repository.AddressRepository

class GetAddressUseCase(
    private val addressRepository: AddressRepository
) {
    fun execute(): List<Address> {
        return addressRepository.getAddress()
    }
}