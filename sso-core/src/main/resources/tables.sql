create schema if not exists auth_rbac;

create table if not exists auth_rbac.role
(
    id bigint not null
        primary key,
    description varchar(255) not null,
    name varchar(255) not null
);

create table if not exists auth_rbac.target
(
    id bigint not null
        primary key,
    action varchar(255),
    app_id bigint,
    object varchar(255)
);

create table if not exists auth_rbac.target_role
(
    target_id bigint not null
        constraint fk_target_role_target
            references auth_rbac.target,
    role_id bigint not null
        constraint fk_target_role_role
            references auth_rbac.role
);

create table if not exists auth_rbac.user_role
(
    id bigint not null
        primary key,
    app_id bigint,
    user_id bigint,
    role_id bigint
        constraint fk_user_role_role
            references auth_rbac.role
);



create schema if not exists sso_oauth;

create table if not exists sso_oauth.third_party_app
(
    id bigint not null
        primary key,
    app_name varchar(255),
    client_id varchar(255)
        constraint uk_client_id
            unique,
    client_secret varchar(400)
);

create table if not exists sso_oauth.third_party_redirect
(
    id bigint not null
        primary key,
    uri varchar(255),
    third_party_app_id bigint
        constraint fk_redirect_app
            references sso_oauth.third_party_app
);

create table if not exists sso_oauth.third_party_required_user_info
(
    id bigint not null
        primary key,
    data_type smallint not null
        constraint third_party_required_user_info_data_type_check
            check ((data_type >= 0) AND (data_type <= 1)),
    platform_type smallint not null
        constraint third_party_required_user_info_platform_type_check
            check ((platform_type >= 0) AND (platform_type <= 4)),
    third_party_app_id bigint
        constraint fk_user_info_app
            references sso_oauth.third_party_app
);

create schema if not exists sso_user;

create table if not exists sso_user.account
(
    user_id bigint not null
        primary key,
    password varchar(255) not null,
    salt varchar(255) not null,
    username varchar(255) not null
        unique
        constraint uk_account
            unique
);

create table if not exists sso_user."user"
(
    id bigint not null
        primary key,
    name varchar(255),
    student_id varchar(255)
        unique
        constraint uk_user
            unique
);

create table if not exists sso_user.user_info
(
    data_type smallint not null
        constraint user_info_data_type_check
            check ((data_type >= 0) AND (data_type <= 1)),
    platform_type smallint not null
        constraint user_info_platform_type_check
            check ((platform_type >= 0) AND (platform_type <= 2)),
    id bigint not null
        primary key,
    user_id bigint
        constraint fk_user_info_user
            references sso_user."user",
    metadata varchar(255) not null
);
