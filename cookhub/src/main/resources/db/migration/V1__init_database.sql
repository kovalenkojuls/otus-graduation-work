-- Создание таблицы пользователей
create table cookhub_user (
    id bigint generated by default as identity,
    activation_code varchar(255),
    active boolean,
    email varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

-- Создание таблицы ролей пользователей
create table cookhub_user_role (
    cookhub_user_id bigint not null,
    role varchar(255) check (role in ('USER','ADMIN'))
);

-- Создание таблицы рецептов
create table recipe (
    id bigint generated by default as identity,
    category varchar(255) check (category in ('SOUPS','SALADS','DESSERTS','MAIN_COURSES')),
    filename varchar(255),
    text TEXT not null,
    user_id bigint,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
);

-- Создание внешних ключей
alter table if exists cookhub_user_role
   add constraint cookhub_user_role_user_fk
   foreign key (cookhub_user_id)
   references cookhub_user;

alter table if exists recipe
   add constraint recipe_user_fk
   foreign key (user_id)
   references cookhub_user;