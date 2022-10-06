package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.address.dao.AddressDao
import com.example.hermes.domain.repository.AddressRepository
import com.example.hermes.domain.usecase.delete.DeleteAddressUseCase
import com.example.hermes.domain.usecase.get.GetAddressActiveUseCase
import com.example.hermes.domain.usecase.get.GetAddressUseCase
import com.example.hermes.domain.usecase.save.SaveAddressUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AddressModule {

    @Provides
    @Singleton
    fun provideAddressRepository(
        addressDao: AddressDao,
        mapper: Mapper
    ): AddressRepository {
        return AddressRepository(addressDao, mapper)
    }

    @Provides
    @Singleton
    fun provideAddressDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): AddressDao {
        return appDatabase.getAddressDao()
    }

    @Provides
    fun provideGetAddressUseCase(
        addressRepository: AddressRepository
    ): GetAddressUseCase {
        return GetAddressUseCase(addressRepository)
    }

    @Provides
    fun provideSaveAddressUseCase(
        addressRepository: AddressRepository
    ): SaveAddressUseCase {
        return SaveAddressUseCase(addressRepository)
    }

    @Provides
    fun provideGetAddressActiveUseCase(
        addressRepository: AddressRepository
    ): GetAddressActiveUseCase {
        return GetAddressActiveUseCase(addressRepository)
    }

    @Provides
    fun provideDeleteAddressUseCase(
        addressRepository: AddressRepository
    ): DeleteAddressUseCase {
        return DeleteAddressUseCase(addressRepository)
    }
}