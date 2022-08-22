package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.network.authorization.IAuthorizationApi
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.repository.AuthorizationRepository
import com.example.hermes.domain.usecase.GetOperatorByLoginAndPasswordUseCase
import com.example.hermes.domain.usecase.GetUserByLoginAndPasswordUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class AuthorizationModule {

    @Provides
    @Singleton
    fun provideAuthorizationRepository(
        authorizationApi: IAuthorizationApi,
        shopsApiManager: ShopsApiManager,
        userDao: UserDao,
        shopsDao: ShopsDao
    ): AuthorizationRepository {
        return AuthorizationRepository(authorizationApi, shopsApiManager, userDao, shopsDao)
    }

    @Provides
    @Singleton
    fun provideIAuthorizationApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IAuthorizationApi {
        return retrofit.create(IAuthorizationApi::class.java)
    }

    @Provides
    fun provideGetUserByLoginAndPasswordUseCase(
        authorizationRepository: AuthorizationRepository
    ): GetUserByLoginAndPasswordUseCase {
        return GetUserByLoginAndPasswordUseCase(authorizationRepository)
    }

    @Provides
    fun provideGetOperatorByLoginAndPasswordUseCase(
        authorizationRepository: AuthorizationRepository
    ): GetOperatorByLoginAndPasswordUseCase {
        return GetOperatorByLoginAndPasswordUseCase(authorizationRepository)
    }
}