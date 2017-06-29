alter table asset_summit alter column rating type float;
ALTER TABLE user_account ADD CONSTRAINT uk_user_account_wechat_id UNIQUE (wechat_id);
alter table user_account add COLUMN leader_user_id int;
alter table user_account alter column login_name type varchar(64);

alter table asset_info alter COLUMN qr_code type varchar(16);
ALTER TABLE asset_info ADD CONSTRAINT uk_asset_info_qr_code UNIQUE (qr_code);

alter table asset_info alter column asset_dept_id DROP NOT NULL;
alter table asset_info alter column asset_owner_id DROP NOT NULL;
alter table asset_info alter column asset_owner_name DROP NOT NULL;
alter table asset_info add COLUMN asset_owner_id2 int;		--责任人B
alter table asset_info add COLUMN asset_owner_name2 varchar(16);	--责任人B姓名
alter table asset_info add COLUMN asset_owner_tel2 varchar(16);	--责任人B电话
alter table asset_info add COLUMN fe_user_id int;	--FE工程师 userId
alter table asset_info add COLUMN dispatch_mode int; --1:专人派工 / 2:抢单 /3:自动派工

alter table asset_info alter COLUMN asset_owner_name type varchar(32);
alter table asset_info alter COLUMN asset_owner_name2 type varchar(32);
alter table asset_info alter COLUMN clinical_owner_name type varchar(32);

alter table asset_info add COLUMN eam_id varchar(32);
alter table asset_info add COLUMN system_id varchar(32);
alter table asset_info add COLUMN system_num1 varchar(32);
alter table asset_info add COLUMN system_num2 varchar(32);
alter table asset_info add COLUMN system_num3 varchar(32);
alter table asset_info add COLUMN system_num4 varchar(32);
alter table asset_info add COLUMN system_num5 varchar(32);

alter table asset_info add COLUMN is_deleted bool;
alter table asset_info add COLUMN parent_asset_id int;

create table qr_code_lib(
id serial not null,
site_id int not null,
hospital_id int not null,
qr_code varchar(16) not null,
issue_date date not null,	-- 发行日期
submit_date date,	--扫码建档日期
submit_wechat_id varchar(64),	--扫码建档者 openId
comment varchar(512),	--备注
status int not null -- 1:已发行(未上传) / 2: 已上传(待建档) / 3: 已建档(待删除)
);
alter table qr_code_lib add primary key (id);

alter table qr_code_lib alter column submit_date type timestamp;  --扫码建档时间
alter table qr_code_lib add COLUMN feedback varchar(512);		--反馈信息
alter table qr_code_lib add COLUMN asset_name varchar(16);		--设备名称
alter table qr_code_lib add COLUMN asset_group int;		--设备类型
alter table qr_code_lib add COLUMN org_id int;		--科室
alter table qr_code_lib add COLUMN user_id int;		--科室负责人
ALTER TABLE qr_code_lib ADD CONSTRAINT uk_qr_code_lib_qrcode UNIQUE (qr_code);

create table qr_code_attachment(
id serial not null,
qr_code_id int not null,
file_type int not null,	-- 1:照片 / 2: 语音
file_id int not null
);
alter table qr_code_attachment add primary key (id);

create table message_subscriber(
id serial not null,
site_id int not null,
hospital_id int not null,
asset_id int not null,		--订阅的相关设备
subscribe_user_id int not null,	--订阅者ID
receive_msg_mode int not null, -- 1-只关注重要消息(报修，关单) / 2- 关注所有消息 / 3-不关注
is_receive_timeout_msg bool not null,	--是否接受流程超期提醒
is_receive_chat_msg bool not null	--是否接收聊天消息
);
alter table message_subscriber add primary key (id);


alter table work_order add column status int;  -- 1-在修 / 2-完成 / 3-取消  (2: 0分表明没有评价过)
alter table work_order add column int_ext_type int;  -- 1-内部 / 2-外部 / 3-混合
alter table work_order add column parent_wo_id int;	--二次工单
alter table work_order add column feedback_rating int;  --默认是0, 打分范围:1~5 
alter table work_order add column feedback_comment varchar(128); --评价comments
alter table work_order add column request_reason_voice int;	--故障说明(语音)
alter table work_order add column pat_problems varchar(128);  --PAT 的 Problems(问题描述)
alter table work_order add column pat_actions varchar(128);  --PAT 的 Actions(解决方案)
alter table work_order add column pat_tests varchar(128);	 --PAT 的 Tests(测试方法)
alter table work_order add column close_time timestamp;	 --关单时间
alter table work_order add column estimated_close_time timestamp;	 --预估修复时间
alter table work_order add column hospital_name varchar(64);	 --所属医院/院区

alter table work_order_step_detail add column cowoker_user_id int; --协作者
alter table work_order_step_detail add column cowoker_user_name varchar(16); --协作者姓名


create table work_order_photo(
id serial not null,
site_id int not null,
work_order_id int not null,
photo_id int
);
alter table work_order_photo add primary key (id);

create table work_order_msg(
id serial not null,
site_id int not null,
work_order_id int not null,
sender_user_id int not null,	--消息发送者
sender_user_name varchar(16),	--消息发送者
send_time timestamp,	--消息发送时间
message varchar(128)	--发送的消息
);
alter table work_order_msg add primary key (id);


create table workflow_config(
id serial not null,
site_id int not null,
hospital_id int not null,
dispatch_mode int not null, --1: 专人派工 / 2: 抢单 /3: 自动派工
dispatch_user_id int,		--派工人
dispatch_user_name varchar(16),	--派工人
timeout_dispatch int, --派工环节的超时提醒阈值(分钟)
timeout_accept int,   --接单环节的超时提醒阈值(分钟)
timeout_repair int, --维修环节的超时提醒阈值(分钟)
timeout_close int, --关单环节的超时提醒阈值(分钟)
order_reopen_timeframe int --二次开单的最大时间间隔
);
alter table workflow_config add primary key (id);
ALTER TABLE workflow_config ADD CONSTRAINT uk_workflow_hospital_id UNIQUE (hospital_id);

alter table workflow_config add COLUMN max_message_count int;
ALTER TABLE workflow_config ADD COLUMN dispatch_user_id2 int4 NULL ;
ALTER TABLE workflow_config ADD COLUMN dispatch_user_name2 varchar(16) NULL ;
ALTER TABLE workflow_config ADD COLUMN is_take_order_enabled bool;  --是否运行抢单

create table wechat_message_log(
id serial not null,
wechatId varchar(64),
wo_id int,
wo_step_id int,
message_count int not null,
message_type int not null -- 推送类型 1:报修流程超时提醒,  2:二次开单提醒
);

alter table wechat_message_log add primary key (id);

create table  asset_fault_type(
id serial NOT NULL,
asset_group_id int4,
fault_name varchar(64)
);
alter table asset_fault_type add primary key (id);

alter table inspection_order add COLUMN man_hours int;
alter table pm_order add COLUMN man_hours int;

--设备tag
create table asset_tag(
id serial not null,
site_id int,
hospital_id int,
name varchar(64)
);
alter table asset_tag add primary key (id);

--设备tag对应的规则，目前不保存规则，而是保存规则匹配的所有资产的ID
create table asset_tag_rule(
id serial not null,
tag_id int,
asset_id int
);
alter table asset_tag_rule add primary key (id);

--医工分组
create table biomed_group(
id serial not null,
site_id int,
hospital_id int,
group_name varchar(64)
);
alter table biomed_group add primary key (id);

--医工分组中的医工
create table biomed_group_user(
id serial not null,
group_id int,
user_id int,
user_name varchar(64)
);
alter table biomed_group_user add primary key (id);

--设备tag对应的医工分组
create table asset_tag_biomed_group(
id serial not null,
tag_id int,
biomed_group_id int
);
alter table asset_tag_biomed_group add primary key (id);

--设备tag对应的消息关注者
create table asset_tag_msg_subscriber(
id serial not null,
tag_id int not null,		--订阅的相关设备 tag
subscribe_user_id int not null,	--订阅者ID
receive_msg_mode int not null, -- 1-只关注重要消息(报修，关单) / 2- 关注所有消息 / 3-不关注
is_receive_timeout_msg bool not null,	--是否接受流程超期提醒
is_receive_chat_msg bool not null	--是否接收聊天消息
);
alter table asset_tag_msg_subscriber add primary key (id);

CREATE TABLE v2_blob_object (
id char(32)  NOT NULL,
created_by varchar(50),
created_date timestamp(6),
last_modified_by varchar(50),
last_modified_date timestamp(6),
bo_id varchar(255) NOT NULL,
object_name varchar(255),
object_size int8,
object_source int4,
object_storage_id varchar(255),
object_type varchar(255),
CONSTRAINT v2_blob_object_pkey PRIMARY KEY (id)
);

CREATE TABLE v2_service_request (
id char(32)  NOT NULL,
created_by varchar(50) ,
created_date timestamp(6),
last_modified_by varchar(50) ,
last_modified_date timestamp(6),
asset_id int4 NOT NULL,
asset_name varchar(64)  NOT NULL,
case_priority int4 NOT NULL,
close_time timestamp(6),
confirmed_down_time timestamp(6),
confirmed_up_time timestamp(6),
estimated_close_time timestamp(6),
from_dept_id int4,
from_dept_name varchar(64) ,
hospital_id int4 NOT NULL,
hospital_name varchar(64)  NOT NULL,
request_reason varchar(256)  NOT NULL,
request_reason_voice int4,
request_time timestamp(6) NOT NULL,
requestor_id int4 NOT NULL,
requestor_name varchar(16)  NOT NULL,
reponse_time timestamp(6),
site_id int4 NOT NULL,
status int4,
CONSTRAINT v2_service_request_pkey PRIMARY KEY (id)
);

CREATE TABLE v2_work_order (
id char(32)  NOT NULL,
created_by varchar(50) ,
created_date timestamp(6),
last_modified_by varchar(50) ,
last_modified_date timestamp(6),
asset_id int4 NOT NULL,
close_time timestamp(6),
current_person_id int4 NOT NULL,
current_person_name varchar(16) ,
current_step_id int4 NOT NULL,
current_step_name varchar(16)  NOT NULL,
feedback_comment varchar(255) ,
feedback_rating int4,
hospital_id int4 NOT NULL,
int_ext_type int4,
parent_wo_id int4,
pat_actions varchar(255) ,
pat_problems varchar(255) ,
pat_tests varchar(255) ,
site_id int4 NOT NULL,
sr_id varchar(255)  NOT NULL,
status int4,
total_man_hour int4,
total_price float8,
CONSTRAINT v2_work_order_pkey PRIMARY KEY (id)
);


CREATE TABLE v2_work_order_detail (
id char(32)  NOT NULL,
created_by varchar(50) ,
created_date timestamp(6),
last_modified_by varchar(50) ,
last_modified_date timestamp(6),
parts_quantity int4,
cowoker_user_id int4,
cowoker_user_name varchar(255) ,
man_hours int4,
other_expense float8,
parts varchar(60) ,
parts_price float8,
site_id int4 NOT NULL,
wo_id varchar(255)  NOT NULL,
CONSTRAINT v2_work_order_detail_pkey PRIMARY KEY (id)
);

CREATE TABLE v2_work_order_step (
id char(32)  NOT NULL,
created_by varchar(50) ,
created_date timestamp(6),
last_modified_by varchar(50) ,
last_modified_date timestamp(6),
comments varchar(128) ,
end_time timestamp(6),
owner_id int4 NOT NULL,
owner_name varchar(16)  NOT NULL,
site_id int4 NOT NULL,
start_time timestamp(6),
step_id int4 NOT NULL,
step_name varchar(16)  NOT NULL,
wo_id varchar(255)  NOT NULL,
CONSTRAINT v2_work_order_step_pkey PRIMARY KEY (id)
);


Alter table site_info add column password_lifetime int;  --密码有效天数
Alter table user_account add column password_update_date date; --上次密码修改时间
Alter table user_account add column password_error_count int; --密码连续错误次数
Alter table user_account add column is_locked bool;	--帐号是否锁定


alter table pm_order add column plan_time timestamp;
alter table pm_order add column nearest_sr_time timestamp;

alter table inspection_order add column plan_time timestamp;

--68码数据库初始
--DELETE FROM i18n_message WHERE msg_type='assetFunctionType';

update asset_info SET function_group = 6801 where function_group = 2;
update asset_info SET function_group = 6802 where function_group = 3;
update asset_info SET function_group = 6803 where function_group = 4;
update asset_info SET function_group = 6804 where function_group = 5;
update asset_info SET function_group = 6805 where function_group = 6;
update asset_info SET function_group = 6806 where function_group = 7;
update asset_info SET function_group = 6807 where function_group = 8;
update asset_info SET function_group = 6808 where function_group = 9;
update asset_info SET function_group = 6809 where function_group = 10;
update asset_info SET function_group = 6810 where function_group = 11;
update asset_info SET function_group = 6812 where function_group = 12;
update asset_info SET function_group = 6813 where function_group = 13;
update asset_info SET function_group = 6815 where function_group = 14;
update asset_info SET function_group = 6816 where function_group = 15;
update asset_info SET function_group = 6820 where function_group = 16;
update asset_info SET function_group = 6821 where function_group = 17;
update asset_info SET function_group = 6822 where function_group = 18;
update asset_info SET function_group = 6823 where function_group = 19;
update asset_info SET function_group = 6824 where function_group = 20;
update asset_info SET function_group = 6825 where function_group = 21;
update asset_info SET function_group = 6826 where function_group = 22;
update asset_info SET function_group = 6827 where function_group = 23;
update asset_info SET function_group = 6828 where function_group = 24;
update asset_info SET function_group = 6830 where function_group = 25;
update asset_info SET function_group = 6831 where function_group = 26;
update asset_info SET function_group = 6832 where function_group = 27;
update asset_info SET function_group = 6833 where function_group = 28;
update asset_info SET function_group = 6834 where function_group = 29;
update asset_info SET function_group = 6840 where function_group = 30;
update asset_info SET function_group = 6841 where function_group = 31;
update asset_info SET function_group = 6845 where function_group = 32;
update asset_info SET function_group = 6846 where function_group = 33;
update asset_info SET function_group = 6854 where function_group = 34;
update asset_info SET function_group = 6855 where function_group = 35;
update asset_info SET function_group = 6856 where function_group = 36;
update asset_info SET function_group = 6857 where function_group = 37;
update asset_info SET function_group = 6858 where function_group = 38;
update asset_info SET function_group = 6863 where function_group = 39;
update asset_info SET function_group = 6864 where function_group = 40;
update asset_info SET function_group = 6865 where function_group = 41;
update asset_info SET function_group = 6866 where function_group = 42;
update asset_info SET function_group = 6870 where function_group = 43;
update asset_info SET function_group = 6877 where function_group = 44;