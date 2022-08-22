package com.example.hermes.di

import com.example.hermes.domain.data.network.order.IOrderApi
import com.example.hermes.domain.data.network.order.OrderApiManager
import com.example.hermes.domain.repository.OrderRepository
import com.example.hermes.domain.usecase.SendOrderUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class OrderModule {

    @Provides
    @Singleton
    fun provideOrderRepository(
        orderApiManager: OrderApiManager
    ): OrderRepository {
        return OrderRepository(orderApiManager)
    }

    @Provides
    @Singleton
    fun provideIOrderApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IOrderApi {
        return retrofit.create(IOrderApi::class.java)
    }


    @Provides
    @Singleton
    fun provideOrderApiManager(
        iOrderApi: IOrderApi
    ): OrderApiManager {
        return OrderApiManager(iOrderApi)
    }

    @Provides
    fun provideSendOrderUseCase(
        orderRepository: OrderRepository
    ): SendOrderUseCase {
        return SendOrderUseCase(orderRepository)
    }
}