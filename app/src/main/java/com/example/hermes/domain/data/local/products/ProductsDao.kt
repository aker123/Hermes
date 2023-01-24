package com.example.hermes.domain.data.local.products

import androidx.room.*
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.local.products.entities.SizeEntity

@Dao
interface ProductsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(productEntity: List<ProductEntity>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productEntity: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSizes(sizeEntity: List<SizeEntity>?)

    @Delete
    fun deleteProduct(productEntity: ProductEntity)

    @Query("DELETE FROM ProductEntity")
    fun clearingProducts()

    @Query("SELECT * FROM ProductEntity WHERE quantity > 0")
    fun getSelectedProducts(): List<ProductEntity>

    @Query("SELECT * FROM SizeEntity")
    fun getSizesProduct(): List<SizeEntity>
}