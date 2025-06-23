create table if not exists items
(
    id          bigserial primary key,
    title       varchar(256) not null,
    description text         not null,
    price       decimal      not null,
    count       integer      not null default 0,
    constraint price_non_negative check (count >= 0),
    constraint count_non_negative check (count >= 0)
);

create table if not exists images
(
    id      bigserial primary key,
    item_id int not null references items(id),
    image   bytea      not null
);

