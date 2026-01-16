package com.mruraza.khata.data.local.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mruraza.khata.data.converter.AppTypeConverters
import com.mruraza.khata.data.local.DAO.CustomerDao
import com.mruraza.khata.data.local.DAO.ItemsDAO
import com.mruraza.khata.data.local.DAO.RestoreDao
import com.mruraza.khata.data.local.DAO.TransactionDao
import com.mruraza.khata.data.local.Entities.CustomerEntity
import com.mruraza.khata.data.local.Entities.ItemsEntity
import com.mruraza.khata.data.local.Entities.TransactionEntity


@Database(
    entities = [
        CustomerEntity::class,
        TransactionEntity::class,
        ItemsEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(AppTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
    abstract fun itemsDao(): ItemsDAO
    abstract fun restoreDao(): RestoreDao
}
