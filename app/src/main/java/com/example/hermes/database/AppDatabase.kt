package com.example.hermes.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hermes.domain.data.local.address.dao.AddressDao
import com.example.hermes.domain.data.local.address.entities.AddressEntity
import com.example.hermes.domain.data.local.operator.dao.OperatorDao
import com.example.hermes.domain.data.local.operator.entities.OperatorEntity
import com.example.hermes.domain.data.local.orders.OrdersDao
import com.example.hermes.domain.data.local.orders.entities.OrderEntity
import com.example.hermes.domain.data.local.products.ProductsDao
import com.example.hermes.domain.data.local.products.entities.ProductEntity
import com.example.hermes.domain.data.local.products.entities.SizeEntity
import com.example.hermes.domain.data.local.user.dao.UserDao
import com.example.hermes.domain.data.local.user.entities.UserEntity
import com.example.hermes.domain.data.local.shops.dao.ShopsDao
import com.example.hermes.domain.data.local.shops.entities.ShopEntity

@Database(entities = [UserEntity::class,ProductEntity::class,ShopEntity::class,OperatorEntity::class, SizeEntity::class, AddressEntity::class, OrderEntity::class], version = 11)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao():UserDao
    abstract fun getProductsDao():ProductsDao
    abstract fun getOrdersDao(): OrdersDao
    abstract fun getShopsDao():ShopsDao
    abstract fun getOperatorDao():OperatorDao
    abstract fun getAddressDao():AddressDao
    companion object {
        private var db_instance: AppDatabase? = null
        fun getAppDatabaseInstance(context: Context): AppDatabase {
            if (db_instance == null) {
                db_instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return db_instance!!
        }
    }
}