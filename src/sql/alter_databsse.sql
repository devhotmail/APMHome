ALTER TABLE "site_info" DROP CONSTRAINT "uk_site_info_name";
ALTER TABLE "site_info" ADD CONSTRAINT "uk_site_info_name" UNIQUE ("name");

ALTER TABLE "supplier" DROP CONSTRAINT "uk_supplier_name";
ALTER TABLE "supplier" ADD CONSTRAINT "uk_supplier_name" UNIQUE (site_id, name);

ALTER TABLE "i18n_message" DROP CONSTRAINT "uk_i18n_message_msg_key";
ALTER TABLE "i18n_message" ADD CONSTRAINT "uk_i18n_message_msg_key" UNIQUE (site_id, msg_type, msg_key);

ALTER TABLE "supplier" ADD column supplier_code varchar(32);

DROP TABLE IF EXISTS i18n_message CASCADE;
CREATE TABLE procedure_name_maping (
"id" serial NOT NULL,
"ris_procedure_name" varchar(256) COLLATE "default" NOT NULL,
"apm_procedure_name" varchar(256) COLLATE "default" NOT NULL,
"apm_procedure_id" int NOT NULL
);

ALTER TABLE procedure_name_maping ADD PRIMARY KEY ("id");
