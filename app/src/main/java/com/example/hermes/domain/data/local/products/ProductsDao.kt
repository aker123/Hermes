package com.example.hermes.domain.data.local.products

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes.domain.data.local.products.entities.ProductEntity

@Dao
interface ProductsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(productEntity: List<ProductEntity>?)

    @Query("DELETE FROM ProductEntity")
    fun clearingProducts()

    @Query("SELECT * FROM ProductEntity WHERE quantity > 0")
    fun getSelectedProducts(): List<ProductEntity>
}