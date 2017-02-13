drop table asset_clinical_record_delta;
drop table asset_clinical_record_history;


CREATE TABLE "public"."asset_clinical_record_delta" (
"id" serial NOT NULL,
"modality_id" varchar(64) COLLATE "default",
"modality_type_id" int4 NOT NULL,
"modality_type" varchar(32) COLLATE "default" NOT NULL,
"procedure_id" int4 NOT NULL,
"procedure_name" varchar(256) COLLATE "default" NOT NULL,
"procedure_step_id" int4 NOT NULL,
"procedure_step_name" varchar(256) COLLATE "default" NOT NULL,
"price_amount" float8,
"price_unit" varchar(16) COLLATE "default",
"inject_count" float8,
"expose_count" float8,
"film_count" int4,
"exam_date" date NOT NULL,
"exam_start_time" time(6) NOT NULL,
"exam_duration" int4 NOT NULL,
"source_system" varchar(32) COLLATE "default",
"source_record_id" varchar(64) COLLATE "default",
"create_time" date DEFAULT now(),
"upload_status_prod" varchar(1) COLLATE "default" DEFAULT 'N'::character varying,
"upload_status_uat" varchar(1) COLLATE "default" DEFAULT 'N'::character varying,
"upload_time_prod" timestamp(6),
"upload_time_uat" timestamp(6),
"err_msg_prod" varchar(256) COLLATE "default",
"err_msg_uat" varchar(256) COLLATE "default");


CREATE TABLE "public"."asset_clinical_record_history" (
"id" serial NOT NULL,
"modality_id" varchar(64) COLLATE "default",
"modality_type_id" int4 NOT NULL,
"modality_type" varchar(32) COLLATE "default" NOT NULL,
"procedure_id" int4 NOT NULL,
"procedure_name" varchar(256) COLLATE "default" NOT NULL,
"procedure_step_id" int4 NOT NULL,
"procedure_step_name" varchar(256),
"price_amount" float8,
"price_unit" varchar(16) COLLATE "default",
"inject_count" float8,
"expose_count" float8,
"film_count" int4,
"exam_date" date NOT NULL,
"exam_start_time" time(6) NOT NULL,
"exam_duration" int4 NOT NULL,
"source_system" varchar(32) COLLATE "default",
"source_record_id" varchar(64) COLLATE "default",
"create_time" date DEFAULT now(),
"upload_status" varchar(1) COLLATE "default" DEFAULT 'N'::character varying,
"upload_time" timestamp(6),
"err_msg" varchar(256) COLLATE "default"
);


ALTER TABLE "public"."asset_clinical_record_delta" ADD PRIMARY KEY ("id");
ALTER TABLE "public"."asset_clinical_record_history" ADD PRIMARY KEY ("id");

create index idx_clinical_record_delta_upload_prod on asset_clinical_record_delta(upload_status_prod);
create index idx_clinical_record_deltaupload_uat on asset_clinical_record_delta(upload_status_uat);

create index idx_clinical_record_history on asset_clinical_record_history(upload_status);

ALTER TABLE "asset_clinical_record_delta" ADD CONSTRAINT "uk_asset_clinical_record_delta_source_id" UNIQUE (source_record_id);
ALTER TABLE "asset_clinical_record_history" ADD CONSTRAINT "uk_asset_clinical_record_his_source_id" UNIQUE (source_record_id);
