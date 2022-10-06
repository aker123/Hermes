package com.example.hermes.di

import com.example.hermes.database.AppDatabase
import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.network.profile.IProfileApi
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.repository.ProfileRepository
import com.example.hermes.domain.usecase.delete.DeleteOperatorDBUseCase
import com.example.hermes.domain.usecase.delete.DeleteUserDBUseCase
import com.example.hermes.domain.usecase.get.GetOperatorByLoginAndPasswordUseCase
import com.example.hermes.domain.usecase.get.GetOperatorDBUseCase
import com.example.hermes.domain.usecase.get.GetUserByLoginAndPasswordUseCase
import com.example.hermes.domain.usecase.get.GetUserDBUseCase
import com.example.hermes.domain.usecase.save.SaveUserUseCase
import com.example.hermes.domain.usecase.save.UpdateUserUseCase
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class ProfileModule {

    @Provides
    @Singleton
    fun provideProfileRepository(
        profileApi: IProfileApi,
        shopsApiManager: ShopsApiManager,
        userDao: UserDao,
        operatorDao: OperatorDao,
        shopsDao: ShopsDao,
        mapper: Mapper
    ): ProfileRepository {
        return ProfileRepository(profileApi,shopsApiManager,userDao,shopsDao,operatorDao,mapper)
    }

    @Provides
    @Singleton
    fun provideIProfileApi(
        @Named("provideRetrofitService")
        retrofit: Retrofit
    ): IProfileApi {
        return retrofit.create(IProfileApi::class.java)
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
        profileRepository: ProfileRepository
    ): GetOperatorDBUseCase {
        return GetOperatorDBUseCase(profileRepository)
    }

    @Provides
    @Singleton
    fun provideUserDao(
        @Named("provideDbInstance")
        appDatabase: AppDatabase
    ): UserDao {
        return appDatabase.getUserDao()
    }

    @Provides
    fun provideSaveUserUseCase(
        profileRepository: ProfileRepository
    ): SaveUserUseCase {
        return SaveUserUseCase(profileRepository)
    }

    @Provides
    fun provideGetUserDBUseCase(
        profileRepository: ProfileRepository
    ): GetUserDBUseCase {
        return GetUserDBUseCase(profileRepository)
    }

    @Provides
    fun provideDeleteUserDBUseCase(
        profileRepository: ProfileRepository
    ): DeleteUserDBUseCase {
        return DeleteUserDBUseCase(profileRepository)
    }

    @Provides
    fun provideDeleteOperatorDBUseCase(
        profileRepository: ProfileRepository
    ): DeleteOperatorDBUseCase {
        return DeleteOperatorDBUseCase(profileRepository)
    }

    @Provides
    fun provideGetUserByLoginAndPasswordUseCase(
        profileRepository: ProfileRepository
    ): GetUserByLoginAndPasswordUseCase {
        return GetUserByLoginAndPasswordUseCase(profileRepository)
    }

    @Provides
    fun provideGetOperatorByLoginAndPasswordUseCase(
        profileRepository: ProfileRepository
    ): GetOperatorByLoginAndPasswordUseCase {
        return GetOperatorByLoginAndPasswordUseCase(profileRepository)
    }

    @Provides
    fun provideUpdateUserUseCase(
        profileRepository: ProfileRepository
    ): UpdateUserUseCase {
        return UpdateUserUseCase(profileRepository)
    }
}