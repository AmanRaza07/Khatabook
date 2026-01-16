package com.mruraza.khata.data.local.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mruraza.khata.data.local.Entities.CustomerEntity
import com.mruraza.khata.data.local.Entities.ItemsEntity
import com.mruraza.khata.data.local.Entities.TransactionEntity

@Dao
interface RestoreDao {

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM ItemsEntity")
    suspend fun clearItems()

    @Query("DELETE FROM customers")
    suspend fun clearCustomers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomers(data: List<CustomerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(data: List<ItemsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(data: List<TransactionEntity>)
}
