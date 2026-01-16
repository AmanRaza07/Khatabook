package com.mruraza.khata.data.repoImpl

import androidx.room.Transaction
import com.mruraza.khata.data.local.DAO.RestoreDao
import com.mruraza.khata.data.local.Entities.CustomerEntity
import com.mruraza.khata.data.local.Entities.ItemsEntity
import com.mruraza.khata.data.local.Entities.TransactionEntity

class RestoreRepository(
    private val restoreDao: RestoreDao
) {

    @Transaction
    suspend fun restoreAll(
        customers: List<CustomerEntity>,
        items: List<ItemsEntity>,
        transactions: List<TransactionEntity>
    ) {
        restoreDao.clearTransactions()
        restoreDao.clearItems()
        restoreDao.clearCustomers()

        restoreDao.insertCustomers(customers)
        restoreDao.insertItems(items)
        restoreDao.insertTransactions(transactions)
    }
}
