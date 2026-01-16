package com.mruraza.khata.Domain.usecases

import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.Domain.repo.LedgerRepository
import com.mruraza.khata.utils.Resources
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AddItemsUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(item: Items) =
        repo.addItems(item)
}

class GetItemsUseCase(
    private val repo: LedgerRepository
) {
    operator fun invoke(): Flow<Resources<List<Items>>> = flow {
        emit(Resources.Loading)
        repo.getItems()
            .catch { e -> emit(Resources.Error(e)) }
            .collect { emit(Resources.Success(it)) }
    }.flowOn(Dispatchers.IO)
}


class GetItemsUseCaseNoLoading(
    private val repo: LedgerRepository
) {
    operator fun invoke(): Flow<List<Items>> = repo.getItems()
}

class UpdateItemUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(item: Items) =
        repo.updateItem(item)
}

class DeleteItemUseCase(
    private val repo: LedgerRepository
) {
    suspend operator fun invoke(item: Items) =
        repo.deleteItem(item)
}