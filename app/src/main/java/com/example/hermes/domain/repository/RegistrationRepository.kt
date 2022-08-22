package com.example.hermes.domain.repository

import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.user.entities.UserEntity
import com.example.hermes.domain.data.network.registration.IRegistrationApi
import com.example.hermes.domain.data.network.registration.models.IUser
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.User


class RegistrationRepository(
    private val registrationApi: IRegistrationApi,
    private val userDao: UserDao
) {

    fun saveUser(user: User) {
        registrationApi.setUser(mapToNetworkUser(user))?.execute()

        userDao.insertUser(
            UserEntity(
                user.uid,
                user.login,
                user.password,
                user.surname,
                user.name,
                user.phoneNumber,
                user.mail
            )
        )
    }

    fun getUserDB(): User?{
        return mapUserDBToUser(userDao.getUser())
    }

    private fun mapUserDBToUser(userEntity: UserEntity?): User? {
        if (userEntity == null) return null
        return User(
            userEntity.uid,
            userEntity.login,
            userEntity.password,
            userEntity.surname,
            userEntity.name,
            userEntity.phoneNumber,
            userEntity.mail
        )
    }

    private fun mapToNetworkUser(user: User): IUser {
        return IUser(
            user.uid,
            user.login,
            user.password,
            user.surname,
            user.name,
            user.phoneNumber,
            user.mail
        )
    }
}