package com.example.hermes.domain.repository

import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.shops.entities.ShopEntity
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.models.Operator
import com.example.hermes.domain.models.Shop

class EntranceRepository(
    private val userDao: UserDao,
    private val operatorDao: OperatorDao,
    private val shopsDao: ShopsDao
) {

    fun getOperatorDB(): Operator? {
        val operatorEntity = operatorDao.getOperator() ?: return null
        val shopEntity = shopsDao.getShop(operatorEntity.shopUid)
        return mapOperatorDBToOperator(shopEntity, operatorEntity)
    }

    private fun mapOperatorDBToOperator(
        shopEntity: ShopEntity?,
        operatorEntity: OperatorEntity?
    ): Operator? {
        if (operatorEntity == null || shopEntity == null) return null
        val shop = mapShopDBToShop(shopEntity) ?: return null
        return Operator(
            operatorEntity.uid,
            operatorEntity.login,
            operatorEntity.password,
            operatorEntity.surname,
            operatorEntity.name,
            operatorEntity.phoneNumber,
            operatorEntity.mail,
            shop
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

}