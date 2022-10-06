package com.example.hermes.domain.repository

import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.address.dao.AddressDao
import com.example.hermes.domain.models.Address
import com.example.hermes.domain.models.Operator

class AddressRepository(
    private val addressDao: AddressDao,
    private val mapper: Mapper
) {

    fun getAddress(): List<Address> {
        return mapper.mapDBAddressToAddress(addressDao.getAddress())
    }

    fun saveAddress(address: Address) {
        addressDao.insertAddress(mapper.mapAddressToDBAddress(address))
    }

    fun saveAddresses(addresses: List<Address>) {
        addressDao.insertAddresses(mapper.mapAddressToDBAddress(addresses))
    }

    fun getAddressActive(): Address? {
        return mapper.mapDBAddressToAddress(addressDao.getAddressActive())
    }

    fun deleteAddress(address: Address) {
        addressDao.deleteAddress(mapper.mapAddressToDBAddress(address))
    }

}