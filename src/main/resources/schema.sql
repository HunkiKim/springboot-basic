create table customer
(
    customer_Id            binary(16) primary key,
    customer_email         varchar(50) not null,
    customer_password      varchar(50) not null,
    customer_name          varchar(50) not null,
    customer_phone_number  varchar(50) not null,
    customer_role          varchar(50) not null,
    customer_created_date  datetime(6) default null,
    customer_modified_date datetime(6) not null default current_timestamp (6),
    constraint unq_user_email unique (email)
);

create table voucher
(
    voucher_Id            binary(16) primary key,
    customer_email        varchar(50) not null,
    voucher_type          varchar(255),
    voucher_value         bigint,
    voucher_created_date  datetime(6) default null,
    voucher_modified_date datetime(6) not null default current_timestamp (6)
);