package com.mruraza.khata.utils

import android.content.Context
import android.net.Uri
import com.mruraza.khata.data.local.Entities.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook

object ExcelRestoreParser {

    fun parse(
        context: Context,
        uri: Uri
    ): Triple<List<CustomerEntity>, List<ItemsEntity>, List<TransactionEntity>> {

        val input = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open file")

        val workbook = XSSFWorkbook(input)

        // -------- Customers --------
        val customers = mutableListOf<CustomerEntity>()
        workbook.getSheet("Customers")?.let { sheet ->
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                customers.add(
                    CustomerEntity(
                        id = row.getCell(0).numericCellValue.toInt(),
                        name = row.getCell(1).stringCellValue,
                        phone = row.getCell(2)?.stringCellValue,
                        address = row.getCell(3)?.stringCellValue,
                        totalBalance = row.getCell(4).numericCellValue
                    )
                )
            }
        }

        // -------- Items --------
        val items = mutableListOf<ItemsEntity>()
        workbook.getSheet("Items")?.let { sheet ->
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                items.add(
                    ItemsEntity(
                        id = row.getCell(0).numericCellValue.toInt(),
                        name = row.getCell(1).stringCellValue,
                        price = row.getCell(2).numericCellValue
                    )
                )
            }
        }

        // -------- Transactions --------
        val transactions = mutableListOf<TransactionEntity>()
        workbook.getSheet("Transactions")?.let { sheet ->
            for (i in 1..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue
                transactions.add(
                    TransactionEntity(
                        id = row.getCell(0).numericCellValue.toInt(),
                        customerId = row.getCell(1).numericCellValue.toInt(),
                        due = row.getCell(2).numericCellValue,
                        paid = row.getCell(3).numericCellValue,
                        discount = row.getCell(4).numericCellValue,
                        items = row.getCell(5).stringCellValue,
                        note = row.getCell(6)?.stringCellValue,
                        timestamp = row.getCell(7).numericCellValue.toLong()
                    )
                )
            }
        }

        workbook.close()
        input.close()

        return Triple(customers, items, transactions)
    }
}
