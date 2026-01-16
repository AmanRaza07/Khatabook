package com.mruraza.khata.DI

import android.content.Context
import androidx.room.Room
import com.mruraza.khata.data.local.DAO.CustomerDao
import com.mruraza.khata.data.local.DAO.ItemsDAO
import com.mruraza.khata.data.local.DAO.RestoreDao
import com.mruraza.khata.data.local.DAO.TransactionDao
import com.mruraza.khata.data.local.Database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "khata_db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideCustomerDao(db: AppDatabase): CustomerDao = db.customerDao()

    @Provides
    @Singleton
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    @Singleton
    fun provideItemsDao(db: AppDatabase): ItemsDAO = db.itemsDao()

    @Provides
    @Singleton
    fun provideRestoreDao(db: AppDatabase): RestoreDao = db.restoreDao()

}