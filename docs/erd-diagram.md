@startuml

' =======================
' USERS & ROLES
' =======================

entity users {
  +id : bigint <<PK>>
  username : varchar(50)
  email : string
  password_hash : string
  created_at : timestamp
}

entity roles {
  +id : int <<PK>>
  name : enum
}

entity user_roles {
  +user_id : bigint
  +role_id : int
  --
  <<PK>> (user_id, role_id)
}

' =======================
' WALLET
' =======================

entity wallets {
  +id : bigint <<PK>>
  user_id : bigint <<FK>>
}

entity currencies {
  +id : int <<PK>>
  name : enum
}

entity wallet_balance {
  +wallet_id : bigint
  +currency_id : int
  balance : bigint
  --
  <<PK>> (wallet_id, currency_id)
}

entity wallet_transactions {
  +id : bigint <<PK>>
  wallet_id : bigint
  currency_id : int
  amount : bigint
  balance_after : bigint
  type : varchar(20)
  ref_type : varchar(20)
  created_at : timestamp
}

' =======================
' ITEM
' =======================

entity item_template {
  +id : bigint <<PK>>
  name : varchar(100)
  type : varchar(50)
  rarity : enum
  base_stats : JSON
  is_npc_sold : boolean
}

entity items {
  +id : bigint <<PK>>
  template_id : bigint
  user_id : bigint
  status : enum
  stats : JSON
}

entity npc_shop {
  +template_id : bigint
  +currency_id : int
  price : bigint
  --
  <<PK>> (template_id, currency_id)
}

' =======================
' MARKETPLACE
' =======================

entity listings {
  +id : bigint <<PK>>
  item_id : bigint
  seller_id : bigint
  status : enum
  created_at : timestamp
}

entity listing_prices {
  +listing_id : bigint
  +currency_id : int
  price : bigint
  --
  <<PK>> (listing_id, currency_id)
}

entity orders {
  +id : bigint <<PK>>
  buyer_id : bigint
  seller_id : bigint
  status : enum
  created_at : timestamp
}

entity order_items {
  +id : bigint <<PK>>
  order_id : bigint
  item_id : bigint
  currency_id : int
  price : bigint
}

' =======================
' BLACK MARKET
' =======================

entity black_market_offers {
  +id : bigint <<PK>>
  event_id : bigint
  user_id : bigint
  template_id : bigint
  or_price : bigint
  discount_percent : int
  expires_at : timestamp
  status : enum
  created_at : timestamp
}

entity black_market_prices {
  +offer_id : bigint
  +currency_id : int
  price : bigint
  --
  <<PK>> (offer_id, currency_id)
}

entity event {
  +id : bigint <<PK>>
  name : varchar(50)
  status : varchar(20)
  start_time : timestamp
  end_time : timestamp
  created_at : timestamp
}

' =======================
' RELATIONSHIPS
' =======================

users ||--o{ user_roles
roles ||--o{ user_roles

users ||--|| wallets

wallets ||--o{ wallet_balance
currencies ||--o{ wallet_balance

wallets ||--o{ wallet_transactions
currencies ||--o{ wallet_transactions

item_template ||--o{ items
users ||--o{ items

item_template ||--o{ npc_shop
currencies ||--o{ npc_shop

users ||--o{ orders : buyer
users ||--o{ orders : seller

orders ||--o{ order_items
items ||--o{ order_items
currencies ||--o{ order_items

items ||--|| listings
users ||--o{ listings

listings ||--o{ listing_prices
currencies ||--o{ listing_prices

users ||--o{ black_market_offers
item_template ||--o{ black_market_offers
event ||--o{ black_market_offers

black_market_offers ||--o{ black_market_prices
currencies ||--o{ black_market_prices

@enduml