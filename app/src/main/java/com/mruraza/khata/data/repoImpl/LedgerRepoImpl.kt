package com.mruraza.khata.data.repoImpl

import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.data.local.DAO.CustomerDao
import com.mruraza.khata.data.local.DAO.ItemsDAO
import com.mruraza.khata.data.local.DAO.TransactionDao
import com.mruraza.khata.data.mapper.ItemsMapper.toEntity
import com.mruraza.khata.data.mapper.ItemsMapper.toModel
import com.mruraza.khata.data.mapper.TransactionMapper.toEntity
import com.mruraza.khata.data.mapper.TransactionMapper.toModel
import com.mruraza.khata.data.mapper.customerMapper.toEntity
import com.mruraza.khata.data.mapper.customerMapper.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class LedgerRepositoryImpl(
    private val customerDao: CustomerDao,
    private val itemsDao: ItemsDAO,
    private val transactionDao: TransactionDao
) : LedgerRepository {

    override suspend fun addCustomer(customer: Customer) {
        customerDao.insert(customer.toEntity())
    }

    override fun getCustomers(): Flow<List<Customer>> =
        customerDao.getCustomers().map { entities ->
            entities.map {
                it.toModel()
            }
        }


    override suspend fun addTransaction(tx: Transaction) {
        transactionDao.insert(tx.toEntity())
    }

    override fun getTransactions(id: Int): Flow<List<Transaction>> =
        transactionDao.getTransactions(id).map { entities ->
            entities.map {
                it.toModel()
            }
        }

    override fun getTransaction(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }

    override suspend fun updateTransaction(tx: Transaction) {
        transactionDao.updateTransaction(tx.toEntity())
    }

    override suspend fun deleteTransaction(tx: Transaction) {
        transactionDao.deleteTransaction(tx.toEntity())
    }

    override suspend fun addItems(item: Items) {
        itemsDao.insert(item.toEntity())
    }

    override fun getItems(): Flow<List<Items>> {
        return itemsDao.getItems().map { entities ->
            entities.map {
                it.toModel()
            }
        }
    }

    override suspend fun updateItem(item: Items) {
        itemsDao.updateItem(item.toEntity())
    }

    override suspend fun deleteItem(item: Items) {
        itemsDao.deleteItem(item.toEntity())
    }

    override suspend fun getCustomersById(id: Int): Flow<Customer> {
        return customerDao.getCustomerById(id).map {
            it.toModel()
        }
    }

    override suspend fun updateCustomer(customer: Customer) {
        customerDao.updateCustomer(customer.toEntity())
    }

    override suspend fun deleteCustomer(customer: Customer) {
        customerDao.deleteCustomer(customer.toEntity())
    }
}
