package com.example.hermes.domain.usecase.save

import com.example.hermes.domain.models.Address
import com.example.hermes.domain.repository.AddressRepository

class SaveAddressUseCase(
    private val addressRepository: AddressRepository
) {
    fun execute(address: Address) {
        addressRepository.saveAddress(address)
    }

    fun execute(addresses: List<Address>) {
        addressRepository.saveAddresses(addresses)
    }
}