package com.mruraza.khata.presentation.UI

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.presentation.viewModel.ItemsViewModel
import com.mruraza.khata.ui.theme.Grey2
import com.mruraza.khata.utils.AppStrings
import com.mruraza.khata.utils.Resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    navController: NavController,
    itemsViewModel: ItemsViewModel = hiltViewModel(),
    modifierFloatingAction: Modifier = Modifier,
    modifier: Modifier = Modifier,
    bottomPadding: PaddingValues
) {
    val itemsState by itemsViewModel.items.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var addDialogVisible by remember { mutableStateOf(false) }
    var editItemDialogVisible by remember { mutableStateOf(false) }
    var editItem by remember {
        mutableStateOf(
            Items(
                id = -1,
                name = "unknown",
                price = 0.0
            )
        )
    }

    if (addDialogVisible) {
        AddItemDialog(
            onDismiss = { addDialogVisible = false },
            onConfirm = {
                itemsViewModel.onAddItemClick(it)
                addDialogVisible = false
            }
        )
    }

    Scaffold(
        topBar = {
            Box(modifier = modifier.fillMaxWidth().padding(start = 16.dp)) {
                Text(
                    text = AppStrings.items,
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(modifier = modifierFloatingAction, onClick = {
                addDialogVisible = true
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Item"
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .padding(start = 16.dp, end = 16.dp)
                .padding(bottom = bottomPadding.calculateBottomPadding())
        ) {
            Spacer(Modifier.height(8.dp))
            // 🔎 Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                label = { Text(AppStrings.search_items) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ✔ Handle Resources State
            when (itemsState) {

                is Resources.Loading -> {
                    Text("Loading items...", style = MaterialTheme.typography.bodyMedium)
                }

                is Resources.Error -> {
                    Text(
                        text = (itemsState as Resources.Error).toString() ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is Resources.Success -> {
                    val items = (itemsState as Resources.Success<List<Items>>).data

                    val filteredItems = remember(searchQuery, items) {
                        items.filter { it.name.contains(searchQuery, ignoreCase = true) }
                    }

                    // 📦 Item List
                    LazyColumn {
                        items(filteredItems.size) { it ->
                            val item = filteredItems[it]
                            SwipeLeftToDelete(
                                onDeleteConfirmed = {
                                    itemsViewModel.deleteItem(item)
                                }
                            ) {
                                ItemRow(
                                    modifier = Modifier.clickable {
                                        editItemDialogVisible = true
                                        editItem = item
                                    },
                                    item = item
                                )
                            }
                        }
                    }
                    if (editItemDialogVisible) {
                        EditItemDialog(
                            item = editItem,
                            onDismiss = { editItemDialogVisible = false },
                            onConfirm = {
                                itemsViewModel.updateItem(it)
                                editItemDialogVisible = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemRow(
    modifier: Modifier = Modifier,
    item: Items
) {
    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    text = "Rs. ${item.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
            }
        }
    }

}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (Items) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.add_item) },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.name) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(AppStrings.price) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            Items(
                                id = 0,
                                name = name.trim(),
                                price = price.toDoubleOrNull() ?: 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun EditItemDialog(
    item: Items,
    onDismiss: () -> Unit,
    onConfirm: (Items) -> Unit
) {
    var name by remember { mutableStateOf(item.name) }
    var price by remember { mutableStateOf(item.price.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.edit_item) },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.name) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(AppStrings.price) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            Items(
                                id = item.id,
                                name = name.trim(),
                                price = price.toDoubleOrNull() ?: 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Save")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeLeftToDelete(
    modifier: Modifier = Modifier,
    onDeleteConfirmed: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDeleteDialog = true
                false // prevent auto dismiss
            } else {
                false
            }
        }
    )

    // ✅ Reset swipe when dialog is dismissed
    LaunchedEffect(showDeleteDialog) {
        if (!showDeleteDialog) {
            swipeState.reset()
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeState,
        enableDismissFromStartToEnd = false, // no RIGHT swipe
        enableDismissFromEndToStart = true,  // LEFT swipe
        backgroundContent = {
            DeleteBackground()
        }
    ) {
        content()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(AppStrings.delete_item) },
            text = { Text(AppStrings.delete_item_msg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun DeleteBackground() {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}
