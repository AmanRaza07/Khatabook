package com.mruraza.khata.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.Domain.usecases.AddItemsUseCase
import com.mruraza.khata.Domain.usecases.DeleteItemUseCase
import com.mruraza.khata.Domain.usecases.GetItemsUseCase
import com.mruraza.khata.Domain.usecases.UpdateItemUseCase
import com.mruraza.khata.data.local.DAO.ItemsDAO
import com.mruraza.khata.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ItemsViewModel @Inject constructor(
    private val getItemsUseCase: GetItemsUseCase,
    private val addItemsUseCase: AddItemsUseCase,
    private val updateItemUseCase: UpdateItemUseCase,
    private val deleteItemUseCase: DeleteItemUseCase
) : ViewModel() {

    val items= getItemsUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resources.Loading
    )

    fun onAddItemClick(item: Items) {
        viewModelScope.launch {
            addItemsUseCase.invoke(item)
        }
    }

    fun updateItem(items: Items){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                updateItemUseCase.invoke(items)
            }
        }
    }

    fun deleteItem(items: Items){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                deleteItemUseCase.invoke(items)
            }
        }
    }
}
