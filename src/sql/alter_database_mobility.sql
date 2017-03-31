--- begin of drop unused columns
alter table work_order alter column name drop not null;
alter table work_order alter column creator_id drop not null;
alter table work_order alter column creator_name drop not null;
alter table work_order alter column create_time drop not null;
alter table work_order alter column comments drop not null;
alter table work_order alter column close_reason drop not null;
alter table work_order alter column is_closed drop not null;
alter table work_order alter column case_owner_id drop not null;
alter table work_order alter column case_owner_name drop not null;
alter table work_order alter column is_internal drop not null;
alter table work_order alter column name drop not null;

/*
alter table asset_info drop COLUMN clinical_owner_id;
alter table asset_info drop COLUMN clinical_owner_name;
alter table asset_info drop COLUMN clinical_owner_tel;

alter table asset_file_attachment drop COLUMN file_url;

alter table work_order drop column creator_id;
alter table work_order drop column creator_name;
alter table work_order drop column create_time;
alter table work_order drop column comments;
alter table work_order drop column close_reason;
alter table work_order drop column is_closed;
alter table work_order drop column case_owner_id;
alter table work_order drop column case_owner_name;
alter table work_order drop column is_internal;
alter table work_order drop column name;
*/
--- end of drop unused columns


alter table asset_summit alter column rating type float;
ALTER TABLE user_account ADD CONSTRAINT uk_user_account_wechat_id UNIQUE (wechat_id);
alter table user_account add COLUMN leader_user_id int;

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

create table qr_code_attachment(
id serial not null,
qr_code_id int not null,
file_type int not null,	-- 1:照片 / 2: 语音
file_id int not null
);
alter table qr_code_attachment add primary key (id);

/*
drop table if exists account_application
create table account_application(
id serial not null,
wechat_id varchar(64) not null, 
name varchar(32) not null,	--真实姓名
telephone varchar(16) not null,	--电话
hospital_name varchar(64) not null,	--所属医院或供应商
clinical_dept_name varchar(32),	--所属临床科室
role_id int not null,	--角色ID
comment varchar(128),	--备注
application_date date,	--帐号申请日期
status int,	-- 1-待审批 / 2-审批通过 / 3-拒绝
password varchar(16)  --密码
);
alter table account_application add primary key (id);
*/

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

alter table workflow_config add COLUMN max_message_count int;

create table wechat_message_log(
id serial not null,
wechatId varchar(64),
wo_id int,
wo_step_id int,
message_count int not null,
message_type int not null -- 推送类型 1:报修流程超时提醒,  2:二次开单提醒
);

alter table wechat_message_log add primary key (id);

