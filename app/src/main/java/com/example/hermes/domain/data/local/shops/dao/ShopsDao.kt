package com.example.hermes.domain.data.local.shops.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.local.shops.entities.ShopEntity

@Dao
interface ShopsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShops(shopEntity: List<ShopEntity>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShop(shopEntity: ShopEntity)

    @Query("SELECT * FROM ShopEntity WHERE uid = :uid")
    fun getShop(uid: String): ShopEntity?
}