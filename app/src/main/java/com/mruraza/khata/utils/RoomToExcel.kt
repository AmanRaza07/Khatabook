package com.mruraza.khata.utils


import android.os.Environment
import com.mruraza.khata.data.local.Entities.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

object RoomExcelExporter {

    fun exportAll(
        customers: List<CustomerEntity>,
        items: List<ItemsEntity>,
        transactions: List<TransactionEntity>
    ): File {

        val workbook = XSSFWorkbook()

        // -------- Customers Sheet --------
        workbook.createSheet("Customers").apply {
            var rowIndex = 0
            createRow(rowIndex++).apply {
                createCell(0).setCellValue("id")
                createCell(1).setCellValue("name")
                createCell(2).setCellValue("phone")
                createCell(3).setCellValue("address")
                createCell(4).setCellValue("totalBalance")
            }

            customers.forEach {
                createRow(rowIndex++).apply {
                    createCell(0).setCellValue(it.id.toDouble())
                    createCell(1).setCellValue(it.name)
                    createCell(2).setCellValue(it.phone ?: "")
                    createCell(3).setCellValue(it.address ?: "")
                    createCell(4).setCellValue(it.totalBalance)
                }
            }
        }

        // -------- Items Sheet --------
        workbook.createSheet("Items").apply {
            var rowIndex = 0
            createRow(rowIndex++).apply {
                createCell(0).setCellValue("id")
                createCell(1).setCellValue("name")
                createCell(2).setCellValue("price")
            }

            items.forEach {
                createRow(rowIndex++).apply {
                    createCell(0).setCellValue(it.id.toDouble())
                    createCell(1).setCellValue(it.name)
                    createCell(2).setCellValue(it.price)
                }
            }
        }

        // -------- Transactions Sheet --------
        workbook.createSheet("Transactions").apply {
            var rowIndex = 0
            createRow(rowIndex++).apply {
                createCell(0).setCellValue("id")
                createCell(1).setCellValue("customerId")
                createCell(2).setCellValue("due")
                createCell(3).setCellValue("paid")
                createCell(4).setCellValue("discount")
                createCell(5).setCellValue("items")
                createCell(6).setCellValue("note")
                createCell(7).setCellValue("timestamp")
            }

            transactions.forEach {
                createRow(rowIndex++).apply {
                    createCell(0).setCellValue(it.id.toDouble())
                    createCell(1).setCellValue(it.customerId.toDouble())
                    createCell(2).setCellValue(it.due)
                    createCell(3).setCellValue(it.paid)
                    createCell(4).setCellValue(it.discount)
                    createCell(5).setCellValue(it.items)
                    createCell(6).setCellValue(it.note ?: "")
                    createCell(7).setCellValue(it.timestamp.toDouble())
                }
            }
        }

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "khata_backup.xlsx"
        )

        FileOutputStream(file).use {
            workbook.write(it)
        }
        workbook.close()

        return file
    }
}
