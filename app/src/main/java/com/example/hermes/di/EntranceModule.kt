package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.network.registration.IRegistrationApi
import com.example.hermes.domain.repository.EntranceRepository
import com.example.hermes.domain.repository.RegistrationRepository
import com.example.hermes.domain.usecase.GetOperatorDBUseCase
import com.example.hermes.domain.usecase.GetUserDBUseCase
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class EntranceModule {

    @Provides
    @Singleton
    fun provideEntranceRepository(
        userDao: UserDao,
        operatorDao: OperatorDao,
        shopsDao: ShopsDao
    ): EntranceRepository {
        return EntranceRepository(userDao,operatorDao,shopsDao)
    }

    @Provides
    @Singleton
    fun provideOperatorDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): OperatorDao {
        return appDatabase.getOperatorDao()
    }

    @Provides
    fun provideGetOperatorDBUseCase(
        entranceRepository: EntranceRepository
    ): GetOperatorDBUseCase {
        return GetOperatorDBUseCase(entranceRepository)
    }
}