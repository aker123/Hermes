package com.example.hermes.di

import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.network.order.IOrderApi
import com.example.hermes.domain.data.network.order.OrderApiManager
import com.example.hermes.domain.repository.OrderRepository
import com.example.hermes.domain.usecase.get.GetActiveOrdersUseCase
import com.example.hermes.domain.usecase.get.GetOrderHistoryUseCase
import com.example.hermes.domain.usecase.get.GetOrderProductsUseCase
import com.example.hermes.domain.usecase.get.GetOrdersUseCase
import com.example.hermes.domain.usecase.send.SendOrderStatusUseCase
import com.example.hermes.domain.usecase.send.SendOrderUseCase
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
        orderApiManager: OrderApiManager,
        mapper: Mapper
    ): OrderRepository {
        return OrderRepository(orderApiManager,mapper)
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

    @Provides
    fun provideGetOrdersUseCase(
        orderRepository: OrderRepository
    ): GetOrdersUseCase {
        return GetOrdersUseCase(orderRepository)
    }

    @Provides
    fun provideSendOrderStatusUseCase(
        orderRepository: OrderRepository
    ): SendOrderStatusUseCase {
        return SendOrderStatusUseCase(orderRepository)
    }

    @Provides
    fun provideGetOrderProductsUseCase(
        orderRepository: OrderRepository
    ): GetOrderProductsUseCase {
        return GetOrderProductsUseCase(orderRepository)
    }

    @Provides
    fun provideGetOrderHistoryUseCase(
        orderRepository: OrderRepository
    ): GetOrderHistoryUseCase {
        return GetOrderHistoryUseCase(orderRepository)
    }

    @Provides
    fun provideGetActiveOrdersUseCase(
        orderRepository: OrderRepository
    ): GetActiveOrdersUseCase {
        return GetActiveOrdersUseCase(orderRepository)
    }
}