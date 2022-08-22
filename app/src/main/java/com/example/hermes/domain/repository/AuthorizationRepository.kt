package com.example.hermes.domain.repository

import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.shops.entities.ShopEntity
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.local.user.entities.UserEntity
import com.example.hermes.domain.data.network.authorization.IAuthorizationApi
import com.example.hermes.domain.data.network.authorization.models.IOperator
import com.example.hermes.domain.data.network.authorization.models.IUser
import com.example.hermes.domain.data.network.shops.ShopsApiManager
import com.example.hermes.domain.data.network.shops.models.IShop
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.Shop
import com.example.hermes.domain.models.User

class AuthorizationRepository(
    private val authorizationApi: IAuthorizationApi,
    private val shopsApiManager: ShopsApiManager,
    private val userDao: UserDao,
    private val shopsDao: ShopsDao,
    private val operatorDao: OperatorDao
) {

    fun getOperator(login: String, password: String): Operator? {
        val response = authorizationApi.getOperator(login, password).execute()

        if (response.body() == null) {
            return null
        }

        val shopEntity = shopsDao.getShop(response.body()!!.shopUid)

        val shop: Shop? = if (shopEntity == null)
            mapNetworkShopToShop(shopsApiManager.getShop(response.body()!!.shopUid))
        else
            mapShopDBToShop(shopEntity)

        if (shopEntity == null) mapShopToDBShop(shop)?.let { shopsDao.insertShop(it) }

        val operator = mapNetworkOperatorToOperator(shop, response.body())

        if (operator != null) {
            operatorDao.insertOperator(
                OperatorEntity(
                    operator.uid,
                    operator.login,
                    operator.password,
                    operator.surname,
                    operator.name,
                    operator.phoneNumber,
                    operator.mail,
                    operator.shop.uid
                )
            )
        }

        return operator
    }

    fun getUser(login: String, password: String): User? {
        val response = authorizationApi.getUser(login, password).execute()

        val user = mapNetworkUserToUser(response.body())

        if (user != null) {
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

        return user
    }

    private fun mapShopToDBShop(shop: Shop?): ShopEntity? {
        if (shop == null) return null
        return ShopEntity(
            shop.uid,
            shop.name,
            shop.physicalAddress,
            shop.legalAddress,
            shop.phoneNumber
        )
    }

    private fun mapShopDBToShop(shopEntity: ShopEntity?): Shop? {
        if (shopEntity == null) return null
        return Shop(
            shopEntity.uid,
            shopEntity.name,
            shopEntity.physicalAddress,
            shopEntity.legalAddress,
            shopEntity.phoneNumber
        )
    }

    private fun mapNetworkShopToShop(i_shop: IShop?): Shop? {
        if (i_shop == null) return null
        return Shop(
            i_shop.uid,
            i_shop.name,
            i_shop.physicalAddress,
            i_shop.legalAddress,
            i_shop.phoneNumber
        )
    }

    private fun mapNetworkOperatorToOperator(shop: Shop?, operator: IOperator?): Operator? {
        if (shop == null || operator == null) return null
        return Operator(
            operator.uid,
            operator.login,
            operator.password,
            operator.surname,
            operator.name,
            operator.phoneNumber,
            operator.mail,
            shop
        )
    }

    private fun mapNetworkUserToUser(user: IUser?): User? {
        if (user == null) return null
        return User(
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