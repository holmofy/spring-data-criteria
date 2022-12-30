create table if not exists `t_user`(
    id bigint primary key,
    nick varchar(32) not null,
    province varchar(32) not null,
    city varchar(32) not null,
    area varchar(128) not null,
    create_date datetime not null,
    last_modified_date datetime not null
) engine=InnoDB;