create table message (
    id bigint not null,
    filename varchar(255),
    tag varchar(255),
    text varchar(2048),
    user_id bigint,
    primary key (id)
);

create table message_seq (next_val bigint);

insert into message_seq values ( 1 );

create table user_role (
    user_id bigint not null,
    roles varchar(255)
);

create table usr (
    id bigint not null,
    activation_code varchar(255),
    active bit not null,
    email varchar(255),
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
);

create table usr_seq (next_val bigint);

insert into usr_seq values ( 1 );

alter table message
    add constraint message_user_fk
    foreign key (user_id) references usr (id);

alter table user_role
    add constraint user_role_user_fk
        foreign key (user_id) references usr (id);