package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.orders.OrdersDao
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.network.order.IOrderApi
import com.example.hermes.domain.data.network.order.OrderApiManager
import com.example.hermes.domain.repository.OrderRepository
import com.example.hermes.domain.usecase.get.*
import com.example.hermes.domain.usecase.save.UpdateOrdersUseCase
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
        ordersDao: OrdersDao,
        mapper: Mapper
    ): OrderRepository {
        return OrderRepository(orderApiManager,ordersDao,mapper)
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
    @Singleton
    fun provideOrdersDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): OrdersDao {
        return appDatabase.getOrdersDao()
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

    @Provides
    fun provideGetOrdersDBUseCase(
        orderRepository: OrderRepository
    ): GetOrdersDBUseCase {
        return GetOrdersDBUseCase(orderRepository)
    }

    @Provides
    fun provideUpdateOrdersUseCase(
        orderRepository: OrderRepository
    ): UpdateOrdersUseCase {
        return UpdateOrdersUseCase(orderRepository)
    }

    @Provides
    fun provideGetOrdersHeaderUseCase(
        orderRepository: OrderRepository
    ): GetOrdersHeaderUseCase {
        return GetOrdersHeaderUseCase(orderRepository)
    }
}