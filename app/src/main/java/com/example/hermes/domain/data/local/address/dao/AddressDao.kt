package com.example.hermes.domain.data.local.address.dao

import androidx.room.*
import com.example.hermes.domain.data.local.address.entities.AddressEntity
import com.example.hermes.domain.data.local.user.entities.UserEntity


@Dao
interface AddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(addressEntity: AddressEntity?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddresses(addressesEntity: List<AddressEntity>?)

    @Query("SELECT * FROM AddressEntity")
    fun getAddress(): List<AddressEntity>?

    @Query("SELECT * FROM AddressEntity WHERE active= 'true' limit 1")
    fun getAddressActive(): AddressEntity?

    @Delete
    fun deleteAddress(addressEntity: AddressEntity)
}