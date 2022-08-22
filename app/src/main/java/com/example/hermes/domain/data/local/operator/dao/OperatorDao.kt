package com.example.hermes.domain.data.local.operator.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.user.entities.UserEntity

@Dao
interface OperatorDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOperator(operatorEntity: OperatorEntity?)

    @Query("SELECT * FROM OperatorEntity limit 1")
    fun getOperator(): OperatorEntity?
}