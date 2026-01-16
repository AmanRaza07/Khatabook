package com.mruraza.khata.presentation.viewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mruraza.khata.Domain.usecases.GetAllTransactionUseCase
import com.mruraza.khata.Domain.usecases.GetCustomersUseCase
import com.mruraza.khata.Domain.usecases.GetItemsUseCase
import com.mruraza.khata.data.local.Entities.CustomerEntity
import com.mruraza.khata.data.local.Entities.ItemsEntity
import com.mruraza.khata.data.local.Entities.TransactionEntity
import com.mruraza.khata.data.repoImpl.RestoreRepository
import com.mruraza.khata.utils.ExcelRestoreParser
import com.mruraza.khata.utils.Resources
import com.mruraza.khata.utils.RoomExcelExporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SyncViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getTransactionsUseCase: GetAllTransactionUseCase,
    private val restoreRepository: RestoreRepository
) : ViewModel() {

    val customers = getCustomersUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )

    val allItems = getItemsUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )

    val allTransactions = getTransactionsUseCase.invoke().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )


    fun exportToExcel(
        context: Context,
        customers: List<CustomerEntity>,
        items: List<ItemsEntity>,
        transactions: List<TransactionEntity>,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                RoomExcelExporter.exportAll(
                    customers = customers,
                    items = items,
                    transactions = transactions
                )
                withContext(Dispatchers.Main) {
                    onDone()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Export failed")
                }
            }
        }
    }

    fun restoreFromExcel(
        context: Context,
        uri: Uri,
        onDone: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (customers, items, transactions) =
                    ExcelRestoreParser.parse(context, uri)

                restoreRepository.restoreAll(
                    customers = customers,
                    items = items,
                    transactions = transactions
                )

                withContext(Dispatchers.Main) { onDone() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Restore failed")
                }
            }
        }
    }


}