package com.mruraza.khata.presentation.UI

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import androidx.core.view.doOnLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mruraza.khata.Domain.model.Customer
import com.mruraza.khata.Domain.model.Items
import com.mruraza.khata.Domain.model.Transaction
import com.mruraza.khata.Domain.model.TransactionItem
import com.mruraza.khata.presentation.viewModel.CustomerTransactionViewModel
import com.mruraza.khata.ui.theme.LocalAppGray
import com.mruraza.khata.ui.theme.lightGreen
import com.mruraza.khata.ui.theme.lightRed
import com.mruraza.khata.utils.AppStrings
import com.mruraza.khata.utils.BSDate
import com.mruraza.khata.utils.Resources
import com.mruraza.khata.utils.bsMonthIndex
import com.mruraza.khata.utils.convertToBS
import com.mruraza.khata.utils.getOwnerProfile
import com.mruraza.khata.utils.nepaliMonthFromNumber
import com.mruraza.khata.utils.toComparable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@Composable
fun CustomerTransactions(
    navController: NavController,
    customerId: Int,
    customerTransactionViewModel: CustomerTransactionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
    activity: Activity,
    bottomPadding: PaddingValues
) {

    var showFilter by remember { mutableStateOf(false) }

    val customerState by customerTransactionViewModel
        .getCustomerById(customerId)
        .collectAsStateWithLifecycle()

    val transactionState by customerTransactionViewModel
        .getTransactions(customerId)
        .collectAsStateWithLifecycle()
    val allItems by customerTransactionViewModel.allItems()
        .collectAsStateWithLifecycle(listOf<Items>())

    val filteredTransactions by customerTransactionViewModel
        .filteredTransactions
        .collectAsStateWithLifecycle()

    val activeFilterRange by customerTransactionViewModel
        .activeFilterRange
        .collectAsStateWithLifecycle()

    var showPaidDialog by remember { mutableStateOf(false) }
    var showEditTransaction by remember { mutableStateOf(false) }
    var onEditTransaction by remember {
        mutableStateOf(
            Transaction(
                id = -1,
                customerId = 0,
                timestamp = 0,
                paid = 0.0,
                due = 0.0,
                discount = 0.0,
                items = listOf(),
                note = null
            )
        )
    }


    Scaffold(
        topBar = {
            topBarCustomerTransaction(
                onBackPress = { navController.popBackStack() },
                onFilterPress = {
                    showFilter = true
                },
                onClearFilter = { customerTransactionViewModel.clearFilter() },
                activeFilterRange = activeFilterRange,
                onPaidPress = {
                    showPaidDialog = true
                },
                modifier = modifier
            )
        }
    ) { innerPadding ->
        when {
            customerState is Resources.Loading ||
                    transactionState is Resources.Loading -> {
                LoadingUI(innerPadding)
            }

            customerState is Resources.Error -> {
                ErrorUI(
                    padding = innerPadding,
                    msg = (customerState as Resources.Error).toString()
                )
            }

            transactionState is Resources.Error -> {
                ErrorUI(
                    padding = innerPadding,
                    msg = (transactionState as Resources.Error).toString()
                )
            }


            customerState is Resources.Success &&
                    transactionState is Resources.Success -> {

                val customer = (customerState as Resources.Success).data
                val transactions = (transactionState as Resources.Success).data
                val transactionsToShow =
                    filteredTransactions ?: transactions
                val totalDue = customerTransactionViewModel.calculateTotalDue(transactions)

                PaidAmountDialog(
                    show = showPaidDialog,
                    totalDue = totalDue,
                    onDismiss = { showPaidDialog = false },
                    onConfirm = { paidAmount ->
                        customerTransactionViewModel.applyPayment(
                            transactions = transactions,
                            paidAmount = paidAmount
                        )
                        showPaidDialog = false
                    }
                )

                customerTransactionScreen(
                    modifier = Modifier.padding(innerPadding),
                    customer = customer,
                    transactions = transactionsToShow,
                    onAddTransaction = { customerTransactionViewModel.addTransaction(it) },
                    onDeleteTransaction = { customerTransactionViewModel.deleteTransaction(it) },
                    onUpdateTransaction = {
                        customerTransactionViewModel.updateTransactionNoSuspend(
                            tx = Transaction(
                                id = it.id,
                                customerId = it.customerId,
                                timestamp = it.timestamp,
                                paid = it.paid + it.due,
                                due = 0.0,
                                discount = it.discount,
                                items = it.items,
                                note = it.note
                            )
                        )
                    },
                    onLongClickTransaction = {
                        onEditTransaction = it
                        showEditTransaction = true
                    },
                    itemList = allItems,
                    activity = activity,
                    padding = bottomPadding,
                    totalDue = totalDue
                )

                if (showEditTransaction) {
                    onLongClickTransaction(
                        transaction = onEditTransaction,
                        customerId = customerId,
                        onDismiss = { showEditTransaction = false },
                        itemList = allItems,
                        onSave = { it ->
                            customerTransactionViewModel.updateTransactionNoSuspend(
                                tx = it
                            )
                            showEditTransaction = false
                        }
                    )
                }

                NepaliDateRangeDialog(
                    show = showFilter,
                    onDismiss = { showFilter = false },
                    onApply = { fromBs, toBs ->

                        val filtered = filterTransactionsByBsDate(
                            transactions = transactions,
                            fromBs = fromBs,
                            toBs = toBs
                        )
                        customerTransactionViewModel.setFilteredTransactions(filtered)
                        customerTransactionViewModel.setFilterRange(fromBs, toBs)
                    }
                )
            }
        }
    }
}

@Composable
fun PaidAmountDialog(
    show: Boolean,
    totalDue: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    if (!show) return

    var amount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.enter_paid_amount) },
        text = {
            Column {
                Text("${AppStrings.total_due}: ₹$totalDue")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.matches(Regex("""\d*\.?\d*"""))) {
                            amount = it
                        }
                    },
                    label = { Text(AppStrings.amount) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = amount.toDoubleOrNull()?.let { it > 0 && it <= totalDue } == true,
                onClick = {
                    onConfirm(amount.toDouble())
                    onDismiss()
                }
            ) {
                Text(AppStrings.apply)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.cancel)
            }
        }
    )
}


fun filterTransactionsByBsDate(
    transactions: List<Transaction>,
    fromBs: BSDate,
    toBs: BSDate
): List<Transaction> {

    val fromValue = fromBs.toComparable()
    val toValue = toBs.toComparable()

    return transactions.filter { tx ->
        val txBs = convertToBS(tx.timestamp)
        val txValue = txBs.toComparable()
        txValue in fromValue..toValue
    }
}


@Composable
fun LoadingUI(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorUI(padding: PaddingValues, msg: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Text(msg ?: AppStrings.error_something_went_wrong)
    }
}


@Composable
private fun customerTransactionScreen(
    modifier: Modifier,
    customer: Customer,
    transactions: List<Transaction>,
    onAddTransaction: (Transaction) -> Unit = {},
    onDeleteTransaction: (Transaction) -> Unit = {},
    onUpdateTransaction: (Transaction) -> Unit = {},
    onLongClickTransaction: (Transaction) -> Unit = {},
    itemList: List<Items>,
    activity: Activity,
    padding: PaddingValues,
    totalDue: Double
) {
    var isTransactionDetailViewVisible by remember { mutableStateOf(false) }
    var transactionId by remember { mutableStateOf(0) }
    var isAddTransactionEnabled by remember { mutableStateOf(false) }
    val sortedTransaction = transactions.sortedByDescending { it.timestamp }
    Column(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface).padding(
            bottom = padding.calculateBottomPadding()
        )
    ) {
        customerInfoCard(customer = customer, transactions = transactions)
        DueField(onAddTransaction = { isAddTransactionEnabled = true })
        transactionTableScreen()
        LazyColumn {
            items(sortedTransaction.size) {
                SwipeLeftRightActions(
                    modifier = Modifier,
                    onDeleteConfirmed = {
                        onDeleteTransaction(sortedTransaction[it])
                    },
                    onMarkAsPaidConfirmed = {
                        onUpdateTransaction(sortedTransaction[it])
                    }
                ) {
                    TransactionTableLayout(
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                isTransactionDetailViewVisible = true
                                transactionId = it
                            },
                            onLongClick = {
                                onLongClickTransaction(sortedTransaction[it])
                            }
                        ),
                        tx = sortedTransaction[it]
                    )
                }
            }
        }
    }
    if (isTransactionDetailViewVisible) {
        OnTransactionClickDialog(
            activity = activity,
            customer = customer,
            transaction = sortedTransaction[transactionId],
            onDismiss = { isTransactionDetailViewVisible = false },
            onConfirm = { isTransactionDetailViewVisible = false },
            modifier = Modifier,
            totalDue = totalDue
        )
    }
    if (isAddTransactionEnabled) {
        AddTransactionDialog(
            customerId = customer.id,
            onDismiss = { isAddTransactionEnabled = false },
            onSave = {
                isAddTransactionEnabled = false
                onAddTransaction(it)
            },
            itemList = itemList
        )

    }
}

@Composable
fun onLongClickTransaction(
    transaction: Transaction,
    customerId: Int,
    onDismiss: () -> Unit,
    itemList: List<Items>,
    onSave: (Transaction) -> Unit
) {
    var paid by remember { mutableStateOf(transaction.paid.toString()) }
    var discount by remember { mutableStateOf(transaction.discount.toString()) }
    var note by remember { mutableStateOf(transaction.note) }
    var isDiscountExpanded by remember { mutableStateOf(false) }
    var isNoteExpanded by remember { mutableStateOf(false) }
    // Transaction Items List
    var items by remember { mutableStateOf(transaction.items) }
    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(AppStrings.edit_transaction)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---------- Items Section ----------
                Text(AppStrings.items, style = MaterialTheme.typography.titleMedium)

                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${item.name} - Rs. ${item.price} × ${item.quantity}"
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                editingIndex = index
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                items = items.toMutableList().also { it.removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
//                        IconButton(onClick = {
//                            items = items.toMutableList().also { it.removeAt(index) }
//                        }) {
//                            Icon(Icons.Default.Delete, contentDescription = "Delete")
//                        }
                    }
                }

                AddItemButton(
                    onAddItem = { newItem ->
                        items = items + newItem
                    },
                    itemsList = itemList
                )
                HorizontalDivider()
                // ----- Inputs -----
                OutlinedTextField(
                    value = paid,
                    onValueChange = { paid = it },
                    label = { Text(AppStrings.paid_label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

//                collapseAndExpandButton(
//                    title = "Discount",
//                    onClick = { isDiscountExpanded = !isDiscountExpanded }
//                )
//                if (isDiscountExpanded) {
//                    OutlinedTextField(
//                        value = discount,
//                        onValueChange = { discount = it },
//                        label = { Text("Discount (Optional)") },
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
                collapseAndExpandButton(
                    title = AppStrings.note,
                    onClick = { isNoteExpanded = !isNoteExpanded }
                )
                if (isNoteExpanded) {
                    OutlinedTextField(
                        value = note ?: "",
                        onValueChange = { note = it },
                        label = { Text(AppStrings.note_optional) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = items.isNotEmpty(),
                onClick = {
                    val total: Double = items.sumOf { it.price * it.quantity }
                    if (discount.isEmpty())
                        discount = "0"
                    if (paid.isEmpty())
                        paid = "0"
                    val due = total - discount.toDouble() - paid.toDouble()
                    val tx = Transaction(
                        id = transaction.id,
                        customerId = customerId,
                        paid = paid.toDoubleOrNull() ?: 0.0,
                        due = due,
                        discount = discount.toDoubleOrNull() ?: 0.0,
                        items = items,
                        note = note?.ifBlank { null },
                        timestamp = System.currentTimeMillis()
                    )
                    onSave(tx)
                }
            ) {
                Text(AppStrings.update)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.cancel)
            }
        }
    )
    if (showEditDialog && editingIndex != null) {
        val item = items[editingIndex!!]

        EditItemsSelected(
            itemName = item.name,
            itemQuant = item.quantity,
            itemPrice = item.price,
            itemsList = itemList,
            onConfirm = { updatedItem ->

                items = items.toMutableList().also {
                    it[editingIndex!!] = updatedItem
                }

                showEditDialog = false
                editingIndex = null
            },
            onDismiss = {
                showEditDialog = false
                editingIndex = null
            }
        )
    }

}


fun formatBsRange(range: Pair<BSDate, BSDate>): String {
    val (from, to) = range
    return "${from.day} ${from.month} ${from.year} - " +
            "${to.day} ${to.month} ${to.year}"
}


@Composable
private fun topBarCustomerTransaction(
    onBackPress: () -> Unit,
    onFilterPress: () -> Unit,
    onPaidPress: () -> Unit,
    onClearFilter: () -> Unit,
    activeFilterRange: Pair<BSDate, BSDate>?,
    modifier: Modifier = Modifier
) {
    val gray = LocalAppGray.current
    Row(
        modifier = modifier.fillMaxWidth().background(gray.bg2).padding(start = 8.dp, end = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = { onBackPress() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "back"
                    )
                }
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = AppStrings.transactions,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (activeFilterRange == null) {

                IconButton(onClick = onFilterPress) {
                    Icon(
                        imageVector = Icons.Default.FilterAlt,
                        contentDescription = "Filter"
                    )
                }

            } else {

                Text(
                    text = formatBsRange(activeFilterRange),
                    modifier = Modifier
                        .clickable { onFilterPress() }
                        .padding(end = 6.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear Filter",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onClearFilter() }
                        .padding(end = 4.dp)
                        .background(Color.Red)
                )
            }

            Icon(
                imageVector = Icons.Default.Paid,
                contentDescription = "Mark Paid",
                modifier = Modifier
                    .clickable { onPaidPress() }
            )
        }

    }
}

fun clampInt(value: String, min: Int, max: Int): String {
    val intVal = value.toIntOrNull() ?: return ""
    return intVal.coerceIn(min, max).toString()
}

fun validateYear(year: String): String =
    clampInt(year, 1970, 2099)

fun validateMonth(month: String): String =
    clampInt(month, 1, 12)

fun validateDay(day: String): String =
    clampInt(day, 1, 32)

fun pad2(value: String): String =
    value.toIntOrNull()?.toString()?.padStart(2, '0') ?: ""


@Composable
fun NepaliDateRangeDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onApply: (fromBs: BSDate, toBs: BSDate) -> Unit
) {
    if (!show) return
    val todayBsDate = convertToBS(System.currentTimeMillis())
    var fromYear by remember { mutableStateOf("") }
    var fromMonth by remember { mutableStateOf("") }
    var fromDay by remember { mutableStateOf("") }

    var toYear by remember { mutableStateOf("${todayBsDate.year}") }
    var toMonth by remember { mutableStateOf("${bsMonthIndex(todayBsDate.month) + 1}") }
    var toDay by remember { mutableStateOf("${todayBsDate.day}") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(AppStrings.filter_by_date_bs) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                Text(AppStrings.from)
                DateRow(fromYear, fromMonth, fromDay) { y, m, d ->
                    fromYear = validateYear(y)
                    fromMonth = pad2(validateMonth(m))
                    fromDay = pad2(validateDay(d))
                }

                Text(AppStrings.to)
                DateRow(toYear, toMonth, toDay) { y, m, d ->
                    toYear = validateYear(y)
                    toMonth = pad2(validateMonth(m))
                    toDay = pad2(validateDay(d))
                }
            }

        },
        confirmButton = {
            val isValid =
                fromYear.toIntOrNull() in 1970..2099 &&
                        toYear.toIntOrNull() in 1970..2099 &&
                        fromMonth.toIntOrNull() in 1..12 &&
                        toMonth.toIntOrNull() in 1..12 &&
                        fromDay.toIntOrNull() in 1..32 &&
                        toDay.toIntOrNull() in 1..32

            TextButton(
                enabled = isValid && listOf(
                    fromYear, fromMonth, fromDay,
                    toYear, toMonth, toDay
                ).all { it.isNotBlank() },
                onClick = {
                    onApply(
                        BSDate(
                            year = fromYear.toInt(),
                            month = nepaliMonthFromNumber(fromMonth),
                            day = fromDay.toInt()
                        ),
                        BSDate(
                            year = toYear.toInt(),
                            month = nepaliMonthFromNumber(toMonth),
                            day = toDay.toInt()
                        )
                    )
                    onDismiss()
                }
            ) {
                Text(AppStrings.apply)
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
fun DateRow(
    year: String,
    month: String,
    day: String,
    onChange: (String, String, String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

        DateBox(
            value = year,
            label = "YYYY",
            maxLength = 4
        ) { onChange(it, month, day) }

        DateBox(
            value = month,
            label = "MM",
            maxLength = 2
        ) { onChange(year, it, day) }

        DateBox(
            value = day,
            label = "DD",
            maxLength = 2
        ) { onChange(year, month, it) }
    }
}

@Composable
fun DateBox(
    value: String,
    label: String,
    maxLength: Int,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.length <= maxLength && it.all { ch -> ch.isDigit() }) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        modifier = Modifier.width(90.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}


@Composable
private fun customerInfoCard(
    modifier: Modifier = Modifier,
    customer: Customer,
    transactions: List<Transaction>
) {
    val gray = LocalAppGray.current
    val totalDue = transactions.sumOf { it.due }
    Box(
        Modifier
            .fillMaxWidth()
            .background(gray.bg2)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = gray.bg2
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    // 🔵 Avatar initial
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .padding(end = 10.dp)
                            .clip(RoundedCornerShape(48.dp))
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
                            fontWeight = FontWeight.Medium
                        )
                    }

                }
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "${customer.phone}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // 🔺 Small Red Triangle
                        Canvas(
                            modifier = Modifier
                                .size(12.dp)
                                .padding(end = 4.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(size.width / 2, 0f)
                                lineTo(0f, size.height)
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(path = path, color = Color.Red)
                        }

                        // 💰 Amount
                        Text(
                            text = "Rs $totalDue",
                            style = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
                            color = Color.Red
                        )
                    }
                }

            }
        }
    }

}

@Composable
private fun DueField(onAddTransaction: () -> Unit) {
    val gray = LocalAppGray.current
    Box(modifier = Modifier.fillMaxWidth().background(gray.bg0)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)) // shape first
                .background(gray.bg2)
        ) {
            Box() {
                AddTransactionButton(modifier = Modifier, onClick = { onAddTransaction() })
            }
        }
    }
}

@Composable
private fun AddTransactionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val gray = LocalAppGray.current
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = gray.bg2,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = gray.bg4
        ),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = AppStrings.add_transaction,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = AppStrings.add_transaction,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun transactionTableScreen() {
    val gray = LocalAppGray.current
    Box(modifier = Modifier.fillMaxWidth().background(gray.bg0)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = AppStrings.date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = AppStrings.paid, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = AppStrings.due, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = AppStrings.note, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

//val converter = NepaliCalendarConverter(dateConfigMap)

@Composable
private fun TransactionTableLayout(
    modifier: Modifier = Modifier,
    tx: Transaction,
) {
    //val converter = NepaliCalendarConverter(dateConfigMap)
    val gray = LocalAppGray.current
    Box(modifier = modifier.fillMaxWidth()) {
        val cardColors = if (tx.due.toInt() == 0) {
            CardDefaults.cardColors(containerColor = lightGreen)
        } else {
            CardDefaults.cardColors(containerColor = lightRed)
        }
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(8.dp),
            colors = cardColors
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1️⃣ Date in BS (1f)
                Box(
                    modifier = Modifier.weight(2f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    //val bsDate = converter.toNepaliDate(tx.timestamp)
                    val bsDate = convertToBS(tx.timestamp)   // returns (day, month, year)

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box() {
                            Box(
                                modifier = Modifier.background(gray.bg4).padding(4.dp)
                                    .clip(RoundedCornerShape(8.dp)).height(24.dp).width(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    text = "${bsDate.day}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.padding(2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = bsDate.month,
                                fontSize = 10.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "${bsDate.year}",
                                fontSize = 10.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                // 2️⃣ Paid Amount (2f)
                Box(
                    modifier = Modifier.weight(2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${tx.paid}",
                        fontSize = 14.sp,
                        color = Color(0xFF018790), // Teal
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // 3️⃣ Due Amount (2f)
                Box(
                    modifier = Modifier.weight(2f),
                    contentAlignment = Alignment.Center
                ) {
                    if (tx.due > 0) {
                        Text(
                            text = "${tx.due}",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Text(
                            text = "-",
                            color = Color.Black
                        )
                    }
                }

                // 4️⃣ Note (2f)
                Box(
                    modifier = Modifier.weight(2f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tx.note ?: "",
                        maxLines = 1,
                        color = Color.Black,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun OnTransactionClickDialog(
    activity: Activity,
    customer: Customer,
    transaction: Transaction,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
    totalDue: Double
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = { onDismiss() }) {

        Surface(
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                // ---------- HEADER ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val bsDate = convertToBS(transaction.timestamp)
                        Text(
                            text = bsDate.year.toString() + " " + bsDate.month + " " + bsDate.day.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            val bsDate = convertToBS(transaction.timestamp)
                            saveComposableAsPdf(
                                activity = activity,
                                fileName = "${customer.name} ${bsDate.day} ${bsDate.month} ${bsDate.year} ${System.currentTimeMillis()}transaction.pdf",
                                content = {
                                    OnTransactionClickDialogContent(
                                        customer = customer,
                                        transaction = transaction,
                                        modifier = Modifier,
                                        totalDue = totalDue
                                    )
                                }
                            )
                        }) {
                            Icon(
                                Icons.Default.PictureAsPdf,
                                contentDescription = "Export PDF"
                            )
                        }
                        IconButton(onClick = { /* share */
                            returnComposableAsPdf(activity = activity, content = {
                                OnTransactionClickDialogContent(
                                    customer = customer,
                                    transaction = transaction,
                                    modifier = Modifier,
                                    totalDue = totalDue
                                )
                            }) { pdfBytes ->
                                val pdfFile = File(context.cacheDir, "temp_bill.pdf")
                                pdfFile.writeBytes(pdfBytes)

                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "${context.packageName}.fileprovider",
                                    pdfFile
                                )

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }

                                // Use context.startActivity with chooser Intent
                                val chooser = Intent.createChooser(shareIntent, "Share PDF")
                                context.startActivity(chooser)
                            }
                        }) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(12.dp))


                // ---------- ITEMS GRID ----------
                Text(
                    AppStrings.items,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(transaction.items.size) { it ->
                        val item = transaction.items[it]

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(item.name, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.width(2.dp))
                                Text("${AppStrings.quantity}: ${item.quantity}", fontSize = 12.sp)
                            }

                            Text("Rs. ${item.price * item.quantity}")
                        }

                        Spacer(Modifier.height(8.dp))

                    }
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))


                        // ---------- SUMMARY ----------
                        val subtotal = transaction.items.sumOf { it.price * it.quantity }
                        val discount = transaction.discount
                        val finalAmount = subtotal - discount - transaction.paid

                        SummaryRow(title = AppStrings.subtotal, value = subtotal)
                        SummaryRow(title = AppStrings.paid_label, value = transaction.paid)
                        SummaryRow(title = AppStrings.final_amount, value = finalAmount)

                        Spacer(modifier = Modifier.height(10.dp))

                        Divider()

                        Spacer(modifier = Modifier.height(10.dp))
                        SummaryRow(title = AppStrings.total_due, value = totalDue)
                        Divider()

                        Spacer(modifier = Modifier.height(10.dp))


                        // ---------- NOTE ----------
                        if (!transaction.note.isNullOrEmpty()) {
                            Text(
                                "${AppStrings.note}: ${transaction.note}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }


                        // ---------- ACTION BUTTONS ----------
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onDismiss) {
                                Text("Close")
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(onClick = onConfirm) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeLeftRightActions(
    modifier: Modifier = Modifier,
    onDeleteConfirmed: () -> Unit,
    onMarkAsPaidConfirmed: () -> Unit,
    content: @Composable () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showPaidDialog by remember { mutableStateOf(false) }

    val swipeState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    // ⬅️ LEFT → Delete
                    showDeleteDialog = true
                    false
                }

                SwipeToDismissBoxValue.StartToEnd -> {
                    // ➡️ RIGHT → Mark as Paid
                    showPaidDialog = true
                    false
                }

                else -> false
            }
        }
    )

    // Reset swipe after action
    LaunchedEffect(showDeleteDialog) {
        if (!showDeleteDialog) swipeState.reset()
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = swipeState,
        enableDismissFromStartToEnd = true,  // RIGHT swipe
        enableDismissFromEndToStart = true,  // LEFT swipe
        backgroundContent = {
            when (swipeState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    MarkAsPaidBackground() // ➡️ YOU will implement
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    DeleteBackground() // ⬅️ existing red bg
                }

                else -> {}
            }
        }
    ) {
        content()
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(AppStrings.delete_transaction) },
            text = { Text(AppStrings.delete_transaction_msg) },
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
    // Paid confirmation dialog
    if (showPaidDialog) {
        AlertDialog(
            onDismissRequest = { showPaidDialog = false },
            title = { Text(AppStrings.clear_due) },
            text = { Text(AppStrings.clear_due_confirmation_msg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPaidDialog = false
                        onMarkAsPaidConfirmed()
                    }
                ) {
                    Text(AppStrings.clear_due, color = Color.Green)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPaidDialog = false }) {
                    Text(AppStrings.cancel)
                }
            }
        )
    }
}

@Composable
private fun MarkAsPaidBackground() {
    Card(
        modifier = Modifier.fillMaxSize().padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Green)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Paid,
                    contentDescription = "Mark As Paid",
                    tint = Color.White
                )
            }

            Text(
                modifier = Modifier
                    .padding(start = 8.dp),
                text = AppStrings.mark_as_paid,
                color = Color.Black,
                textAlign = TextAlign.Start
            )
        }

    }
}

@Composable
private fun DeleteBackground() {
    Card(
        modifier = Modifier.fillMaxSize().padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
            Icon(
                modifier = Modifier.padding(end = 16.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}


@Composable
private fun SummaryRow(title: String, value: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.SemiBold)
        Text("Rs. $value", fontWeight = FontWeight.Bold)
    }
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun collapseAndExpandButton(
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var collapsed by remember { mutableStateOf(true) }
    Row(modifier = modifier.fillMaxWidth().clickable {
        collapsed = !collapsed
        onClick()
    }) {
        Row(horizontalArrangement = Arrangement.Start) {
            if (collapsed) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Remove Transaction",
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EditItemsSelected(
    itemName: String,
    itemQuant: Double,
    itemsList: List<Items>,
    onConfirm: (TransactionItem) -> Unit,
    onDismiss: () -> Unit,
    itemPrice: Double
) {
    var name by remember { mutableStateOf(TextFieldValue(itemName)) }
    var qty by remember { mutableStateOf(itemQuant.toString()) }
    var price by remember { mutableStateOf(itemPrice.toString()) }
    var showSuggestions by remember { mutableStateOf(false) }

    // For dropdown width
    var textFieldWidth by remember { mutableStateOf(0.dp) }

    // Filter items
    val filtered = remember(name.text) {
        if (name.text.isBlank()) emptyList()
        else itemsList.filter {
            it.name.contains(name.text, ignoreCase = true)
        }
    }
    val density = LocalDensity.current
    AlertDialog(
        onDismissRequest = {},
        title = { Text(AppStrings.add_item) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {

                // ---------- NAME WITH AUTOCOMPLETE ----------
                Box {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showSuggestions = true
                        },
                        label = { Text(AppStrings.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldWidth = with(density) { coordinates.size.width.toDp() }
                            }
                    )

                    DropdownMenu(
                        expanded = showSuggestions && filtered.isNotEmpty(),
                        onDismissRequest = { showSuggestions = false },
                        modifier = Modifier.width(textFieldWidth)
                    ) {
                        filtered.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    // Set name & move cursor to end
                                    name = TextFieldValue(
                                        text = item.name,
                                        selection = TextRange(item.name.length)
                                    )

                                    // Auto fill price
                                    price = item.price.toString()

                                    showSuggestions = false
                                }
                            )
                        }
                    }
                }

                // ---------- PRICE ----------
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(AppStrings.paid_label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---------- QUANTITY ----------
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text(AppStrings.quantity) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.text.isNotBlank() && price.isNotBlank(),
                onClick = {
                    onConfirm(
                        TransactionItem(
                            name = name.text,
                            price = price.toDoubleOrNull() ?: 0.0,
                            quantity = qty.toDoubleOrNull() ?: 1.0
                        )
                    )
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

}

@Composable
private fun AddTransactionDialog(
    customerId: Int,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    itemList: List<Items>
) {
    var paid by remember { mutableStateOf("") }
    var discount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isDiscountExpanded by remember { mutableStateOf(false) }
    var isNoteExpanded by remember { mutableStateOf(false) }
    // Transaction Items List
    var items by remember { mutableStateOf(listOf<TransactionItem>()) }

    var editingIndex by remember { mutableStateOf<Int?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }


    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(AppStrings.add_transaction)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ---------- Items Section ----------
                Text(AppStrings.items, style = MaterialTheme.typography.titleMedium)

                items.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "${item.name} - Rs. ${item.price} × ${item.quantity}"
                        )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                editingIndex = index
                                showEditDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit")
                            }
                            IconButton(onClick = {
                                items = items.toMutableList().also { it.removeAt(index) }
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                            }
                        }
                    }
                }

                AddItemButton(
                    onAddItem = { newItem ->
                        items = items + newItem
                    },
                    itemsList = itemList
                )
                HorizontalDivider()
                // ----- Inputs -----
                OutlinedTextField(
                    value = paid,
                    onValueChange = { paid = it },
                    label = { Text(AppStrings.paid_label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

//                collapseAndExpandButton(
//                    title = "Discount",
//                    onClick = { isDiscountExpanded = !isDiscountExpanded }
//                )
//                if (isDiscountExpanded) {
//                    OutlinedTextField(
//                        value = discount,
//                        onValueChange = { discount = it },
//                        label = { Text("Discount (Optional)") },
//                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                        modifier = Modifier.fillMaxWidth()
//                    )
//                }
                collapseAndExpandButton(
                    title = AppStrings.note,
                    onClick = { isNoteExpanded = !isNoteExpanded }
                )
                if (isNoteExpanded) {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        label = { Text(AppStrings.note_optional) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = items.isNotEmpty(),
                onClick = {
                    val total: Double = items.sumOf { it.price * it.quantity }
                    if (discount.isEmpty())
                        discount = "0"
                    if (paid.isEmpty())
                        paid = "0"
                    val due = total - discount.toDouble() - paid.toDouble()
                    val tx = Transaction(
                        id = 0,
                        customerId = customerId,
                        paid = paid.toDoubleOrNull() ?: 0.0,
                        due = due,
                        discount = discount.toDoubleOrNull() ?: 0.0,
                        items = items,
                        note = note.ifBlank { null },
                        timestamp = System.currentTimeMillis()
                    )
                    onSave(tx)
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
    if (showEditDialog && editingIndex != null) {
        val item = items[editingIndex!!]

        EditItemsSelected(
            itemName = item.name,
            itemQuant = item.quantity,
            itemPrice = item.price,
            itemsList = itemList,
            onConfirm = { updatedItem ->

                items = items.toMutableList().also {
                    it[editingIndex!!] = updatedItem
                }

                showEditDialog = false
                editingIndex = null
            },
            onDismiss = {
                showEditDialog = false
                editingIndex = null
            }
        )
    }

}

@Composable
private fun AddItemButton(
    onAddItem: (TransactionItem) -> Unit,
    itemsList: List<Items>
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AddItemDialog(
            itemsList = itemsList,
            onDismiss = { showDialog = false },
            onConfirm = { item ->
                onAddItem(item)
                showDialog = false
            }
        )
    }

    Button(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(AppStrings.add_item)
    }
}

@Composable
fun AddItemDialog(
    itemsList: List<Items>,
    onDismiss: () -> Unit,
    onConfirm: (TransactionItem) -> Unit
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var price by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }

    var showSuggestions by remember { mutableStateOf(false) }

    // For dropdown width
    var textFieldWidth by remember { mutableStateOf(0.dp) }

    // Filter items
    val filtered = remember(name.text) {
        if (name.text.isBlank()) emptyList()
        else itemsList.filter {
            it.name.contains(name.text, ignoreCase = true)
        }
    }
    val density = LocalDensity.current
    AlertDialog(
        onDismissRequest = {},
        title = { Text(AppStrings.add_item) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            ) {

                // ---------- NAME WITH AUTOCOMPLETE ----------
                Box {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            showSuggestions = true
                        },
                        label = { Text(AppStrings.name) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldWidth = with(density) { coordinates.size.width.toDp() }
                            }
                    )

                    DropdownMenu(
                        expanded = showSuggestions && filtered.isNotEmpty(),
                        onDismissRequest = { showSuggestions = false },
                        modifier = Modifier.width(textFieldWidth)
                    ) {
                        filtered.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.name) },
                                onClick = {
                                    // Set name & move cursor to end
                                    name = TextFieldValue(
                                        text = item.name,
                                        selection = TextRange(item.name.length)
                                    )

                                    // Auto fill price
                                    price = item.price.toString()

                                    showSuggestions = false
                                }
                            )
                        }
                    }
                }

                // ---------- PRICE ----------
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(AppStrings.price) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---------- QUANTITY ----------
                OutlinedTextField(
                    value = qty,
                    onValueChange = { qty = it },
                    label = { Text(AppStrings.quantity) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.text.isNotBlank() && price.isNotBlank(),
                onClick = {
                    onConfirm(
                        TransactionItem(
                            name = name.text,
                            price = price.toDoubleOrNull() ?: 0.0,
                            quantity = qty.toDoubleOrNull() ?: 1.0
                        )
                    )
                }
            ) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}


@Composable
fun OnTransactionClickDialogContent(
    customer: Customer,
    transaction: Transaction,
    modifier: Modifier = Modifier,
    totalDue: Double
) {
    val context = LocalContext.current
    val ownerProfile = getOwnerProfile(context)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Same content as your Dialog composable
        // Header, LazyColumn for items, summary, note, actions...
        // ---------- ITEMS GRID ----------
        if (ownerProfile.name != "" && ownerProfile.phone != "" && ownerProfile.address != "") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.Top
            ) {
                // Owner Name
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ownerProfile.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = ownerProfile.address,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }

                // Phone on the right
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = ownerProfile.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                }

            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val bsDate = convertToBS(transaction.timestamp)
            Column(modifier = Modifier.weight(2f)) {
                Text("${AppStrings.name} =  ${customer.name}")
                Text("${AppStrings.address} = ${customer.address ?: ""}")
                Text("${AppStrings.phone} = ${customer.phone ?: ""}")
            }

            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                Text(bsDate.year.toString() + " ")
                Text(bsDate.month + " ")
                Text(bsDate.day.toString())
            }

        }
        Spacer(Modifier.height(16.dp))
        Text(
            AppStrings.items,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(transaction.items.size) { it ->
                val item = transaction.items[it]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(item.name, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(2.dp))
                        Text("${AppStrings.quantity}: ${item.quantity}", fontSize = 12.sp)
                    }

                    Text("Rs. ${item.price * item.quantity}")
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(10.dp))


        // ---------- SUMMARY ----------
        val subtotal = transaction.items.sumOf { it.price * it.quantity }
        val discount = transaction.discount
        val finalAmount = subtotal - discount - transaction.paid

        SummaryRow(title = AppStrings.subtotal, value = subtotal)
        SummaryRow(title = AppStrings.paid_label, value = transaction.paid)
        SummaryRow(title = AppStrings.final_amount, value = finalAmount)

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider()

        Spacer(modifier = Modifier.height(10.dp))
        SummaryRow(title = AppStrings.total_due, value = totalDue)
        HorizontalDivider()

        Spacer(modifier = Modifier.height(10.dp))


        // ---------- NOTE ----------
        if (!transaction.note.isNullOrEmpty()) {
            Text(
                "${AppStrings.note}: ${transaction.note}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}

private fun returnComposableAsPdf(
    activity: Activity,
    content: @Composable () -> Unit,
    onPdfReady: (ByteArray) -> Unit
) {
    val root = FrameLayout(activity)
    activity.addContentView(
        root,
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    val composeView = ComposeView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        setContent { content() }
    }
    root.addView(composeView)

    composeView.doOnLayout {
        composeView.post {
            if (composeView.width == 0) {
                Toast.makeText(activity, "Layout failed (0 size)", Toast.LENGTH_SHORT).show()
                root.removeView(composeView)
                return@post
            }

            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                composeView.width,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(20_0000, View.MeasureSpec.AT_MOST)

            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

            val totalHeight = composeView.measuredHeight
            if (totalHeight == 0) {
                Toast.makeText(activity, "Measured height is 0", Toast.LENGTH_SHORT).show()
                root.removeView(composeView)
                return@post
            }

            val fullBitmap = Bitmap.createBitmap(
                composeView.measuredWidth,
                totalHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(fullBitmap)
            composeView.draw(canvas)

            val document = PdfDocument()
            val pageWidth = 595
            val pageHeight = 842

            val scale = pageWidth.toFloat() / fullBitmap.width
            val scaledHeight = (fullBitmap.height * scale).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(fullBitmap, pageWidth, scaledHeight, true)

            var yOffset = 0
            var pageNum = 1
            while (yOffset < scaledHeight) {
                val bottom = minOf(yOffset + pageHeight, scaledHeight)
                val pageBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    yOffset,
                    pageWidth,
                    bottom - yOffset
                )

                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                val page = document.startPage(pageInfo)
                page.canvas.drawBitmap(pageBitmap, 0f, 0f, null)
                document.finishPage(page)

                yOffset += pageHeight
                pageNum++
            }

            // Instead of saving, write to ByteArrayOutputStream
            val outputStream = ByteArrayOutputStream()
            document.writeTo(outputStream)
            document.close()

            onPdfReady(outputStream.toByteArray())

            root.removeView(composeView)
        }
    }
}

private fun saveComposableAsPdf(
    activity: Activity,
    fileName: String,
    content: @Composable () -> Unit
) {
    val root = FrameLayout(activity)
    activity.addContentView(
        root,
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )

    val composeView = ComposeView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        setContent { content() }
    }
    root.addView(composeView)

    composeView.doOnLayout {
        composeView.post {
            if (composeView.width == 0) {
                Toast.makeText(activity, "Layout failed (0 size)", Toast.LENGTH_SHORT).show()
                root.removeView(composeView)
                return@post
            }

            // 🟢 Step 1: Force ComposeView to measure with a finite but large max height
            val widthSpec = View.MeasureSpec.makeMeasureSpec(
                composeView.width,
                View.MeasureSpec.EXACTLY
            )
            val heightSpec = View.MeasureSpec.makeMeasureSpec(20_0000, View.MeasureSpec.AT_MOST)
            // allow up to 20,000px

            composeView.measure(widthSpec, heightSpec)
            composeView.layout(0, 0, composeView.measuredWidth, composeView.measuredHeight)

            val totalHeight = composeView.measuredHeight
            if (totalHeight == 0) {
                Toast.makeText(activity, "Measured height is 0", Toast.LENGTH_SHORT).show()
                root.removeView(composeView)
                return@post
            }

            // 🟢 Step 2: Render into bitmap
            val fullBitmap = Bitmap.createBitmap(
                composeView.measuredWidth,
                totalHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = android.graphics.Canvas(fullBitmap)
            composeView.draw(canvas)

            // 🟢 Step 3: Split into multiple A4 pages
            val document = PdfDocument()
            val pageWidth = 595  // A4 width in points
            val pageHeight = 842 // A4 height in points

            val scale = pageWidth.toFloat() / fullBitmap.width
            val scaledHeight = (fullBitmap.height * scale).toInt()

            val scaledBitmap =
                Bitmap.createScaledBitmap(fullBitmap, pageWidth, scaledHeight, true)

            var yOffset = 0
            var pageNum = 1
            while (yOffset < scaledHeight) {
                val bottom = minOf(yOffset + pageHeight, scaledHeight)
                val pageBitmap = Bitmap.createBitmap(
                    scaledBitmap,
                    0,
                    yOffset,
                    pageWidth,
                    bottom - yOffset
                )

                val pageInfo =
                    PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create()
                val page = document.startPage(pageInfo)
                page.canvas.drawBitmap(pageBitmap, 0f, 0f, null)
                document.finishPage(page)

                yOffset += pageHeight
                pageNum++
            }

            // 🟢 Step 4: Save PDF
            val filePath = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "$fileName.pdf"
            )
            FileOutputStream(filePath).use {
                document.writeTo(it)
            }
            document.close()

            Toast.makeText(activity, "Bill saved in Downloads", Toast.LENGTH_SHORT).show()

            // cleanup
            root.removeView(composeView)
        }
    }
}