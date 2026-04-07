-- Initial schema for RuneForgeMarket

create table if not exists users (
    id bigserial primary key,
    username varchar(50) not null,
    email varchar(255) not null,
    password_hash varchar(255) not null,
    created_at timestamp not null default now()
);

create unique index if not exists uk_users_username on users (username);
create unique index if not exists uk_users_email on users (email);

create table if not exists roles (
    id serial primary key,
    name varchar(20) not null unique
);

create table if not exists user_roles (
    user_id bigint not null,
    role_id int not null,
    primary key (user_id, role_id),
    constraint fk_user_roles_user foreign key (user_id) references users (id),
    constraint fk_user_roles_role foreign key (role_id) references roles (id)
);

create table if not exists wallets (
    id bigserial primary key,
    user_id bigint not null,
    constraint fk_wallet_user foreign key (user_id) references users (id)
);

create table if not exists currencies (
    id serial primary key,
    name varchar(20) not null
);

create table if not exists wallet_balance (
    wallet_id bigint not null,
    currency_id int not null,
    balance bigint not null,
    primary key (wallet_id, currency_id),
    constraint fk_wallet_balance_wallet foreign key (wallet_id) references wallets (id),
    constraint fk_wallet_balance_currency foreign key (currency_id) references currencies (id)
);

create table if not exists wallet_transactions (
    id bigserial primary key,
    wallet_id bigint not null,
    currency_id int not null,
    amount bigint not null,
    balance_after bigint not null,
    type varchar(20) not null,
    ref_type varchar(20) not null,
    created_at timestamp not null default now(),
    constraint fk_wallet_transactions_wallet foreign key (wallet_id) references wallets (id),
    constraint fk_wallet_transactions_currency foreign key (currency_id) references currencies (id)
);

create table if not exists item_templates (
    id serial primary key,
    name varchar(100) not null unique,
    type varchar(50) not null,
    rarity varchar(50) not null,
    base_stats jsonb,
    is_npc_tradeable boolean not null,
    istackable boolean not null
);

create table if not exists items (
    id bigserial primary key,
    template_id int not null,
    owner_id bigint not null,
    status varchar(20) not null,
    base_stats jsonb,
    constraint fk_items_template foreign key (template_id) references item_templates (id),
    constraint fk_items_owner foreign key (owner_id) references users (id)
);

create table if not exists npc_shop (
    template_id int not null,
    currency_id int not null,
    price bigint not null,
    primary key (template_id, currency_id),
    constraint fk_npc_shop_template foreign key (template_id) references item_templates (id),
    constraint fk_npc_shop_currency foreign key (currency_id) references currencies (id)
);

create table if not exists listings (
    id bigserial primary key,
    item_id bigint not null,
    seller_id bigint not null,
    status varchar(20) not null,
    created_at timestamp not null default now(),
    constraint fk_listings_item foreign key (item_id) references items (id),
    constraint fk_listings_seller foreign key (seller_id) references users (id)
);

create table if not exists listing_prices (
    listing_id bigint not null,
    currency_id int not null,
    price bigint not null,
    primary key (listing_id, currency_id),
    constraint fk_listing_prices_listing foreign key (listing_id) references listings (id),
    constraint fk_listing_prices_currency foreign key (currency_id) references currencies (id)
);

create table if not exists orders (
    id bigserial primary key,
    buyer_id bigint not null,
    seller_id bigint not null,
    status varchar(20) not null,
    created_at timestamp not null default now(),
    constraint fk_orders_buyer foreign key (buyer_id) references users (id),
    constraint fk_orders_seller foreign key (seller_id) references users (id)
);

create table if not exists order_items (
    id bigserial primary key,
    order_id bigint not null,
    item_id bigint not null,
    currency_id int not null,
    price bigint not null,
    constraint fk_order_items_order foreign key (order_id) references orders (id),
    constraint fk_order_items_item foreign key (item_id) references items (id),
    constraint fk_order_items_currency foreign key (currency_id) references currencies (id)
);

create table if not exists event (
    id bigserial primary key,
    name varchar(50) not null,
    status varchar(20) not null,
    start_time timestamp not null,
    end_time timestamp not null,
    created_at timestamp not null default now()
);

create table if not exists black_market_offers (
    id bigserial primary key,
    event_id bigint not null,
    user_id bigint not null,
    template_id int not null,
    or_price bigint not null,
    discount_percent int not null,
    expires_at timestamp not null,
    status varchar(20) not null,
    created_at timestamp not null default now(),
    constraint fk_black_market_event foreign key (event_id) references event (id),
    constraint fk_black_market_user foreign key (user_id) references users (id),
    constraint fk_black_market_template foreign key (template_id) references item_templates (id)
);

create table if not exists black_market_prices (
    offer_id bigint not null,
    currency_id int not null,
    price bigint not null,
    primary key (offer_id, currency_id),
    constraint fk_black_market_prices_offer foreign key (offer_id) references black_market_offers (id),
    constraint fk_black_market_prices_currency foreign key (currency_id) references currencies (id)
);
