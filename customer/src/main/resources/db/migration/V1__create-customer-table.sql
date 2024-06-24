create extension if not exists "uuid-ossp";

create table customer (
    id uuid default uuid_generate_v4() primary key,
    name varchar(255) not null,
    email varchar(255) unique not null,
    password text not null,
    birthdate date not null,
    is_deleted boolean not null default false,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now(),
    deleted_at timestamp
);