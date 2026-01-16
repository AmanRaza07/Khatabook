# Khata – Offline Ledger & Billing Android App

Khata is a **production-ready, offline-first Android application** designed to manage customers, items, and financial transactions efficiently.  
It works completely **offline**, supports **billing, reports, and data export/import**, and is built using **Clean Architecture** with **Room Database**.

---

## ✨ Features

### 👥 Customer Management
- Create, update, delete customers
- View complete transaction history per customer
- Track balances (credit / debit)
- Customer-wise reports and summaries

### 📦 Item Management
- Add and manage items/products
- Item-wise transaction tracking
- Pricing and usage history

### 💸 Transaction Handling
- Record credit & debit transactions
- Offline transaction processing
- Full transaction history
- Edit, delete, and manage past records

### 🧾 Billing & Invoices
- Generate professional bills
- Print bills directly
- Share bills via PDF
- Reprint past invoices

### 📄 PDF & Reports
- Generate transaction PDFs
- Customer and item history reports
- Share or print reports anytime

### 📊 Data Export & Restore
- Export complete database to **Excel**
- Restore data from Excel backup
- Safe offline backup & recovery

### 🌙 UI & UX
- Light Theme & Dark Theme
- Clean and minimal UI
- Optimized for daily business use

### 🗓 Nepali Date Support
- Uses **custom Nepali Date library**
- Accurate local date handling for transactions and reports

---

## 🧱 Architecture

The app follows **Clean Architecture**, ensuring scalability, testability, and long-term maintainability.

presentation/
domain/
data/

yaml
Copy code

### Key Principles:
- Separation of concerns
- Business logic independent of UI
- Easily extensible and maintainable

---

## 🛠 Tech Stack

- **Language:** Kotlin  
- **Architecture:** Clean Architecture (MVVM-based)  
- **Database:** Room (SQLite)  
- **Offline First:** Yes  
- **PDF Generation:** Native Android APIs  
- **Export Format:** Excel  
- **Date System:** Custom Nepali Date Library  
- **UI:** Material Design  
- **Themes:** Light & Dark  

---

## 📦 Core Modules

- Customers
- Items
- Transactions
- Billing & Invoices
- Reports & History
- Backup & Restore

---

## 🔐 Data Safety

- Fully offline
- No external server dependency
- Local encrypted Room database (if enabled)
- Manual backup & restore support

---

## 🚀 Production Ready

- Optimized for real-world usage
- Clean codebase
- Scalable architecture
- Ready for Play Store deployment
- Tested with real transaction flows

---

## 👨‍💻 Author
**Madan Raj Upadhyay**  
AI and Android Developer  
---

## ⭐ Support
If you find this project useful, consider giving it a ⭐ on GitHub!
