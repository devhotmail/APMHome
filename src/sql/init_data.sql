INSERT INTO "site_info" VALUES (1, 'GE', 'GE', 'GE', 'GE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO "org_info" VALUES (1, 1, 'Digital Health', 'Digital Health');

INSERT INTO "sys_role" VALUES (1, 'HospitalHead', '院长');
INSERT INTO "sys_role" VALUES (2, 'DeptHead', '主任');
INSERT INTO "sys_role" VALUES (3, 'DeviceHead', '设备科');

INSERT INTO "user_account" VALUES (1, 1, 'admin', 'administrator', '5605ee4b362e9754', 'aps.admin@ge.com', '', 't', 't', 't', 't', 1, '2016-10-17 10:57:00');

INSERT INTO "chart_config" VALUES (1, 'dashboard_pie', 'select * from demo_data where id>=990 ', 't', 'Pie', 'type', 'number1', 'number1', '饼图测试', 'e', NULL, 2, 't', 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 't');
INSERT INTO "chart_config" VALUES (2, 'dashboard_bar', 'select * from demo_data where true :#searchFilter', 't', 'Bar', 'type', 'the_date', 'number3', '柱状图测试', 'e', NULL, 2, 't', 'x Label', '', '', '', '', '', '', 'y Label', '', '', 't');
INSERT INTO "chart_config" VALUES (3, 'dashboard_line', 'select * from demo_data where true :#searchFilter', 't', 'Line', 'type', 'the_date', 'number4', '趋势图', 'e', NULL, 2, 't', 'X', '', '', '', '', '', '', 'YY', '', '', 't');

INSERT INTO "data_table_config" VALUES (4, 'test1', 'select count(*) from chart_config', 'select * from chart_config', 't', 't');
INSERT INTO "data_table_config" VALUES (5, 'test2', 'select count(*) from demo_data where 1=1 :#searchFilter', 'select * from demo_data where 1=1 :#searchFilter', 't', 't');
INSERT INTO "data_table_config" VALUES (6, 'aaa', 'aaa', 'aaa', 'f', 't');


INSERT INTO "public"."asset_info"("id", "name", "hospital_id", "department_id", "owner_org_id", "owner_user_id", "site_id") VALUES(1, 'GE OPTIMA CT', 1, 1, 1, 1, 1);


INSERT INTO "public"."asset_clinical_record"("id", "site_id", "asset_id", "modality_id", "modality_type", "procedure_id", "procedure_name", "price_amount", "inject_count", "expose_count", "film_count", "exam_date", "exam_start_time", "exam_end_time") VALUES(1, 1, 1, 1, 1, 1, '1', 300, 5, 89, 4, '2016-09-20', '08:10:23', '10:01:56');