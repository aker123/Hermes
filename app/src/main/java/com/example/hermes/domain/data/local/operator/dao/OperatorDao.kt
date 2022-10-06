package com.example.hermes.domain.data.local.operator.dao

import androidx.room.*
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity

@Dao
interface OperatorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperator(operatorEntity: OperatorEntity?)

    @Query("SELECT * FROM OperatorEntity limit 1")
    fun getOperator(): OperatorEntity?

    @Delete
    fun deleteOperator(operatorEntity: OperatorEntity)
}