package com.example.hermes.domain.data.local.orders

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes.domain.data.local.orders.entities.OrderEntity

@Dao
interface OrdersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orderEntity: OrderEntity)

    @Query("SELECT * FROM OrderEntity")
    fun getOrders(): List<OrderEntity>
}