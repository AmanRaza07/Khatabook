package com.mruraza.khata.Domain.usecases

import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddTransactionUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(tx: Transaction) =
        repo.addTransaction(tx)
}

class GetCustomerTransactionsUseCase(
    private val repo: LedgerRepository
) {
    operator fun invoke(customerId: Int): Flow<Resources<List<Transaction>>> = flow {
        emit(Resources.Loading)
        repo.getTransactions(customerId)
            .catch { e -> emit(Resources.Error(e)) }
            .collect { emit(Resources.Success(it)) }
    }.flowOn(Dispatchers.IO)
}

class GetCustomerTransactionsUseCaseNoLoading(
    private val repo: LedgerRepository
) {
    operator fun invoke(customerId: Int): Flow<List<Transaction>> {
        return repo.getTransactions(customerId).flowOn(Dispatchers.IO)
    }
}

class GetAllTransactionUseCase(
    private val repo: LedgerRepository
) {
    operator fun invoke(): Flow<Resources<List<Transaction>>> = flow {
        emit(Resources.Loading)
        repo.getTransaction()
            .catch { e -> emit(Resources.Error(e)) }
            .collect { emit(Resources.Success(it)) }
    }.flowOn(Dispatchers.IO)
}

class UpdateTransactionUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(tx: Transaction) =
        repo.updateTransaction(tx)
}

class DeleteTransactionUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(tx: Transaction) =
        repo.deleteTransaction(tx)
}