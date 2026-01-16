package com.mruraza.khata.data.local.DAO

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mruraza.khata.data.local.Entities.ItemsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemsDAO {
    @Insert
    suspend fun insert(item: ItemsEntity)

    @Query("SELECT * FROM ItemsEntity")
    fun getItems(): Flow<List<ItemsEntity>>

    @Update
    suspend fun updateItem(item: ItemsEntity)

    @Delete
    suspend fun deleteItem(item: ItemsEntity)
}