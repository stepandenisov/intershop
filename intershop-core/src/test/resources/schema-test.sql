create table if not exists items
(
    id          bigserial primary key,
    title       varchar(256) not null,
    description text         not null,
    price       decimal      not null
);


create table if not exists images
(
    id      bigserial primary key,
    item_id int   not null references items (id),
    image   bytea not null
);

CREATE TABLE if not exists users
(
    id       bigserial primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    roles    text       not null
);

create table if not exists orders
(
    id    bigserial primary key,
    user_id bigserial references users (id),
    total decimal not null
);

create table if not exists orders_items
(
    id         bigserial primary key,
    order_id   bigserial references orders (id),
    item_id    bigserial references items (id),
    item_count int     not null,
    item_price decimal not null
);

create table if not exists carts
(
    id    bigserial primary key,
    user_id bigserial references users (id),
    total decimal not null
);

create table if not exists carts_items
(
    id         bigserial primary key,
    cart_id    bigserial references carts (id),
    item_id    bigserial references items (id),
    item_count int not null
);

