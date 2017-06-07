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

