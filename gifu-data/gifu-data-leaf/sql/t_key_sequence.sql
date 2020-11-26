create table if not exists t_key_sequence
(
	c_key varchar(64) not null comment '主键'
		primary key,
	c_max_id bigint default 1 not null comment '最大序列号',
	c_step int default 1000 not null comment '步长',
	c_version bigint default 1 not null comment '版本号',
	c_create_timestamp bigint default 0 not null comment '创建时间',
	c_update_timestamp bigint default 0 not null comment '修改时间',
	c_delete_flag tinyint default 0 not null comment '删除标记'
)
comment 'KEY 序列';