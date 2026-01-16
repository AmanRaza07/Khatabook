package com.mruraza.khata.Domain.usecases

import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddCustomerUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(customer: Customer) =
        repo.addCustomer(customer)
}

class GetCustomersUseCase(
    private val repo: LedgerRepository
) {
    operator fun invoke(): Flow<Resources<List<Customer>>> = flow {
        emit(Resources.Loading)
        repo.getCustomers()
            .catch { e->emit(Resources.Error(e)) }
            .collect { emit(Resources.Success(it)) }
    }.flowOn(Dispatchers.IO)
}

class GetCustomerByIdUseCase(
    private val repo: LedgerRepository
) {
    operator fun invoke(id: Int): Flow<Resources<Customer>> = flow {
        emit(Resources.Loading)
        repo.getCustomersById(id)
            .catch { e->emit(Resources.Error(e)) }
            .collect { emit(Resources.Success(it)) }
    }.flowOn(Dispatchers.IO)
}

class UpdateCustomerUseCase(
    private val repo: LedgerRepository
){
    suspend operator fun invoke(customer: Customer) =
        repo.updateCustomer(customer)
}


class deleteCustomerUseCase(
    private val repo: LedgerRepository
){
    suspend operator fun invoke(customer: Customer) =
        repo.deleteCustomer(customer)
}