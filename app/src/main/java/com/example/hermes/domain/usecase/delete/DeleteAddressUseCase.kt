package com.example.hermes.domain.usecase.delete

import com.example.hermes.domain.models.Address
import com.example.hermes.domain.repository.AddressRepository

class DeleteAddressUseCase(
    private val addressRepository: AddressRepository
) {
    fun execute(address: Address) {
        return addressRepository.deleteAddress(address)
    }
}