ALTER TABLE "site_info" ADD CONSTRAINT "uk_site_info_name" UNIQUE ("name");
ALTER TABLE "supplier" ADD CONSTRAINT "uk_supplier_name" UNIQUE (site_id, name);
ALTER TABLE "i18n_message" ADD CONSTRAINT "uk_i18n_message_msg_key" UNIQUE (site_id, msg_type, msg_key);

ALTER TABLE "supplier" ADD column supplier_code varchar(32);

