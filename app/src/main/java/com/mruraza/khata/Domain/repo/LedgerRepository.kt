package com.mruraza.khata.Domain.repo

import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.Domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface LedgerRepository {
    suspend fun addCustomer(customer: Customer)
    fun getCustomers(): Flow<List<Customer>>
    suspend fun getCustomersById(id: Int): Flow<Customer>
    suspend fun updateCustomer(customer: Customer)
    suspend fun deleteCustomer(customer: Customer)


    suspend fun addTransaction(tx: Transaction)
    fun getTransactions(id: Int): Flow<List<Transaction>>
    fun getTransaction(): Flow<List<Transaction>>
    suspend fun updateTransaction(tx: Transaction)
    suspend fun deleteTransaction(tx: Transaction)

    suspend fun addItems(item: Items)
    fun getItems(): Flow<List<Items>>
    suspend fun updateItem(item: Items)
    suspend fun deleteItem(item: Items)
}
