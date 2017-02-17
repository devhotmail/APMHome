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
ALTER TABLE site_info ADD column manhour_price float;

truncate table field_code_type;
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (2,'caseSubType','故障子类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (3,'assetGroup','设备类型');


alter table asset_contract add column hospital_id int;
alter table asset_file_attachment add column hospital_id int;

alter table asset_info alter COLUMN function_type type varchar(64);


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

alter table asset_contract add COLUMN contract_type int;

alter table work_order add COLUMN from_dept_id int;
alter table work_order add COLUMN from_dept_name varchar(64);
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
revenue float,
maintenance_cost float,
deprecation_cost float,
inject_count int,
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
