# 🧾 Expense Tracker - Microservices Based Application

![Java](https://img.shields.io/badge/Java-SpringBoot-green)
![Python](https://img.shields.io/badge/Python-Flask-blue)
![Kafka](https://img.shields.io/badge/Kafka-Event%20Streaming-orange)
![MySQL](https://img.shields.io/badge/MySQL-Database-lightblue)
![Kong](https://img.shields.io/badge/Kong-API%20Gateway-purple)

A **microservices-based Expense Tracker Application** built using **Spring Boot, Flask, MySQL, Kafka, and Kong API Gateway**.  
It provides secure authentication, user profile management, expense tracking, and intelligent message parsing for automated expense extraction.

---

## 🚀 Tech Stack

- **Backend Microservices:**
  - Spring Boot → `AuthService`, `UserService`, `ExpenseService`
  - Flask → `DataScienceService`
- **API Gateway:** Kong
- **Database:** MySQL
- **Event Streaming:** Kafka

---

## 🧩 Microservices Overview

### 🔑 AuthService (Spring Boot)
Handles authentication and JWT-based authorization.  

**Key Features**
- Secure **user signup and login** with password hashing  
- Generates & validates **JWT tokens**  
- Supports **refresh token mechanism**  
- Verifies token validity for Kong  

**Endpoints**
- `POST /auth/v1/signup` → User registration  
- `POST /auth/v1/login` → User login  
- `POST /auth/v1/refreshToken` → Refresh JWT token  
- `GET /auth/v1/ping` → Validate token & retrieve userId  

---

### 👤 UserService (Spring Boot)
Manages user profile data and consumes signup events from Kafka.  

**Key Features**
- Consumes **signup events** from Kafka and stores user info  
- Provides APIs to **fetch/update profile**  
- Ensures **data consistency** across services  

**Endpoints**
- `GET /user/v1/getUser` → Get user info  
- `POST /user/v1/createUpdate` → Create or update user info  

---

### 📊 DataScienceService (Flask)
Parses incoming messages (e.g., bank SMS) to extract expense details.  

**Key Features**
- Checks whether a given message is a **bank-related message** by scanning for specific keywords  
- If valid, forwards the message to **ChatMistral AI LLM** using LangChain schemas  
- Receives a **structured JSON response** with extracted details (amount, merchant, payment channel, etc.)  
- Returns structured response to client and can publish events to Kafka for further processing  

**Endpoints**
- `POST /v1/ds/message` → Parse message & return structured JSON  

---

### 💰 ExpenseService (Spring Boot)
Handles all expense-related operations.  

**Key Features**
- Full **CRUD operations** for expenses  
- Consumes expense events from Kafka (via DataScienceService)  
- Provides **date-based expense filtering**  
- Links expenses with **authenticated userId**  

**Endpoints**
- `POST /expense/v1/addExpense` → Add new expense  
- `PUT /expense/v1/updateExpanse` → Update existing expense  
- `DELETE /expense/v1/deleteExpanse` → Delete expense  
- `GET /expense/v1/getExpense` → Get all expenses  
- `GET /expense/v1/getExpanseByDate` → Get expenses between dates  

---

## 🌐 Kong API Gateway
Centralized entry point for all client requests.  

**Key Features**
- Acts as a **reverse proxy** and routes requests to respective microservices  
- Uses a **custom Lua plugin** to:
  - Extract `userId` from JWT by validating token with **AuthService**  
  - Inject the `userId` into request headers for upstream services  
- Implements **custom rate limiting per user** → `60 requests per minute per user`  
- Ensures **security and throttling** across all services  

---

## 🔄 Workflow

1. **Request Validation** → All requests go through **Kong API Gateway**.  
   - For every request (except signup & refresh), Kong calls `/auth/v1/ping` to validate JWT.  
   - If valid, Kong attaches `userId` to request headers.  
   - Custom Lua plugin ensures **rate limiting** (60 requests/minute per user).  

2. **User Management**  
   - Signup at `/auth/v1/signup` → AuthService publishes event → Kafka → UserService consumes & stores in DB.  
   - User fetches/updates profile via `/user/v1/getUser` or `/user/v1/createUpdate`.  

3. **Authentication**  
   - Login via `/auth/v1/login`.  
   - Refresh expired JWT via `/auth/v1/refreshToken`.  

4. **Expense Handling**  
   - CRUD via ExpenseService (`addExpense`, `updateExpanse`, `deleteExpanse`).  
   - Retrieve expenses (`getExpense`, `getExpanseByDate`).  

5. **Smart Expense Extraction**  
   - DataScienceService checks if a message is bank-related.  
   - Sends it to ChatMistral AI via LangChain → receives structured JSON.  
   - Publishes event to Kafka → ExpenseService consumes → Stores expense automatically.  

---

## 📂 Architecture Diagram

![Architecture Diagram](./docs/expense_tracker_architecture.png)


## 📱 Mobile Application

The project also includes a **React Native mobile app** that connects with the backend microservices through the Kong API Gateway.  

**Key Featuress**
- 📊 **Dashboard View** → Displays total expenses, monthly breakdown, and charts.  
- ➕ **Add Expense** → Allows manual expense entry with amount, category, and notes.  
- 🔍 **Filter & Search** → Search and filter expenses by date, category, or merchant.  
- 🔔 **Automated Expense Capture** → Auto-detects bank SMS messages and sends them to **DataScienceService** for parsing.  
- 👤 **User Profile** → Manage profile info synced with **UserService**.  
- 🔐 **Authentication** → Secure login & signup via **AuthService (JWT-based)**.  

---
