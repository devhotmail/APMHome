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
alter table asset_info add COLUMN supplier_id int4;
alter table asset_info add COLUMN qr_code varchar(256);

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
supplier_id int,
revenue float,
maintenance_cost float,
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

INSERT INTO "i18n_message" VALUES (269, 'label', 'AssetBrowser', '设备分布分析', 'Asset Browser', NULL, -1);
INSERT INTO "i18n_message" VALUES (270, 'label', 'UsageAndClinicalPerformance', '运用与临床绩效', 'Usage And Clinical Performance', NULL, -1);
INSERT INTO "i18n_message" VALUES (271, 'label', 'FinancialPerformanceAndForecast', '财务绩效与预测', 'Financial Performance And Forcast', NULL, -1);
INSERT INTO "i18n_message" VALUES (5624,'label','StaffPerformance', '人员绩效分析','Staff Performance Analysis',null,-1);
INSERT INTO "i18n_message" VALUES (5625,'label','FailureAnalysis', '设备故障分析','Failure Analysis',null,-1);

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

create index ix_exam_summit_site_id on exam_summit(site_id);
create index ix_exam_summit_hospital_id on exam_summit(hospital_id);
create index ix_exam_summit_asset_id on exam_summit(asset_id);
create index ix_exam_summit_asset_group on exam_summit(asset_group);
create index ix_exam_summit_dept_id on exam_summit(dept_id);
create index ix_exam_summit_part_id on exam_summit(part_id);
create index ix_exam_summit_subpart_id on exam_summit(subpart_id);
create index ix_exam_summit_step_id on exam_summit(step_id);

insert into proc_part(name) values ('头部');
insert into proc_part(name) values ('颈部');
insert into proc_part(name) values ('胸部');
insert into proc_part(name) values ('腹部');
insert into proc_part(name) values ('盆部');
insert into proc_part(name) values ('脊柱');
insert into proc_part(name) values ('上肢');
insert into proc_part(name) values ('下肢');
insert into proc_part(name) values ('其它');

insert into proc_step(part_id, name) values (1,'head_step1');
insert into proc_step(part_id, name) values (1,'head_step2');
insert into proc_step(part_id, name) values (1,'head_step3');
insert into proc_step(part_id, name) values (1,'head_step4');
insert into proc_step(part_id, name) values (1,'head_step5');

insert into proc_step(part_id, name) values (2,'neck_step1');
insert into proc_step(part_id, name) values (2,'neck_step2');
insert into proc_step(part_id, name) values (2,'neck_step3');
insert into proc_step(part_id, name) values (2,'neck_step4');
insert into proc_step(part_id, name) values (2,'neck_step5');

insert into proc_step(part_id, name) values (3,'chest_step1');
insert into proc_step(part_id, name) values (3,'chest_step2');
insert into proc_step(part_id, name) values (3,'chest_step3');
insert into proc_step(part_id, name) values (3,'chest_step4');
insert into proc_step(part_id, name) values (3,'chest_step5');

insert into proc_step(part_id, name) values (4,'abdomen_step1');
insert into proc_step(part_id, name) values (4,'abdomen_step2');
insert into proc_step(part_id, name) values (4,'abdomen_step3');
insert into proc_step(part_id, name) values (4,'abdomen_step4');
insert into proc_step(part_id, name) values (4,'abdomen_step5');

insert into proc_step(part_id, name) values (5,'pelvic_step1');
insert into proc_step(part_id, name) values (5,'pelvic_step2');
insert into proc_step(part_id, name) values (5,'pelvic_step3');
insert into proc_step(part_id, name) values (5,'pelvic_step4');
insert into proc_step(part_id, name) values (5,'pelvic_step5');

insert into proc_step(part_id, name) values (6,'spine_step1');
insert into proc_step(part_id, name) values (6,'spine_step2');
insert into proc_step(part_id, name) values (6,'spine_step3');
insert into proc_step(part_id, name) values (6,'spine_step4');
insert into proc_step(part_id, name) values (6,'spine_step5');

insert into proc_step(part_id, name) values (7,'upper_limbs_step1');
insert into proc_step(part_id, name) values (7,'upper_limbs_step2');
insert into proc_step(part_id, name) values (7,'upper_limbs_step3');
insert into proc_step(part_id, name) values (7,'upper_limbs_step4');
insert into proc_step(part_id, name) values (7,'upper_limbs_step5');

insert into proc_step(part_id, name) values (8,'lower_limbs_step1');
insert into proc_step(part_id, name) values (8,'lower_limbs_step2');
insert into proc_step(part_id, name) values (8,'lower_limbs_step3');
insert into proc_step(part_id, name) values (8,'lower_limbs_step4');
insert into proc_step(part_id, name) values (8,'lower_limbs_step5');

insert into proc_step(part_id, name) values (9,'else_step1');
insert into proc_step(part_id, name) values (9,'else_step2');
insert into proc_step(part_id, name) values (9,'else_step3');
insert into proc_step(part_id, name) values (9,'else_step4');
insert into proc_step(part_id, name) values (9,'else_step5');

