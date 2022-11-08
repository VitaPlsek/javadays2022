--liquibase formatted sql

--changeset vita.plsek:1 context:ddl
create table client
(
    id     bigserial primary key,
    name   varchar
);

create table office
(
    id        bigserial primary key,
    client_id bigint,
    location  varchar,
    active    bool default true,

    foreign key (client_id) REFERENCES client (id)
);
