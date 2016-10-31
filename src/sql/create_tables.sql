DROP TABLE IF EXISTS i18n_message CASCADE;
DROP TABLE IF EXISTS chart_config CASCADE;
DROP TABLE IF EXISTS data_table_config CASCADE;

DROP TABLE IF EXISTS site_info CASCADE;
DROP TABLE IF EXISTS org_info CASCADE;
DROP TABLE IF EXISTS sys_role CASCADE;
DROP TABLE IF EXISTS user_account CASCADE;
DROP TABLE IF EXISTS user_role CASCADE;

DROP TABLE IF EXISTS asset_file_attachment CASCADE;
DROP TABLE IF EXISTS inspection_checklist CASCADE;
DROP TABLE IF EXISTS inspection_order CASCADE;
DROP TABLE IF EXISTS inspection_order_detail CASCADE;
DROP TABLE IF EXISTS pm_order CASCADE;
DROP TABLE IF EXISTS work_order CASCADE;
DROP TABLE IF EXISTS work_order_history CASCADE;
DROP TABLE IF EXISTS work_order_step CASCADE;
DROP TABLE IF EXISTS work_order_step_detail CASCADE;
DROP TABLE IF EXISTS asset_clinical_record CASCADE;
DROP TABLE IF EXISTS supplier CASCADE;
DROP TABLE IF EXISTS asset_info CASCADE;
DROP TABLE IF EXISTS asset_depreciation CASCADE;

CREATE TABLE i18n_message (
"id" serial NOT NULL,
"msg_type" varchar(60) COLLATE "default" NOT NULL,
"msg_key" varchar(60) COLLATE "default" NOT NULL,
"value_zh" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"value_en" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"value_tw" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"site_id" int4 NOT NULL
);

CREATE TABLE data_table_config(
"id" serial NOT NULL,
"data_table_name" varchar(30) COLLATE "default" NOT NULL,
"data_count_sql" varchar(500) COLLATE "default" NOT NULL,
"data_sql" varchar(500) COLLATE "default" NOT NULL,
"is_default_empty_result" bool NOT NULL,
"is_pagination_supported" bool
);

CREATE TABLE chart_config (
"id" serial NOT NULL,
"chart_name" varchar(30) COLLATE "default" NOT NULL,
"chart_data_sql" varchar(500) COLLATE "default" NOT NULL,
"is_default_empty_result" bool NOT NULL,
"chart_type" varchar(20) COLLATE "default" NOT NULL,
"series_data_field" varchar(20) COLLATE "default" NOT NULL,
"x_axis_data_field" varchar(20) COLLATE "default" NOT NULL,
"y_axis_data_field" varchar(20) COLLATE "default" NOT NULL,
"chart_title" varchar(40) COLLATE "default" DEFAULT NULL::character varying,
"legend_position" varchar(1) COLLATE "default" DEFAULT NULL::character varying,
"legend_rows" int4,
"legend_cols" int4,
"zoom_enabled" bool,
"x_axis_label" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_min" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_max" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_tick_angle" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_tick_count" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_tick_format" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"x_axis_tick_interval" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"y_axis_label" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"y_axis_min" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"y_axis_max" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"add_date_axis" bool
);


CREATE TABLE "site_info" (
"id" serial NOT NULL,
"name" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"name_en" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"alias_name" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"site_description" varchar(256) COLLATE "default" DEFAULT NULL::character varying,
"contact_person" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"contact_phone" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"contact_email" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"location" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"location_en" varchar(256) COLLATE "default" DEFAULT NULL::character varying,
"time_zone" int4,
"default_lang" varchar(16) COLLATE "default" DEFAULT NULL::character varying,
"is_enabled" bool
);

CREATE TABLE org_info (
"id" serial NOT NULL,
site_id int4 NOT NULL,
hospital_id int4,
"name" varchar(64) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"name_en" varchar(64) COLLATE "default",
parent_id int
);

CREATE TABLE "sys_role" (
"id" serial NOT NULL,
"name" varchar(32) COLLATE "default" DEFAULT NULL::character varying,
"role_desc" varchar(64) COLLATE "default" DEFAULT NULL::character varying,
"home_page" varchar(64) COLLATE "default" DEFAULT NULL::character varying
);

CREATE TABLE "user_account" (
"id" serial NOT NULL,
org_id int4 NOT NULL,
"login_name" varchar(16) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"name" varchar(32) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"password" varchar(16) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"email" varchar(32) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"telephone" varchar(16) COLLATE "default" DEFAULT NULL::character varying,
"is_super_admin" bool DEFAULT false NOT NULL,
"is_site_admin" bool DEFAULT false NOT NULL,
"is_local_admin" bool DEFAULT false NOT NULL,
"is_active" bool DEFAULT true NOT NULL,
"is_online" bool DEFAULT false,
site_id int4,
"last_login_time" timestamp(6)
);

CREATE TABLE user_role (
"id" serial NOT NULL,
"user_id" int4,
"role_id" int4
);


create table asset_info(
id serial not null,
site_id int not null,
name varchar(64) not null,
alias_name varchar(64),
function_group int,
function_type int,
function_grade int,
manufacture varchar(64),
vendor varchar(64),
maitanance varchar(64),
maitanance_tel varchar(32),
serial_num varchar(64),
depart_num varchar(64),
financing_num varchar(64),
barcode varchar(64),
modality_id varchar(64),
location_code varchar(64),
location_name varchar(64),
hospital_id int not null,
clinical_dept_id int,
clinical_dept_name varchar(64),
asset_group int,
asset_dept_id int not null,
asset_owner_id int not null,
asset_owner_name varchar(16) not null,
asset_owner_tel varchar(16),
is_valid bool not null,
status int not null,
manufact_date date,
purchase_date date,
arrive_date date,
install_date date,
warranty_date date,
terminate_date date,
last_pm_date date,
last_metering_date date,
last_qa_date date,
purchase_price float,
salvage_value float,
lifecycle int,
depreciation_method int);


create table asset_file_attachment(
id serial not null,
site_id int not null,
asset_id int not null,
name varchar(64) not null,
file_type varchar(64) not null,
file_url varchar(128) not null);


create table inspection_checklist(
id serial not null,
site_id int not null,
asset_id int not null,
checklist_type int not null,
item varchar(64) not null,
display_order int not null);

create table inspection_order(
id serial not null,
site_id int not null,
asset_id int not null,
name varchar(64) not null,
creator_id int not null,
creator_name varchar(16) not null,
create_time timestamp not null,
owner_id int not null,
owner_name varchar(16) not null,
owner_org_id int not null,
owner_org_name varchar(64) not null,
start_time timestamp,
end_time timestamp,
is_finished bool not null,
comments varchar(256),
paper_url varchar(128) );


create table inspection_order_detail(
id serial not null,
site_id int not null,
order_id int not null,
dept_id int not null,
dept_name int not null,
item_id int not null,
item_name int not null,
is_passed bool not null);


create table pm_order(
id serial not null,
site_id int not null,
asset_id int not null,
name varchar(64) not null,
creator_id int not null,
creator_name varchar(16) not null,
create_time timestamp not null,
owner_id int,
owner_name varchar(16),
owner_org_id int,
owner_org_name varchar(64),
start_time timestamp,
end_time timestamp,
is_finished bool not null,
comments varchar(256),
next_time timestamp,
report_url varchar(128));


create table work_order(
id serial not null,
site_id int not null,
asset_id int not null,
name varchar(32) not null,
creator_id int not null,
creator_name varchar(16) not null,
create_time timestamp not null,
requestor_id int not null,
requestor_name varchar(16) not null,
request_time timestamp not null,
request_reason varchar(256) not null,
case_owner_id int,
case_owner_name varchar(16),
case_type int,
case_sub_type int,
case_priority int not null,
is_internal bool not null,
current_person_id int not null,
current_person_name varchar(16),
current_step int not null,
is_closed bool not null,
close_reason varchar(256),
comments varchar(256),
total_man_hour int,
total_price float,
confirmed_start_time timestamp,
confirmed_end_time timestamp);


create table work_order_history(
id serial not null,
site_id int not null,
asset_id int not null,
work_order_id int not null,
update_person_id int not null,
update_person_name varchar(16) not null,
update_time timestamp not null,
update_detail varchar(512) not null);


create table work_order_step(
id serial not null,
site_id int not null,
work_order_id int not null,
step_id int not null,
step_name varchar(32) not null,
owner_id int not null,
owner_name varchar(16) not null,
start_time timestamp not null,
end_time timestamp not null,
description varchar(128),
attachment_url varchar(128) );


create table work_order_step_detail(
id serial not null,
site_id int not null,
step_id int not null,
man_hours int,
accessory varchar(60),
accessory_quantity int,
accessory_price float);


create table asset_clinical_record(
id serial not null,
site_id int not null,
asset_id int not null,
modality_id int not null,
modality_type_id int not null,
modality_type varchar(32) not null,
procedure_id int not null,
procedure_name varchar(32) not null,
procedure_step_id int not null,
procedure_step_name varchar(32) not null,
price_amount float,
price_unit int,
patient_id varchar(64),
patient_name_zh varchar(32),
patient_name_en varchar(32),
patient_age varchar(32),
patient_gender int,
inject_count float,
expose_count float,
film_count int,
exam_date date not null,
exam_start_time time not null,
exam_end_time time not null);


create table supplier(
id serial not null,
site_id int not null,
name varchar(64) not null,
city varchar(32),
address varchar(128),
zipcode varchar(16),
contactor varchar(16),
tel varchar(16));

create table asset_depreciation(
id serial not null,
site_id int not null,
asset_id int not null,
deprecate_date date not null,
deprecate_amount float not null
);


ALTER TABLE asset_info ADD PRIMARY KEY (id);
ALTER TABLE asset_file_attachment ADD PRIMARY KEY (id);
ALTER TABLE inspection_checklist ADD PRIMARY KEY (id);
ALTER TABLE inspection_order ADD PRIMARY KEY (id);
ALTER TABLE inspection_order_detail ADD PRIMARY KEY (id);
ALTER TABLE pm_order ADD PRIMARY KEY (id);
ALTER TABLE work_order ADD PRIMARY KEY (id);
ALTER TABLE work_order_history ADD PRIMARY KEY (id);
ALTER TABLE work_order_step ADD PRIMARY KEY (id);
ALTER TABLE work_order_step_detail ADD PRIMARY KEY (id);
ALTER TABLE asset_clinical_record ADD PRIMARY KEY (id);
ALTER TABLE supplier ADD PRIMARY KEY (id);
ALTER TABLE site_info ADD PRIMARY KEY (id);
ALTER TABLE org_info ADD PRIMARY KEY (id);
ALTER TABLE sys_role ADD PRIMARY KEY (id);
ALTER TABLE user_account ADD PRIMARY KEY (id);
ALTER TABLE user_role ADD PRIMARY KEY (id);
ALTER TABLE i18n_message ADD PRIMARY KEY (id);
ALTER TABLE chart_config ADD PRIMARY KEY (id);
ALTER TABLE data_table_config ADD PRIMARY KEY (id);
ALTER TABLE asset_depreciation ADD PRIMARY KEY (id);

ALTER TABLE "user_account" ADD CONSTRAINT "uk_user_account_login_name" UNIQUE ("login_name");

ALTER TABLE org_info ADD FOREIGN KEY (site_id) REFERENCES site_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE org_info ADD FOREIGN KEY (parent_id) REFERENCES org_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE user_account ADD FOREIGN KEY (org_id) REFERENCES org_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE user_role ADD FOREIGN KEY (user_id) REFERENCES user_account (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE user_role ADD FOREIGN KEY (role_id) REFERENCES sys_role (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE asset_info ADD FOREIGN KEY (hospital_id) REFERENCES org_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE asset_file_attachment ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE asset_clinical_record ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE inspection_checklist ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE inspection_order ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE inspection_order_detail ADD FOREIGN KEY (order_id) REFERENCES inspection_order (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE pm_order ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE work_order ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE work_order_history ADD FOREIGN KEY (work_order_id) REFERENCES work_order (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE work_order_step ADD FOREIGN KEY (work_order_id) REFERENCES work_order (id) ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE work_order_step_detail ADD FOREIGN KEY (step_id) REFERENCES work_order_step (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE asset_depreciation ADD FOREIGN KEY (asset_id) REFERENCES asset_info (id) ON DELETE NO ACTION ON UPDATE NO ACTION;

