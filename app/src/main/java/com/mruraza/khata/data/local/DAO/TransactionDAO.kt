package com.mruraza.khata.data.local.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mruraza.khata.data.local.Entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE customerId = :id")
    fun getTransactions(id: Int): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Update
    fun updateTransaction(tx: TransactionEntity)

    @Delete
    fun deleteTransaction(tx: TransactionEntity)
}
