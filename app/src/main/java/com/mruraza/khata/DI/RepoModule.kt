package com.mruraza.khata.DI

import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.data.local.DAO.CustomerDao
import com.mruraza.khata.data.local.DAO.ItemsDAO
import com.mruraza.khata.data.local.DAO.RestoreDao
import com.mruraza.khata.data.local.DAO.TransactionDao
import com.mruraza.khata.data.repoImpl.LedgerRepositoryImpl
import com.mruraza.khata.data.repoImpl.RestoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLedgerRepository(
        dao: CustomerDao,
        transactionDao: TransactionDao,
        itemsDao: ItemsDAO
    ): LedgerRepository = LedgerRepositoryImpl(dao,transactionDao=transactionDao, itemsDao = itemsDao)



    @Provides
    @Singleton
    fun providesRestoreReop(
        restoreDao: RestoreDao
    ): RestoreRepository{
        return RestoreRepository(restoreDao)
    }
}