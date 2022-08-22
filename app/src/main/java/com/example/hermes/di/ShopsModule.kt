package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.network.shops.IShopsApi
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.repository.ShopsRepository
import com.example.hermes.domain.usecase.GetProductsUseCase
import com.example.hermes.domain.usecase.GetShopDBUseCase
import com.example.hermes.domain.usecase.GetShopsUseCase
import com.example.hermes.domain.usecase.SaveShopsDBUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ShopsModule {

    @Provides
    @Singleton
    fun provideShopsRepository(
        shopsApiManager: ShopsApiManager,
        shopsDao: ShopsDao
    ): ShopsRepository {
        return ShopsRepository(shopsApiManager,shopsDao)
    }

    @Provides
    @Singleton
    fun provideShopsDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): ShopsDao {
        return appDatabase.getShopsDao()
    }

    @Provides
    @Singleton
    fun provideIShopsApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IShopsApi {
        return retrofit.create(IShopsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShopsApiManager(
        iShopsApi: IShopsApi
    ): ShopsApiManager {
        return ShopsApiManager(iShopsApi)
    }

    @Provides
    fun provideGetShopsUseCase(
        shopsRepository: ShopsRepository
    ): GetShopsUseCase {
        return GetShopsUseCase(shopsRepository)
    }

    @Provides
    fun provideGetShopDBUseCase(
        shopsRepository: ShopsRepository
    ): GetShopDBUseCase {
        return GetShopDBUseCase(shopsRepository)
    }

    @Provides
    fun provideSaveShopsDBUseCase(
        shopsRepository: ShopsRepository
    ): SaveShopsDBUseCase {
        return SaveShopsDBUseCase(shopsRepository)
    }
}