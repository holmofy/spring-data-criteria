create sequence t_user_seq;

create table if not exists t_user(
    id bigint primary key default nextval('t_user_seq'),
    nick varchar(32) not null,
    province varchar(32) not null,
    city varchar(32) not null,
    area varchar(128) not null,
    created_date timestamp not null,
    last_modified_date timestamp not null
);

insert into t_user
(nick,province,city,area,created_date,last_modified_date)
values
('Tom','JiangXi','NanChang','Qingshanhu',now(),now()),
('Jerry','JiangXi','JiuJiang','DuChange',now(),now()),
('Bob','JiangXi','NanChang','Qingshanhu',now(),now()),
('Lucy','JiangXi','NanChang','Qingshanhu',now(),now()),
('Jack','JiangXi','NanChang','Qingshanhu',now(),now());