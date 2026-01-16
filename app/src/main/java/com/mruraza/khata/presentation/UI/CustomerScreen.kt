package com.mruraza.khata.presentation.UI

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.presentation.viewModel.CustomersViewModel
import com.mruraza.khata.utils.AppStrings
import com.mruraza.khata.utils.Resources

@Composable
fun CustomerScreen(
    navController: NavController,
    customerViewModel: CustomersViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    bottomPadding: PaddingValues = PaddingValues(0.dp)
) {
    val context = LocalContext.current
    val customerState by customerViewModel.allCustomers.collectAsStateWithLifecycle()
    var customersList: List<Customer> by remember { mutableStateOf(emptyList()) }
    val addCustomerEnable by customerViewModel.isAddCustomerEnabled.collectAsStateWithLifecycle()
    var onLongPressedCustomer by remember {
        mutableStateOf(
            Customer(
                id = -1,
                name = "",
                phone = "",
                address = "",
                totalBalance = 0.0
            )
        )
    }
    var updateCustomerDialogEnabled by remember { mutableStateOf(false) }

    when (customerState) {
        is Resources.Loading -> {}

        is Resources.Error -> {
            val message = (customerState as Resources.Error).throwable.message
            LaunchedEffect(message) {
                Toast.makeText(context, "Error: $message", Toast.LENGTH_SHORT).show()
            }
        }

        is Resources.Success -> {
            customersList = (customerState as Resources.Success<List<Customer>>).data
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { customerHeader(modifier = modifier) }) { innerPadding ->
        CustomerScreenLayout(
            modifier = Modifier.padding(innerPadding),
            customers = customersList,
            onAddCustomer = { customerViewModel.updateAddCustomerEnabled(true) },
            customerViewModel = customerViewModel,
            navController = navController,
            onDeleteCustomer = { customerViewModel.deleteCustomer(it) },
            onLongPress = {
                updateCustomerDialogEnabled = true
                onLongPressedCustomer = it
            },
            bottomPadding = bottomPadding
        )
    }
    if (updateCustomerDialogEnabled) {
        CustomerCardOnLongPress(
            customer = onLongPressedCustomer,
            onDismiss = { updateCustomerDialogEnabled = false },
            onSubmit = { name, phone, address, totalDue ->
                val customer = Customer(
                    id = onLongPressedCustomer.id,
                    name = name,
                    phone = phone,
                    address = address,
                    totalBalance = totalDue.toDoubleOrNull() ?: 0.0
                )
                customerViewModel.updateCustomer(customer)
                updateCustomerDialogEnabled = false
            }
        )
    }
    if (addCustomerEnable) {
        AddCustomerDialog(
            onDismiss = { customerViewModel.updateAddCustomerEnabled(false) },
            onSubmit = { name, phone, address, totalDue ->
                val customer = Customer(
                    name = name,
                    phone = phone,
                    address = address,
                    totalBalance = totalDue.toDoubleOrNull() ?: 0.0
                )
                customerViewModel.addCustomer(customer)
                customerViewModel.updateAddCustomerEnabled(false)
            })
    }
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
            title = { Text(AppStrings.delete_customer) },
            text = { Text(AppStrings.delete_customer_msg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text(AppStrings.delete, color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(AppStrings.cancel)
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
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CustomerScreenLayout(
    modifier: Modifier = Modifier,
    customers: List<Customer>,
    onAddCustomer: () -> Unit,
    customerViewModel: CustomersViewModel,
    navController: NavController,
    onDeleteCustomer: (Customer) -> Unit,
    onLongPress: (Customer) -> Unit = {},
    bottomPadding: PaddingValues
) {

    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(searchQuery, customers) {
        if (searchQuery.isBlank()) customers else {
            val q = searchQuery.lowercase()
            customers.filter {
                (it.name.lowercase().orEmpty().contains(q)) ||
                        (it.phone?.lowercase().orEmpty().contains(q)) ||
                        (it.address?.lowercase().orEmpty().contains(q))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .padding(bottom = bottomPadding.calculateBottomPadding())
    ) {

        // SEARCH + MENU ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f).padding(start = 8.dp).fillMaxWidth(),
                label = { Text("ग्राहकहरू खोज्नुहोस्") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            IconButton({
                onAddCustomer()
            }) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Add Customer"
                )
            }

        }

        if (filteredList.isEmpty()) {
            EmptyCustomerState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredList.size) { index ->
                    val transactions by customerViewModel.getTransactions(customerId = customers[index].id)
                        .collectAsStateWithLifecycle(listOf())
                    val allDue = transactions.sumOf { it.due }
                    SwipeLeftToDelete(
                        onDeleteConfirmed = {
                            onDeleteCustomer(filteredList[index])
                        }
                    ) {
                        CustomerCard(
                            modifier = Modifier.combinedClickable(
                                onLongClick = {
                                    onLongPress(filteredList[index])
                                },
                                onClick = {
                                    navController.navigate("transactions/${filteredList[index].id}")
                                }
                            ),
                            customer = filteredList[index],
                            due = allDue
                        )
                    }

                }
            }
        }
    }
}

@Composable
private fun CustomerCardOnLongPress(
    customer: Customer,
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, address: String, totalDue: String) -> Unit
) {
    var name by remember { mutableStateOf(customer.name) }
    var phone by remember { mutableStateOf(customer.phone) }
    var address by remember { mutableStateOf(customer.address) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("ग्राहक परिमार्जन गर्नुहोस्") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("नाम") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone ?: "",
                    onValueChange = { phone = it },
                    label = { Text("फोन") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = address ?: "",
                    onValueChange = { address = it },
                    label = { Text("ठेगाना") }
                )

//                OutlinedTextField(
//                    value = totalDue,
//                    onValueChange = { totalDue = it },
//                    label = { Text("Total Due Amount") },
//                    singleLine = true,
//                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(name, phone ?: "", address ?: "", "0")
                },
                enabled = name != ""
            ) {
                Text(AppStrings.submit)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.cancel)
            }
        }
    )
}

@Composable
private fun CustomerCard(
    modifier: Modifier = Modifier,
    customer: Customer,
    due: Double = 0.0
) {
    Box(
        Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .padding(end = 10.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF4A90E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (customer.name.firstOrNull()?.uppercase() ?: "-").toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = customer.name ?: "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )

                    Text(
                        text = customer.address ?: "No address",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }

                val color = when {
                    due > 5000 -> Color.Red
                    due > 2000 -> Color(0xFFFFA000)
                    else -> Color(0xFF43A047)
                }

                Text(
                    text = "Rs. $due",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }
        }
    }
}


@Composable
private fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, phone: String, address: String, totalDue: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var totalDue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.add_customer) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(AppStrings.name) },
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(AppStrings.phone) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(AppStrings.address) }
                )

            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(name, phone, address, totalDue)
                },
                enabled = name != ""
            ) {
                Text(AppStrings.submit)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.cancel)
            }
        }
    )
}

@Composable
private fun customerHeader(
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp),
            text = "ग्राहकहरू",
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        )

    }
}

@Composable
private fun EmptyCustomerState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "No matching customers",
            fontSize = 18.sp,
            color = Color.Gray
        )
    }
}
