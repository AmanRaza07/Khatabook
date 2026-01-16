package com.mruraza.khata.presentation.viewModel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.Domain.usecases.AddCustomerUseCase
import com.mruraza.khata.Domain.usecases.AddTransactionUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerTransactionsUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerTransactionsUseCaseNoLoading
import com.mruraza.khata.Domain.usecases.GetCustomersUseCase
import com.mruraza.khata.Domain.usecases.UpdateCustomerUseCase
import com.mruraza.khata.Domain.usecases.deleteCustomerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CustomersViewModel @Inject constructor(
    private val addCustomerUseCase: AddCustomerUseCase,
    private val getCustomerUseCase: GetCustomersUseCase,
    private val getCustomerTransactionsUseCase: GetCustomerTransactionsUseCaseNoLoading,
    private val updateCustomerUseCase: UpdateCustomerUseCase,
    private val deleteCustomerUseCase: deleteCustomerUseCase

) : ViewModel() {

    fun addCustomer(customer: Customer) {
        viewModelScope.launch {
            addCustomerUseCase.invoke(customer)
        }
    }

    val allCustomers =
        getCustomerUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = com.mruraza.khata.utils.Resources.Loading
        )

    private val _isAddCustomerEnabled = MutableStateFlow(false)
    fun updateAddCustomerEnabled(value: Boolean) {
        _isAddCustomerEnabled.value = value
    }
    val isAddCustomerEnabled: StateFlow<Boolean> = _isAddCustomerEnabled.asStateFlow()

    fun getTransactions(customerId: Int) = getCustomerTransactionsUseCase(customerId)


    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                updateCustomerUseCase.invoke(customer)
            }
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                deleteCustomerUseCase.invoke(customer)
            }
        }
    }
}