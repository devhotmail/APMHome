ALTER TABLE "site_info" ADD CONSTRAINT "uk_site_info_name" UNIQUE ("name");
ALTER TABLE "supplier" ADD CONSTRAINT "uk_supplier_name" UNIQUE (site_id, name);
ALTER TABLE "i18n_message" ADD CONSTRAINT "uk_i18n_message_msg_key" UNIQUE (site_id, msg_type, msg_key);
ALTER TABLE "supplier" ADD column supplier_code varchar(32);

ALTER TABLE user_account ADD column wechat_id varchar(64);

DROP TABLE IF EXISTS procedure_name_maping CASCADE;
CREATE TABLE procedure_name_maping (
"id" serial NOT NULL,
site_id int not null,
hospital_id int not null,
"ris_procedure_name" varchar(256) COLLATE "default" NOT NULL,
"apm_procedure_name" varchar(256) COLLATE "default" NOT NULL,
"apm_procedure_id" int NOT NULL
);

ALTER TABLE procedure_name_maping ADD PRIMARY KEY ("id");

ALTER TABLE asset_clinical_record ADD column original_procedure_name varchar(256);
ALTER TABLE asset_clinical_record ADD column original_procedure_id varchar(32);

ALTER TABLE work_order_step_detail ADD column other_expense float;

truncate table field_code_type;
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (2,'caseSubType','故障子类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (3,'assetGroup','设备类型');


alter table asset_contract add column hospital_id int;
alter table asset_file_attachment add column hospital_id int;


ALTER TABLE "asset_clinical_record" ADD CONSTRAINT "uk_asset_clinical_record_ris_id" UNIQUE ("site_id", "hospital_id", "source_record_id");

create index ix_asset_clinical_record_asset_id on asset_clinical_record(asset_id);
create index ix_asset_clinical_record_site_id on asset_clinical_record(site_id);
create index ix_asset_clinical_record_exam_date on asset_clinical_record(exam_date);
create index ix_asset_clinical_record_procedure_name on asset_clinical_record(procedure_name);

alter table asset_info add COLUMN clinical_owner_id int4;
alter table asset_info add COLUMN clinical_owner_name varchar(16);
alter table asset_info add COLUMN clinical_owner_tel varchar(16);
alter table asset_info add COLUMN registration_no varchar(64);
alter table asset_info add COLUMN factory_warranty_date date;
alter table asset_info add COLUMN supplier_id int4;
alter table asset_info add COLUMN qr_code varchar(256);

alter table asset_contract add COLUMN contract_type int;

alter table work_order add COLUMN from_dept_id int;  --报修科室
alter table work_order add COLUMN from_dept_name varchar(64);  --报修科室
alter table work_order add COLUMN ticket_no varchar(32);
alter table work_order add COLUMN reponse_time int;
alter table work_order add COLUMN repaire_time int;

create table asset_summit(
id serial not null,
site_id  int not null,
hospital_id int not null,
asset_id int not null,
asset_group int,
dept_id int,
supplier_id int,
revenue float,
mt_manpower float,
mt_accessory float,
pm_manpower float,
pm_accessory float,
deprecation_cost float,
inject_count float,
expose_count float,
film_count int,
exam_count int,
exam_duration int,
down_time int,
work_order_count int,
rating int,
created date,
last_modified timestamp);

alter table asset_summit add primary key (id);
alter table asset_summit add constraint uk_asset_summit_asset_n_date unique (asset_id, created);

INSERT INTO "i18n_message" VALUES (277,  'label', 'AssetBrowser', '设备分布分析', 'Asset Browser', NULL, -1);
INSERT INTO "i18n_message" VALUES (278,  'label', 'UsageAndClinicalPerformance', '运用与临床绩效', 'Usage And Clinical Performance', NULL, -1);
INSERT INTO "i18n_message" VALUES (279,  'label', 'FinancialPerformanceAndForecast', '财务绩效与预测', 'Financial Performance And Forcast', NULL, -1);
INSERT INTO "i18n_message" VALUES (5624, 'label', 'StaffPerformance', '人员绩效分析','Staff Performance Analysis',null,-1);
INSERT INTO "i18n_message" VALUES (5625, 'label', 'FailureAnalysis', '设备故障分析','Failure Analysis',null,-1);

create table  proc_part(
id serial not null primary key,
name varchar(64) not null
);

create table proc_subpart(
id serial not null primary key,
part_id int not null,
name varchar(64) not null
);

create table proc_step(
id serial not null primary key,
part_id int not null,
name varchar(128) not null
);

create table proc_map(
id serial not null primary key,
asset_group int not null,
part_id int not null,
subpart_id int,
step_id int
);


create table exam_summit(
id serial not null primary key,
site_id  int not null,
hospital_id int not null,
asset_id int not null,
asset_group int not null,
dept_id int not null,
part_id int not null,
subpart_id int not null,
step_id int not null,
exam_count int not null default 0,
created date,
last_modified timestamp
);

create index idx_exam_summit_asset on exam_summit(site_id, hospital_id, asset_id);
create index idx_exam_summit_date on exam_summit(created);
create index idx_exam_summit_step on exam_summit(asset_group, part_id, step_id);
create index idx_exam_summit_step on exam_summit(asset_group, asset_id, part_id);

CREATE TABLE "public"."v2_service_request" (
"id" char(32) COLLATE "default" NOT NULL,
"created_by" varchar(50) COLLATE "default",
"created_date" timestamp(6),
"last_modified_by" varchar(50) COLLATE "default",
"last_modified_date" timestamp(6),
"asset_id" int4 NOT NULL,
"asset_name" varchar(64) COLLATE "default" NOT NULL,
"case_priority" int4 NOT NULL,
"close_time" timestamp(6),
"confirmed_down_time" timestamp(6),
"confirmed_up_time" timestamp(6),
"estimated_close_time" timestamp(6),
"from_dept_id" int4,
"from_dept_name" varchar(64) COLLATE "default",
"hospital_id" int4 NOT NULL,
"hospital_name" varchar(64) COLLATE "default" NOT NULL,
"request_reason" varchar(256) COLLATE "default" NOT NULL,
"request_reason_voice" int4,
"request_time" timestamp(6) NOT NULL,
"requestor_id" int4 NOT NULL,
"requestor_name" varchar(16) COLLATE "default" NOT NULL,
"reponse_time" timestamp(6),
"site_id" int4 NOT NULL,
"status" int4,
"equipment_taker" varchar(255) COLLATE "default",
"take_time" timestamp(6),
CONSTRAINT "v2_service_request_pkey" PRIMARY KEY ("id")
);

CREATE TABLE "public"."v2_work_order" (
"id" char(32) COLLATE "default" NOT NULL,
"created_by" varchar(50) COLLATE "default",
"created_date" timestamp(6),
"last_modified_by" varchar(50) COLLATE "default",
"last_modified_date" timestamp(6),
"asset_id" int4 NOT NULL,
"close_time" timestamp(6),
"current_person_id" int4 NOT NULL,
"current_person_name" varchar(16) COLLATE "default",
"current_step_id" int4 NOT NULL,
"current_step_name" varchar(16) COLLATE "default" NOT NULL,
"feedback_comment" varchar(255) COLLATE "default",
"feedback_rating" int4,
"hospital_id" int4 NOT NULL,
"int_ext_type" int4,
"parent_wo_id" int4,
"pat_actions" varchar(255) COLLATE "default",
"pat_problems" varchar(255) COLLATE "default",
"pat_tests" varchar(255) COLLATE "default",
"site_id" int4 NOT NULL,
"sr_id" varchar(255) COLLATE "default" NOT NULL,
"status" int4,
"total_man_hour" int4,
"total_price" float8,
"case_type" int4,
CONSTRAINT "v2_work_order_pkey" PRIMARY KEY ("id")
);

alter table inspection_order add COLUMN man_hours int;
alter table pm_order add COLUMN man_hours int;
alter table work_order add column feedback_rating int;

alter table pm_order add COLUMN planned_time timestamp;
alter table pm_order drop COLUMN nearest_sr_time;
alter table pm_order add COLUMN nearest_sr_days int;
alter table pm_order add COLUMN nearest_sr_id character(32);

alter table V2_service_request add COLUMN nearest_sr_days int;
alter table V2_service_request add COLUMN nearest_sr_id character(32);

INSERT INTO "i18n_message" VALUES (5626, 'label', 'ProcessAnalysis', '维修流程分析','Process Analysis',null,-1);
INSERT INTO "i18n_message" VALUES (5627, 'label', 'MaintenanceCost', '维修成本分析','Maintenance Cost',null,-1);

