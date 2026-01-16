package com.mruraza.khata.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.Domain.usecases.AddTransactionUseCase
import com.mruraza.khata.Domain.usecases.DeleteTransactionUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerByIdUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerTransactionsUseCase
import com.mruraza.khata.Domain.usecases.GetItemsUseCaseNoLoading
import com.mruraza.khata.Domain.usecases.UpdateTransactionUseCase
import com.mruraza.khata.utils.BSDate
import com.mruraza.khata.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CustomerTransactionViewModel @Inject constructor(
    private val getTransactionsUseCase: GetCustomerTransactionsUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getCustomerByIdUseCase: GetCustomerByIdUseCase,
    private val getItemsUseCase: GetItemsUseCaseNoLoading,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase
) : ViewModel() {
    fun getTransactions(customerId: Int) = getTransactionsUseCase(customerId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )

    fun getCustomerById(id: Int) = getCustomerByIdUseCase(id).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )

    fun addTransaction(tx: Transaction) {
        viewModelScope.launch {
            addTransactionUseCase.invoke(tx)
        }
    }

    fun allItems() = getItemsUseCase.invoke()


    suspend fun updateTransaction(tx: Transaction) {
        withContext(Dispatchers.IO) {
            updateTransactionUseCase.invoke(tx)
        }
    }

    fun updateTransactionNoSuspend(tx: Transaction) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateTransactionUseCase.invoke(tx)
            }
        }
    }


    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteTransactionUseCase.invoke(tx)
            }
        }
    }


    private val _activeFilterRange =
        MutableStateFlow<Pair<BSDate, BSDate>?>(null)
    val activeFilterRange = _activeFilterRange.asStateFlow()

    fun setFilterRange(from: BSDate, to: BSDate) {
        _activeFilterRange.value = from to to
    }

    private val _filteredTransactions =
        MutableStateFlow<List<Transaction>?>(null)

    val filteredTransactions = _filteredTransactions.asStateFlow()

    fun setFilteredTransactions(list: List<Transaction>) {
        _filteredTransactions.value = list
    }

    fun clearFilter() {
        _filteredTransactions.value = null
        _activeFilterRange.value = null
    }


    fun calculateTotalDue(transactions: List<Transaction>): Double {
        return transactions.sumOf { it.due }
    }

    fun applyPaymentToTransactions(
        transactions: List<Transaction>,
        paidAmount: Double
    ): List<Transaction> {

        var remaining = paidAmount

        // oldest first
        val sorted = transactions.sortedBy { it.timestamp }

        return sorted.map { tx ->
            if (remaining <= 0 || tx.due <= 0) {
                tx
            } else {
                val pay = minOf(tx.due, remaining)

                remaining -= pay

                tx.copy(
                    paid = tx.paid + pay,
                    due = tx.due - pay
                )
            }
        }
    }

    fun applyPayment(transactions: List<Transaction>, paidAmount: Double) {
        viewModelScope.launch {
            val updated = withContext(Dispatchers.IO) {
                applyPaymentToTransactions(transactions, paidAmount)
            }
            updated.forEach { updateTransaction(it) } // already suspend, safe
        }
    }


}