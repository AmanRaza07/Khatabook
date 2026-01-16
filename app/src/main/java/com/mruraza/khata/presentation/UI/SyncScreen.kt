package com.mruraza.khata.presentation.UI

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.mruraza.khata.data.mapper.ItemsMapper.toEntity
import com.mruraza.khata.data.mapper.TransactionMapper.toEntity
import com.mruraza.khata.data.mapper.customerMapper.toEntity
import com.mruraza.khata.presentation.viewModel.SyncViewModel
import com.mruraza.khata.utils.Resources

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SyncScreen(
    navController: NavController,
    viewModel: SyncViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Setting") }) }
    ) { paddingValues ->
        OwnerProfileScreen(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            viewModel = viewModel
        )
    }
}


@Composable
fun OwnerProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SyncViewModel
) {
    val context = LocalContext.current

    val sharedPref = remember {
        context.getSharedPreferences("owner_prefs", Context.MODE_PRIVATE)
    }

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf(false) }

    var originalName by remember { mutableStateOf("") }
    var originalAddress by remember { mutableStateOf("") }
    var originalPhone by remember { mutableStateOf("") }


    // 🔹 Load saved data once
    LaunchedEffect(Unit) {
        name = sharedPref.getString("owner_name", "") ?: ""
        address = sharedPref.getString("owner_address", "") ?: ""
        phone = sharedPref.getString("owner_phone", "") ?: ""

        originalName = name
        originalAddress = address
        originalPhone = phone

        if (phone.isNotBlank()) {
            phoneError = !phone.matches(Regex("^[0-9]{10}$"))
        }
    }

    val isChanged by remember(name, address, phone) {
        derivedStateOf {
            name != originalName ||
                    address != originalAddress ||
                    phone != originalPhone
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(text = "Business Profile", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        // Business Name
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Business Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Address
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phone
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it
                phoneError = !it.matches(Regex("^[0-9]{10}$"))
            },
            label = { Text("Phone Number") },
            isError = phoneError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        if (phoneError) {
            Text(
                text = "Enter a valid 10-digit phone number",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            enabled = isChanged && !phoneError && name.isNotBlank() && address.isNotBlank(),
            onClick = {
                sharedPref.edit().apply {
                    putString("owner_name", name)
                    putString("owner_address", address)
                    putString("owner_phone", phone)
                    apply()
                }
                Toast.makeText(context, "Profile saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sync & Backup section
        Text(
            text = "Sync & Backup Settings",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your profile data is stored locally. You can sync or backup your data anytime.",
            style = MaterialTheme.typography.bodyMedium
        )

        ExcelContent(
            Modifier,
            viewModel
        )
    }
}


@Composable
fun ExcelContent(
    modifier: Modifier = Modifier,
    viewModel: SyncViewModel
) {
    val customersState by viewModel.customers.collectAsState()
    val itemsState by viewModel.allItems.collectAsState()
    val transactionsState by viewModel.allTransactions.collectAsState()

    var exporting by remember { mutableStateOf(false) }
    var restoring by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // -------- FILE PICKER --------
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            restoring = true
            viewModel.restoreFromExcel(
                context = context,
                uri = it,
                onDone = {
                    restoring = false
                    Toast.makeText(context, "Restore completed", Toast.LENGTH_LONG).show()
                },
                onError = { error ->
                    restoring = false
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    Box(modifier = Modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // -------- EXPORT BUTTON --------
            Box(modifier = Modifier.fillMaxWidth().padding(end=16.dp).weight(1f), contentAlignment = Alignment.CenterStart) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !exporting && !restoring,
                    onClick = {
                        if (
                            customersState is Resources.Success &&
                            itemsState is Resources.Success &&
                            transactionsState is Resources.Success
                        ) {
                            exporting = true

                            val customers =
                                (customersState as Resources.Success).data.map { it.toEntity() }
                            val items = (itemsState as Resources.Success).data.map { it.toEntity() }
                            val transactions =
                                (transactionsState as Resources.Success).data.map { it.toEntity() }

                            viewModel.exportToExcel(
                                context = context,
                                customers = customers,
                                items = items,
                                transactions = transactions,
                                onDone = {
                                    exporting = false
                                    Toast.makeText(
                                        context,
                                        "Backup saved to Downloads",
                                        Toast.LENGTH_LONG
                                    ).show()
                                },
                                onError = {
                                    exporting = false
                                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                ) {
                    Text("Export")
                }
            }
            Box(modifier = Modifier.fillMaxWidth().padding(start =16.dp).weight(1f), contentAlignment = Alignment.CenterEnd) {
                // -------- RESTORE BUTTON --------
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !exporting && !restoring,
                    onClick = {
                        showConfirmDialog = true
                    }
                ) {
                    Text("Restore")
                }
            }
        }

        // -------- CONFIRMATION DIALOG --------
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirm Restore") },
                text = { Text("This will erase your current database and restore from the selected Excel file. Are you sure?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showConfirmDialog = false
                            filePicker.launch(
                                arrayOf(
                                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                )
                            )
                        }
                    ) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("No")
                    }
                }
            )
        }

        // -------- PROGRESS OVERLAY --------
        if (
            exporting ||
            restoring ||
            customersState is Resources.Loading ||
            itemsState is Resources.Loading ||
            transactionsState is Resources.Loading
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when {
                            exporting -> "Exporting backup..."
                            restoring -> "Restoring database..."
                            else -> "Preparing data..."
                        },
                        color = Color.White
                    )
                }
            }
        }
    }
}

