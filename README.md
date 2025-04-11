# Smart Banking System - Microservice Architecture

## 📅 Project Proposal

The **Smart Banking System** is a microservice-based application designed to simulate essential features of a modern digital bank. The system is structured around secure authentication, account handling, card management, and transaction processing. Each sub-system is developed independently and deployed using containerized microservices, demonstrating DevOps, security, and cloud deployment best practices.

---

## 🛠️ System Architecture Components

### 1. 🔐 User Authentication & Login Microservice
- Handles user registration and login  
- Provides JWT-based authentication  
- Manages user roles (admin, customer)  
- Secures credentials with password hashing (e.g., bcrypt)

### 2. 💳 Card Management Microservice
- Enables CRUD operations for debit/credit cards  
- Associates cards with specific accounts  
- Maintains card status (active/blocked)

### 3. 💸 Transaction Management Microservice
- Records and retrieves banking transactions  
- Supports transfers between accounts  
- Classifies transactions: debit, credit, transfer  
- Implements filters by date, type, and amount

### 4. 🏦 Account Management Microservice
- Manages CRUD operations for bank accounts  
- Links accounts to user profiles  
- Tracks account balances  
- Ensures account status is up-to-date (active/suspended)

---

## 📊 ER Diagram (Text Representation)

```
[User]
 ├─ user_id (PK)
 ├─ username
 ├─ email
 ├─ password_hash
 └─ role (admin/customer)

[Account]
 ├─ account_id (PK)
 ├─ user_id (FK → User.user_id)
 ├─ account_number
 ├─ account_type (savings/current)
 ├─ balance
 └─ status (active/suspended)

[Card]
 ├─ card_id (PK)
 ├─ account_id (FK → Account.account_id)
 ├─ card_number
 ├─ card_type (credit/debit)
 ├─ expiry_date
 └─ status (active/blocked)

[Transaction]
 ├─ transaction_id (PK)
 ├─ from_account_id (FK → Account.account_id)
 ├─ to_account_id (FK → Account.account_id)
 ├─ amount
 ├─ transaction_type (credit/debit/transfer)
 ├─ timestamp
 └─ description
```

### 🖊️ Entities & Relationships

```
   +---------+         +-----------+         +-------+
   |  User   |───────▶|  Account   |──────▶|  Card  |
   +---------+         +-----------+         +-------+
      ▲                   ▲   ▲              |
      │                   │   └────────────────────────────┘
      │                   │
      │          +----------------------+
      │          |     Transaction      |
      ◀──────────────────from_account_id   |
                 └─────────────────to_account_id─────────────▶
```

