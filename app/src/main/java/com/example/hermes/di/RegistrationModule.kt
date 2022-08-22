package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.network.registration.IRegistrationApi
import com.example.hermes.domain.repository.RegistrationRepository
import com.example.hermes.domain.usecase.GetUserDBUseCase
import com.example.hermes.domain.usecase.SaveUserUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RegistrationModule {

    @Provides
    @Singleton
    fun provideRegistrationRepository(
        registrationApi: IRegistrationApi,
        userDao: UserDao
    ): RegistrationRepository {
        return RegistrationRepository(registrationApi,userDao)
    }

    @Provides
    @Singleton
    fun provideIRegistrationApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IRegistrationApi {
        return retrofit.create(IRegistrationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRegistrationDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): UserDao {
        return appDatabase.getRegistrationDao()
    }

    @Provides
    fun provideSaveUserUseCase(
        registrationRepository: RegistrationRepository
    ): SaveUserUseCase {
        return SaveUserUseCase(registrationRepository)
    }

    @Provides
    fun provideGetUserDBUseCase(
        registrationRepository: RegistrationRepository
    ): GetUserDBUseCase {
        return GetUserDBUseCase(registrationRepository)
    }
}