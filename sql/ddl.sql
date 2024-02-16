create table board_table
(
    id             bigint auto_increment primary key,
    created_time   datetime     null,
    updated_time   datetime     null,
    board_contents varchar(500) null,
    board_hits     int          null,
    board_pass     varchar(255) null,
    board_title    varchar(255) null,
    board_writer   varchar(20)  not null,
    file_attached  int          null
);

create table board_file_table
(
    id                 bigint auto_increment primary key,
    created_time       datetime     null,
    updated_time       datetime     null,
    original_file_name varchar(255) null,
    stored_file_name   varchar(255) null,
    board_id           bigint       null,
    constraint FKcfxqly70ddd02xbou0jxgh4o3
        foreign key (board_id) references board_table (id) on delete cascade
);