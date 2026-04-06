# RuneForgeMarket SRS

## 1. Introduction
### 1.1 Purpose
This document specifies the software requirements for RuneForgeMarket, a Warcraft player-to-player item trading platform with wallets and multi-currency support.

### 1.2 Scope
The system provides:
- User authentication and role management.
- Item templates and item instances with JSON stats.
- Wallet balances in multiple currencies and wallet transactions.
- Marketplace, orders, black market, and event-related data structures (per ERD).

### 1.3 Definitions
- Currency: In-game money types (e.g., GOLD, SILVER, COPPER).
- Item Template: Static item definition (type, rarity, base stats).
- Item: Instance owned by a user.
- Wallet Balance: Amount per currency for a wallet.
- Transaction Type: CREDIT, DEBIT, ADJUST.
- Transaction Ref Type: Source or context (ADMIN, PLAYER_TRADE, NIGHT_MARKET, NPC_MARKET).

## 2. Overall Description
### 2.1 Product Perspective
The backend exposes REST APIs for auth, items, and wallet services. Data is stored in PostgreSQL with JSONB columns for item stats.

### 2.2 User Classes
- Player: Owns items, trades, uses wallet.
- Admin: Manages templates, currency operations.

### 2.3 Assumptions and Dependencies
- PostgreSQL is available.
- Spring Boot 4.x with JPA and Spring Security.
- JWT authentication for API access.

## 3. System Features and Requirements

### 3.1 Authentication and Authorization
- Users can register, login, and refresh tokens.
- Roles are assigned through `users`, `roles`, and `user_roles`.
- JWT tokens are used for stateless auth.

Functional requirements:
- FR-AUTH-01: Register a new user with username, email, and password.
- FR-AUTH-02: Login with username/email and password to receive tokens.
- FR-AUTH-03: Refresh access tokens with refresh token.

### 3.2 Wallet and Currency
- Each user has a wallet.
- Each wallet holds balances for multiple currencies.
- Transactions update balances and are recorded.

Functional requirements:
- FR-WALLET-01: Get wallet balance by currency.
- FR-WALLET-02: Get all wallet balances (all currencies).
- FR-WALLET-03: List wallet transactions (optionally filtered by currency).
- FR-WALLET-04: Apply a wallet transaction (credit/debit/adjust) with validation.

Validation rules:
- Amount must be positive.
- Debit must not exceed balance.
- Transaction type and ref type must be provided.

### 3.3 Item Templates and Items
- Item templates define base stats as JSON.
- Item instances derive stats from templates and rarity rules.
- Items can be created, transferred, and deleted.

Functional requirements:
- FR-ITEM-01: Create item from template for an owner.
- FR-ITEM-02: Get item by id.
- FR-ITEM-03: List items by owner.
- FR-ITEM-04: Filter items by status.
- FR-ITEM-05: Create and update item templates.

Validation rules for base stats:
- baseStats is required and non-empty.
- Keys must be non-blank.
- Values must be numeric and >= 0.

### 3.4 Marketplace and Orders (Data Model)
- Listings, listing prices, orders, and order items are defined in ERD.
- These features are represented in the data model for future implementation.

### 3.5 Black Market and Events (Data Model)
- Event-driven offers and prices are defined in ERD.
- These features are represented in the data model for future implementation.

## 4. External Interface Requirements
### 4.1 REST API (Current Scope)
- Auth:
  - POST /api/auth/register
  - POST /api/auth/login
  - POST /api/auth/refresh

- Wallet:
  - GET /api/wallet/balance?currencyId=...
  - GET /api/wallet/balances
  - GET /api/wallet/transactions?currencyId=...
  - POST /api/wallet/transactions

- Items:
  - GET /api/items/{id}
  - GET /api/items/owner/{ownerId}
  - POST /api/items/create_item
  - POST /api/items/create_template

### 4.2 Database Schema (ERD)
Key entities from ERD:
- users, roles, user_roles
- wallets, currencies, wallet_balance, wallet_transactions
- item_template, items, npc_shop
- listings, listing_prices, orders, order_items
- black_market_offers, black_market_prices, event

## 5. Non-Functional Requirements
- NFR-01: All services must run on Java 21.
- NFR-02: JSON stats stored as JSONB.
- NFR-03: Transactions should be atomic (use DB transactions).
- NFR-04: API responses should be JSON.

## 6. Future Enhancements
- Full marketplace operations (list, buy, sell, cancel).
- Order processing and item delivery.
- Black market event scheduling and offers.
- Audit logging for wallet operations.
