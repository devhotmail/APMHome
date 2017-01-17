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
