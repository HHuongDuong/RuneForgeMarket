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

### 3.6 Sequence Flows
Implemented flows:
1. Login flow
  1. Client sends credentials to POST /api/auth/login.
  2. Service validates user and password.
  3. Service returns access and refresh tokens.

2. Register flow
  1. Client sends data to POST /api/auth/register.
  2. Service validates uniqueness and hashes password.
  3. Service creates user and assigns USER role.
  4. Service returns access and refresh tokens.

3. Refresh token flow
  1. Client sends refresh token to POST /api/auth/refresh.
  2. Service validates token type and expiration.
  3. Service returns new access and refresh tokens.

4. Wallet transaction flow
  1. Client posts to POST /api/wallet/transactions.
  2. Service validates amount and balance (for DEBIT).
  3. Service updates wallet balance.
  4. Service records wallet_transactions entry.

5. Wallet balance flow
  1. Client calls GET /api/wallet/balance or GET /api/wallet/balances.
  2. Service loads wallet balances; creates missing zero balances.
  3. Service returns balances.

6. Item template flow
  1. Client posts to POST /api/items/create_template.
  2. Service validates baseStats.
  3. Service stores item template.

7. Item creation flow
  1. Client posts to POST /api/items/create_item.
  2. Service loads template.
  3. Service generates stats from template.
  4. Service saves item.

8. Listing lifecycle flow
  1. Seller creates listing (POST /api/listings).
  2. Listing becomes ACTIVE with a price.
  3. Seller can cancel (POST /api/listings/{id}/cancel).
  4. System can lock (POST /api/listings/{id}/lock) to avoid double-buy.
  5. System marks sold (POST /api/listings/{id}/sold).

Planned flows (not implemented yet):
1. Purchase flow (marketplace)
  1. Buyer requests listing details.
  2. System locks listing.
  3. Buyer confirms purchase with currency and price.
  4. System debits buyer wallet, credits seller wallet, records transactions.
  5. System creates order and order_items, updates listing to SOLD.

2. Trade flow (player to player)
  1. Player A initiates trade with Player B.
  2. System validates item ownership and wallet balances.
  3. System transfers item ownership and applies wallet transactions.
  4. System logs transaction with ref type PLAYER_TRADE.

3. Black market offer flow
  1. Event creates offers for users.
  2. User views offers and prices.
  3. User purchases offer with wallet transaction.
  4. System records black_market_offers and black_market_prices usage.

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

### 4.3 Data Dictionary
users:
- id (bigint, PK)
- username (varchar(50))
- email (string)
- password_hash (string)
- created_at (timestamp)

roles:
- id (int, PK)
- name (enum)

user_roles:
- user_id (bigint, FK -> users.id)
- role_id (int, FK -> roles.id)
- PK (user_id, role_id)

wallets:
- id (bigint, PK)
- user_id (bigint, FK -> users.id)

currencies:
- id (int, PK)
- name (enum)

wallet_balance:
- wallet_id (bigint, FK -> wallets.id)
- currency_id (int, FK -> currencies.id)
- balance (bigint)
- PK (wallet_id, currency_id)

wallet_transactions:
- id (bigint, PK)
- wallet_id (bigint, FK -> wallets.id)
- currency_id (int, FK -> currencies.id)
- amount (bigint)
- balance_after (bigint)
- type (varchar(20))
- ref_type (varchar(20))
- created_at (timestamp)

item_template:
- id (bigint, PK)
- name (varchar(100))
- type (enum)
- rarity (enum)
- base_stats (json)
- is_npc_sold (boolean)
- stackable (boolean)

items:
- id (bigint, PK)
- template_id (bigint, FK -> item_template.id)
- user_id (bigint, FK -> users.id)
- status (enum)
- stats (json)

npc_shop:
- template_id (bigint, FK -> item_template.id)
- currency_id (int, FK -> currencies.id)
- price (bigint)
- PK (template_id, currency_id)

listings:
- id (bigint, PK)
- item_id (bigint, FK -> items.id)
- seller_id (bigint, FK -> users.id)
- status (enum)
- created_at (timestamp)

listing_prices:
- listing_id (bigint, FK -> listings.id)
- currency_id (int, FK -> currencies.id)
- price (bigint)
- PK (listing_id, currency_id)

orders:
- id (bigint, PK)
- buyer_id (bigint, FK -> users.id)
- seller_id (bigint, FK -> users.id)
- status (enum)
- created_at (timestamp)

order_items:
- id (bigint, PK)
- order_id (bigint, FK -> orders.id)
- item_id (bigint, FK -> items.id)
- currency_id (int, FK -> currencies.id)
- price (bigint)

black_market_offers:
- id (bigint, PK)
- event_id (bigint, FK -> event.id)
- user_id (bigint, FK -> users.id)
- template_id (bigint, FK -> item_template.id)
- or_price (bigint)
- discount_percent (int)
- expires_at (timestamp)
- status (enum)
- created_at (timestamp)

black_market_prices:
- offer_id (bigint, FK -> black_market_offers.id)
- currency_id (int, FK -> currencies.id)
- price (bigint)
- PK (offer_id, currency_id)

event:
- id (bigint, PK)
- name (varchar(50))
- status (varchar(20))
- start_time (timestamp)
- end_time (timestamp)
- created_at (timestamp)

### 4.4 API Examples
Auth - register:
POST /api/auth/register
Request:
{
  "username": "player1",
  "email": "player1@example.com",
  "password": "secret123"
}
Response:
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer"
}

Auth - login:
POST /api/auth/login
Request:
{
  "usernameOrEmail": "player1",
  "password": "secret123"
}
Response:
{
  "accessToken": "...",
  "refreshToken": "...",
  "tokenType": "Bearer"
}

Wallet - get balance:
GET /api/wallet/balance?currencyId=1
Response:
{
  "walletId": 10,
  "currencyId": 1,
  "balance": 1500
}

Wallet - get balances:
GET /api/wallet/balances
Response:
[
  {"walletId": 10, "currencyId": 1, "balance": 1500},
  {"walletId": 10, "currencyId": 2, "balance": 900}
]

Wallet - apply transaction:
POST /api/wallet/transactions
Request:
{
  "currencyId": 1,
  "amount": 200,
  "type": "DEBIT",
  "refType": "PLAYER_TRADE"
}
Response:
{
  "id": 55,
  "walletId": 10,
  "currencyId": 1,
  "amount": 200,
  "balanceAfter": 1300,
  "type": "DEBIT",
  "refType": "PLAYER_TRADE",
  "createdAt": "2026-04-06T10:00:00Z"
}

Items - create item:
POST /api/items/create_item
Request:
{
  "ownerId": 10,
  "templateId": 3,
  "status": "INVENTORY"
}
Response:
{
  "id": 100,
  "name": "Iron Sword",
  "ownerId": 10,
  "type": "WEAPON",
  "rarity": "COMMON",
  "status": "INVENTORY",
  "stats": {"attack": 12, "speed": 2}
}

Item template - create:
POST /api/items/create_template
Request:
{
  "name": "Iron Sword",
  "type": "WEAPON",
  "rarity": "COMMON",
  "baseStats": {"attack": 10, "speed": 2},
  "isNpcTradeable": true,
  "stackable": false
}
Response:
{
  "id": 3,
  "name": "Iron Sword",
  "type": "WEAPON",
  "rarity": "COMMON",
  "baseStats": {"attack": 10, "speed": 2},
  "isNpcTradeable": true,
  "stackable": false
}

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
