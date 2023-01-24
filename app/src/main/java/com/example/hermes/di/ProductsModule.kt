package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.network.products.IProductsApi
import com.example.hermes.domain.data.network.products.ProductsApiManager
import com.example.hermes.domain.repository.ProductsRepository
import com.example.hermes.domain.usecase.delete.ClearBasketUseCase
import com.example.hermes.domain.usecase.delete.DeleteProductDBUseCase
import com.example.hermes.domain.usecase.get.GetProductsUseCase
import com.example.hermes.domain.usecase.get.GetSelectedProductsUseCase
import com.example.hermes.domain.usecase.save.SaveProductUseCase
import com.example.hermes.domain.usecase.save.SaveProductsUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProductsModule {

    @Provides
    @Singleton
    fun provideProductsRepository(
        productsApiManager: ProductsApiManager,
        productsDao: ProductsDao,
        mapper: Mapper
    ): ProductsRepository {
        return ProductsRepository(productsApiManager,productsDao,mapper)
    }

    @Provides
    @Singleton
    fun provideIProductsApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IProductsApi {
        return retrofit.create(IProductsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductsDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): ProductsDao {
        return appDatabase.getProductsDao()
    }


    @Provides
    @Singleton
    fun provideProductsApiManager(
        iProductsApi: IProductsApi
    ): ProductsApiManager {
        return ProductsApiManager(iProductsApi)
    }

    @Provides
    fun provideGetProductsUseCase(
        productsRepository: ProductsRepository
    ): GetProductsUseCase {
        return GetProductsUseCase(productsRepository)
    }

    @Provides
    fun provideGetSelectedProductsUseCase(
        productsRepository: ProductsRepository
    ): GetSelectedProductsUseCase {
        return GetSelectedProductsUseCase(productsRepository)
    }

    @Provides
    fun provideSaveProductsUseCase(
        productsRepository: ProductsRepository
    ): SaveProductsUseCase {
        return SaveProductsUseCase(productsRepository)
    }

    @Provides
    fun provideClearBasketUseCase(
        productsRepository: ProductsRepository
    ): ClearBasketUseCase {
        return ClearBasketUseCase(productsRepository)
    }

    @Provides
    fun provideDeleteProductDBUseCase(
        productsRepository: ProductsRepository
    ): DeleteProductDBUseCase {
        return DeleteProductDBUseCase(productsRepository)
    }

    @Provides
    fun provideSaveProductUseCase(
        productsRepository: ProductsRepository
    ): SaveProductUseCase {
        return SaveProductUseCase(productsRepository)
    }
}