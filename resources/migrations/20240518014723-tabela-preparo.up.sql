create extension if not exists "uuid-ossp";
--;;
create table preparo
(
    id               uuid primary key default uuid_generate_v4(),
    id_cliente       uuid    NOT NULL,
    numero_do_pedido text    NOT NULL,
    produtos         uuid[]  NOT NULL,
    status           text    NOT NULL,
    created_at       timestamp default now()
)