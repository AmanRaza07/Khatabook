package com.mruraza.khata.DI

import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.Domain.usecases.AddCustomerUseCase
import com.mruraza.khata.Domain.usecases.AddItemsUseCase
import com.mruraza.khata.Domain.usecases.AddTransactionUseCase
import com.mruraza.khata.Domain.usecases.DeleteItemUseCase
import com.mruraza.khata.Domain.usecases.DeleteTransactionUseCase
import com.mruraza.khata.Domain.usecases.GetAllTransactionUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerByIdUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerTransactionsUseCase
import com.mruraza.khata.Domain.usecases.GetCustomerTransactionsUseCaseNoLoading
import com.mruraza.khata.Domain.usecases.GetCustomersUseCase
import com.mruraza.khata.Domain.usecases.GetItemsUseCase
import com.mruraza.khata.Domain.usecases.GetItemsUseCaseNoLoading
import com.mruraza.khata.Domain.usecases.UpdateCustomerUseCase
import com.mruraza.khata.Domain.usecases.UpdateItemUseCase
import com.mruraza.khata.Domain.usecases.UpdateTransactionUseCase
import com.mruraza.khata.Domain.usecases.deleteCustomerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideAddCustomer(repo: LedgerRepository) =
        AddCustomerUseCase(repo)

    @Provides
    fun provideGetCustomers(repo: LedgerRepository) =
        GetCustomersUseCase(repo)

    @Provides
    fun provideAddTransaction(repo: LedgerRepository) =
        AddTransactionUseCase(repo)

    @Provides
    fun provideGetCustomerTransactions(repo: LedgerRepository) =
        GetCustomerTransactionsUseCase(repo)

    @Provides
    fun provideGetCustomerById(repo: LedgerRepository) =
        GetCustomerByIdUseCase(repo)

    @Provides
    fun providesGetItems(repo: LedgerRepository) =
        GetItemsUseCase(repo)

    @Provides
    fun providesAddItems(repo: LedgerRepository) =
        AddItemsUseCase(repo)

    @Provides
    fun providesGetItemsNoLoading(repo: LedgerRepository) =
        GetItemsUseCaseNoLoading(repo)

    @Provides
    fun providesGetAllTransactionUseCase(repo: LedgerRepository)=
        GetAllTransactionUseCase(repo)

    @Provides
    fun providesUpdateTransactionUseCase(repo: LedgerRepository)=
        UpdateTransactionUseCase(repo)

    @Provides
    fun providesDeleteTransactionUseCase(repo: LedgerRepository)=
        DeleteTransactionUseCase(repo)

    @Provides
    fun providesGetTransactionNoLoadingUseCase(repo: LedgerRepository)=
        GetCustomerTransactionsUseCaseNoLoading(repo)

    @Provides
    fun providesUpdateCustomerUseCase(repo: LedgerRepository)=
        UpdateCustomerUseCase(repo)

    @Provides
    fun providesDeleteCustomerUseCase(repo: LedgerRepository)=
        deleteCustomerUseCase(repo)

    @Provides
    fun providesItemsUpdateUseCase(repo: LedgerRepository)=
        UpdateItemUseCase(repo)

    @Provides
    fun providesItemsDeleteUseCase(repo: LedgerRepository)=
        DeleteItemUseCase(repo)
}
