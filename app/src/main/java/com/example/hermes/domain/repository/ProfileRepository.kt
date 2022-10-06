package com.example.hermes.domain.repository

import com.example.hermes.domain.Mapper
import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.local.user.entities.UserEntity
import com.example.hermes.domain.data.network.profile.IProfileApi
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.data.network.utils.ApiErrorException
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.models.User
import org.json.JSONObject


class ProfileRepository(
    private val profileApi: IProfileApi,
    private val shopsApiManager: ShopsApiManager,
    private val userDao: UserDao,
    private val shopsDao: ShopsDao,
    private val operatorDao: OperatorDao,
    private val mapper: Mapper
) {

    fun getOperatorDB(): Operator? {
        val operatorEntity = operatorDao.getOperator() ?: return null
        val shopEntity = shopsDao.getShop(operatorEntity.shopUid)
        return mapper.mapOperatorDBToOperator(shopEntity, operatorEntity)
    }

    fun getOperator(login: String, password: String): Operator? {
        val response = profileApi.getOperator(login, password).execute()

        if (response.body() == null) {
            return null
        }

        val shopEntity = shopsDao.getShop(response.body()!!.shopUid)

        val shop: Shop? = if (shopEntity == null)
            mapper.mapNetworkShopToShop(shopsApiManager.getShop(response.body()!!.shopUid))
        else
            mapper.mapShopDBToShop(shopEntity)

        if (shopEntity == null) mapper.mapShopToDBShop(shop)?.let { shopsDao.insertShop(it) }

        val operator = mapper.mapNetworkOperatorToOperator(shop, response.body())

        if (operator != null) operatorDao.insertOperator(mapper.mapOperatorToOperatorDB(operator))

        return operator
    }

    fun getUser(login: String, password: String): User? {
        val response = profileApi.getUser(login, password).execute()

        val user = mapper.mapNetworkUserToUser(response.body())

        if (user != null) userDao.insertUser(mapper.mapUserToUserDB(user))

        return user
    }

    fun saveUser(user: User) {
       val response = profileApi.setUser(mapper.mapToNetworkUser(user))?.execute() ?: throw Exception()
        response.errorBody()?.let { throw ApiErrorException(it.string()) }
        userDao.insertUser(mapper.mapUserToUserDB(user))
    }

    fun updateUser(user: User) {
        val call = profileApi.updateUser(mapper.mapToNetworkUser(user))?.execute() ?: throw Exception()
        userDao.insertUser(mapper.mapUserToUserDB(user))
    }

    fun deleteUserDB() {
        userDao.getUser()?.let { userDao.deleteUser(it) }
    }

    fun deleteOperatorDB() {
        operatorDao.getOperator()?.let { operatorDao.deleteOperator(it) }
    }

    fun getUserDB(): User? {
        return mapper.mapUserDBToUser(userDao.getUser())
    }
}