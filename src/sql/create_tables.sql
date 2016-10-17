
CREATE SEQUENCE "public"."asset_clinical_record_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for asset_file_attachment_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."asset_file_attachment_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for asset_info_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."asset_info_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for chart_config_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."chart_config_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
SELECT setval('"public"."chart_config_id_seq"', 1, true);

-- ----------------------------
-- Sequence structure for data_table_config_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."data_table_config_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 6
 CACHE 1;
SELECT setval('"public"."data_table_config_id_seq"', 6, true);

-- ----------------------------
-- Sequence structure for demo_data_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."demo_data_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for i18n_message_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."i18n_message_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;
SELECT setval('"public"."i18n_message_id_seq"', 3, true);

-- ----------------------------
-- Sequence structure for inspection_check_list_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."inspection_check_list_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for inspection_order_detail_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."inspection_order_detail_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for inspection_order_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."inspection_order_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for institution_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."institution_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
SELECT setval('"public"."institution_id_seq"', 1, true);

-- ----------------------------
-- Sequence structure for organization_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."organization_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
SELECT setval('"public"."organization_id_seq"', 1, true);

-- ----------------------------
-- Sequence structure for pm_order_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."pm_order_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for supplier_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."supplier_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for sys_role_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."sys_role_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 3
 CACHE 1;
SELECT setval('"public"."sys_role_id_seq"', 3, true);

-- ----------------------------
-- Sequence structure for user_account_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."user_account_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;
SELECT setval('"public"."user_account_id_seq"', 1, true);

-- ----------------------------
-- Sequence structure for user_role_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."user_role_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for work_order_history_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."work_order_history_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for work_order_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."work_order_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for work_order_step_detail_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."work_order_step_detail_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Sequence structure for work_order_step_id_seq
-- ----------------------------

CREATE SEQUENCE "public"."work_order_step_id_seq"
 INCREMENT 1
 MINVALUE 1
 MAXVALUE 9223372036854775807
 START 1
 CACHE 1;

-- ----------------------------
-- Table structure for asset_clinical_record
-- ----------------------------
DROP TABLE IF EXISTS "public"."asset_clinical_record";
CREATE TABLE "public"."asset_clinical_record" (
"id" int4 DEFAULT nextval('asset_clinical_record_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"asset_id" int4 NOT NULL,
"modality_id" int4 NOT NULL,
"modality_type" int4 NOT NULL,
"procedure_id" int4 NOT NULL,
"procedure_name" varchar(40) COLLATE "default",
"procedure_step_id" int4,
"procedure_step_name" varchar(40) COLLATE "default",
"price_amount" float8,
"inject_count" float8,
"expose_count" numeric(53),
"film_count" int4,
"exam_date" date NOT NULL,
"exam_start_time" time(6),
"exam_end_time" time(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for asset_file_attachment
-- ----------------------------
DROP TABLE IF EXISTS "public"."asset_file_attachment";
CREATE TABLE "public"."asset_file_attachment" (
"id" int4 DEFAULT nextval('asset_file_attachment_id_seq'::regclass) NOT NULL,
"asset_id" int4 NOT NULL,
"file_type" int4 NOT NULL,
"file_url" varchar(100) COLLATE "default" NOT NULL,
"site_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for asset_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."asset_info";
CREATE TABLE "public"."asset_info" (
"id" int4 DEFAULT nextval('asset_info_id_seq'::regclass) NOT NULL,
"name" varchar(40) COLLATE "default" NOT NULL,
"hospital_id" int4 NOT NULL,
"department_id" int4,
"owner_org_id" int4 NOT NULL,
"owner_user_id" int4 NOT NULL,
"site_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for chart_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."chart_config";
CREATE TABLE "public"."chart_config" (
"id" int4 DEFAULT nextval('chart_config_id_seq'::regclass) NOT NULL,
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
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for data_table_config
-- ----------------------------
DROP TABLE IF EXISTS "public"."data_table_config";
CREATE TABLE "public"."data_table_config" (
"id" int4 DEFAULT nextval('data_table_config_id_seq'::regclass) NOT NULL,
"data_table_name" varchar(30) COLLATE "default" NOT NULL,
"data_count_sql" varchar(500) COLLATE "default" NOT NULL,
"data_sql" varchar(500) COLLATE "default" NOT NULL,
"is_default_empty_result" bool NOT NULL,
"is_pagination_supported" bool
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for demo_data
-- ----------------------------
DROP TABLE IF EXISTS "public"."demo_data";
CREATE TABLE "public"."demo_data" (
"id" int4 DEFAULT nextval('demo_data_id_seq'::regclass) NOT NULL,
"name" varchar(255) COLLATE "default" NOT NULL,
"number1" int4 NOT NULL,
"number2" int4 DEFAULT 3 NOT NULL,
"number3" numeric(4,2) DEFAULT 4.99 NOT NULL,
"number4" int4,
"type" varchar(10) COLLATE "default" DEFAULT 'G'::character varying,
"the_date" varchar(20) COLLATE "default" DEFAULT NULL::character varying
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for i18n_message
-- ----------------------------
DROP TABLE IF EXISTS "public"."i18n_message";
CREATE TABLE "public"."i18n_message" (
"id" int4 DEFAULT nextval('i18n_message_id_seq'::regclass) NOT NULL,
"msg_type" varchar(60) COLLATE "default" NOT NULL,
"msg_key" varchar(60) COLLATE "default" NOT NULL,
"value_zh" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"value_en" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"value_tw" varchar(128) COLLATE "default" DEFAULT NULL::character varying,
"site_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for inspection_check_list
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_check_list";
CREATE TABLE "public"."inspection_check_list" (
"id" int4 DEFAULT nextval('inspection_check_list_id_seq'::regclass) NOT NULL,
"asset_id" int4 NOT NULL,
"item" varchar(100) COLLATE "default" NOT NULL,
"site_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for inspection_order
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_order";
CREATE TABLE "public"."inspection_order" (
"id" int4 DEFAULT nextval('inspection_order_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"name" varchar(40) COLLATE "default" NOT NULL,
"asset_id" int4 NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for inspection_order_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."inspection_order_detail";
CREATE TABLE "public"."inspection_order_detail" (
"id" int4 DEFAULT nextval('inspection_order_detail_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"asset_id" int4 NOT NULL,
"order_id" int4 NOT NULL,
"dept_id" int4 NOT NULL,
"item_id" int4 NOT NULL,
"is_passed" bool NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for org_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."org_info";
CREATE TABLE "public"."org_info" (
"id" int4 DEFAULT nextval('organization_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"name" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"name_en" varchar(50) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for pm_order
-- ----------------------------
DROP TABLE IF EXISTS "public"."pm_order";
CREATE TABLE "public"."pm_order" (
"id" int4 DEFAULT nextval('pm_order_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"asset_id" int4 NOT NULL,
"name" varchar(60) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for site_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."site_info";
CREATE TABLE "public"."site_info" (
"id" int4 DEFAULT nextval('institution_id_seq'::regclass) NOT NULL,
"name" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"name_en" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"alias" varchar(50) COLLATE "default" DEFAULT NULL::character varying,
"description" varchar(200) COLLATE "default" DEFAULT NULL::character varying,
"contact_person" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"contact_phone" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"contact_email" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"location" varchar(100) COLLATE "default" DEFAULT NULL::character varying,
"location_en" varchar(200) COLLATE "default" DEFAULT NULL::character varying,
"time_zone" int4,
"default_lang" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"is_enabled" bool
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for supplier
-- ----------------------------
DROP TABLE IF EXISTS "public"."supplier";
CREATE TABLE "public"."supplier" (
"id" int4 DEFAULT nextval('supplier_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"name" varchar(60) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."sys_role";
CREATE TABLE "public"."sys_role" (
"id" int4 DEFAULT nextval('sys_role_id_seq'::regclass) NOT NULL,
"name" varchar(50) COLLATE "default" DEFAULT NULL::character varying,
"role_desc" varchar(50) COLLATE "default" DEFAULT NULL::character varying
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_account";
CREATE TABLE "public"."user_account" (
"id" int4 DEFAULT nextval('user_account_id_seq'::regclass) NOT NULL,
"org_id" int4 NOT NULL,
"login_name" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"name" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"password" varchar(40) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"email" varchar(50) COLLATE "default" DEFAULT NULL::character varying NOT NULL,
"telephone" varchar(20) COLLATE "default" DEFAULT NULL::character varying,
"is_super_admin" bool DEFAULT false NOT NULL,
"is_tenant_admin" bool DEFAULT false NOT NULL,
"is_active" bool DEFAULT true NOT NULL,
"is_online" bool DEFAULT false,
"site_id" int4,
"last_login_time" timestamp(6)
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS "public"."user_role";
CREATE TABLE "public"."user_role" (
"id" int4 DEFAULT nextval('user_role_id_seq'::regclass) NOT NULL,
"user_id" int4,
"role_id" int4
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for work_order
-- ----------------------------
DROP TABLE IF EXISTS "public"."work_order";
CREATE TABLE "public"."work_order" (
"id" int4 DEFAULT nextval('work_order_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"asset_id" int4 NOT NULL,
"name" varchar(40) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for work_order_history
-- ----------------------------
DROP TABLE IF EXISTS "public"."work_order_history";
CREATE TABLE "public"."work_order_history" (
"id" int4 DEFAULT nextval('work_order_history_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"work_order_id" int4 NOT NULL,
"update_time" timestamp(6) NOT NULL,
"update_detail" varchar(200) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for work_order_step
-- ----------------------------
DROP TABLE IF EXISTS "public"."work_order_step";
CREATE TABLE "public"."work_order_step" (
"id" int4 DEFAULT nextval('work_order_step_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"work_order_id" int4 NOT NULL,
"step_name" varchar(40) COLLATE "default" NOT NULL
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Table structure for work_order_step_detail
-- ----------------------------
DROP TABLE IF EXISTS "public"."work_order_step_detail";
CREATE TABLE "public"."work_order_step_detail" (
"id" int4 DEFAULT nextval('work_order_step_detail_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"work_order_id" int4 NOT NULL,
"step_id" int4 NOT NULL,
"man_hours" int4 NOT NULL,
"accessory" varchar(60) COLLATE "default" NOT NULL,
"accessory_quantity" int4 NOT NULL,
"accessory_price" float8
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------
ALTER SEQUENCE "public"."asset_clinical_record_id_seq" OWNED BY "asset_clinical_record"."id";
ALTER SEQUENCE "public"."asset_file_attachment_id_seq" OWNED BY "asset_file_attachment"."id";
ALTER SEQUENCE "public"."asset_info_id_seq" OWNED BY "asset_info"."id";
ALTER SEQUENCE "public"."chart_config_id_seq" OWNED BY "chart_config"."id";
ALTER SEQUENCE "public"."data_table_config_id_seq" OWNED BY "data_table_config"."id";
ALTER SEQUENCE "public"."demo_data_id_seq" OWNED BY "demo_data"."id";
ALTER SEQUENCE "public"."i18n_message_id_seq" OWNED BY "i18n_message"."id";
ALTER SEQUENCE "public"."inspection_check_list_id_seq" OWNED BY "inspection_check_list"."id";
ALTER SEQUENCE "public"."inspection_order_detail_id_seq" OWNED BY "inspection_order_detail"."id";
ALTER SEQUENCE "public"."inspection_order_id_seq" OWNED BY "inspection_order"."id";
ALTER SEQUENCE "public"."institution_id_seq" OWNED BY "site_info"."id";
ALTER SEQUENCE "public"."organization_id_seq" OWNED BY "org_info"."id";
ALTER SEQUENCE "public"."pm_order_id_seq" OWNED BY "pm_order"."id";
ALTER SEQUENCE "public"."supplier_id_seq" OWNED BY "supplier"."id";
ALTER SEQUENCE "public"."sys_role_id_seq" OWNED BY "sys_role"."id";
ALTER SEQUENCE "public"."user_account_id_seq" OWNED BY "user_account"."id";
ALTER SEQUENCE "public"."user_role_id_seq" OWNED BY "user_role"."id";
ALTER SEQUENCE "public"."work_order_history_id_seq" OWNED BY "work_order_history"."id";
ALTER SEQUENCE "public"."work_order_id_seq" OWNED BY "work_order"."id";
ALTER SEQUENCE "public"."work_order_step_detail_id_seq" OWNED BY "work_order_step_detail"."id";
ALTER SEQUENCE "public"."work_order_step_id_seq" OWNED BY "work_order_step"."id";

-- ----------------------------
-- Primary Key structure for table asset_clinical_record
-- ----------------------------
ALTER TABLE "public"."asset_clinical_record" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table asset_file_attachment
-- ----------------------------
ALTER TABLE "public"."asset_file_attachment" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table asset_info
-- ----------------------------
ALTER TABLE "public"."asset_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table chart_config
-- ----------------------------
ALTER TABLE "public"."chart_config" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table data_table_config
-- ----------------------------
ALTER TABLE "public"."data_table_config" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table i18n_message
-- ----------------------------
ALTER TABLE "public"."i18n_message" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table inspection_check_list
-- ----------------------------
ALTER TABLE "public"."inspection_check_list" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table inspection_order
-- ----------------------------
ALTER TABLE "public"."inspection_order" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table inspection_order_detail
-- ----------------------------
ALTER TABLE "public"."inspection_order_detail" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table org_info
-- ----------------------------
ALTER TABLE "public"."org_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table pm_order
-- ----------------------------
ALTER TABLE "public"."pm_order" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table site_info
-- ----------------------------
ALTER TABLE "public"."site_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table sys_role
-- ----------------------------
ALTER TABLE "public"."sys_role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table user_account
-- ----------------------------
ALTER TABLE "public"."user_account" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table user_role
-- ----------------------------
ALTER TABLE "public"."user_role" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Primary Key structure for table work_order
-- ----------------------------
ALTER TABLE "public"."work_order" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."org_info"
-- ----------------------------
ALTER TABLE "public"."org_info" ADD FOREIGN KEY ("site_id") REFERENCES "public"."site_info" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."user_account"
-- ----------------------------
ALTER TABLE "public"."user_account" ADD FOREIGN KEY ("org_id") REFERENCES "public"."org_info" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;

-- ----------------------------
-- Foreign Key structure for table "public"."user_role"
-- ----------------------------
ALTER TABLE "public"."user_role" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_account" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE "public"."user_role" ADD FOREIGN KEY ("role_id") REFERENCES "public"."sys_role" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;
