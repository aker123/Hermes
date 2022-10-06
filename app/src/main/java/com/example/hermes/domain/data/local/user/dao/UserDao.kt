package com.example.hermes.domain.data.local.user.dao

import androidx.room.*
import com.example.hermes.domain.data.local.shops.entities.ShopEntity
import com.example.hermes.domain.data.local.user.entities.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userEntity: UserEntity?)

    @Query("SELECT * FROM UserEntity limit 1")
    fun getUser(): UserEntity?

    @Delete
    fun deleteUser(userEntity:UserEntity)
}