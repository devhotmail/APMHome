INSERT INTO "site_info" VALUES (1, 'GE', 'GE', 'GE', 'GE', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

INSERT INTO "org_info" VALUES (1, 1, 'Digital Healthcare', 'Digital Healthcare');

INSERT INTO "sys_role" VALUES (1, 'HospitalHead', '院长', '/home.xhtml');
INSERT INTO "sys_role" VALUES (2, 'AssetHead', '设备科主任', 'home2.xhtml');
INSERT INTO "sys_role" VALUES (3, 'AssetStuff', '设备科成员', 'home2.xhtml');
INSERT INTO "sys_role" VALUES (4, 'DeptHead', '科室主任', 'home3.xhtml');
INSERT INTO "sys_role" VALUES (5, 'DeptStuff', '科室成员', 'home3.xhtml');

INSERT INTO "user_account" VALUES (1, 1, 'admin', 'administrator', '5605ee4b362e9754', 'apm.admin@ge.com', '', 't', 't', 't', 't', 1, '2016-10-17 10:57:00');

INSERT INTO "chart_config" VALUES (1, 'dashboard_pie', 'select * from demo_data where id>=990 ', 't', 'Pie', 'type', 'number1', 'number1', '饼图测试', 'e', NULL, 2, 't', 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 't');
INSERT INTO "chart_config" VALUES (2, 'dashboard_bar', 'select * from demo_data where true :#searchFilter', 't', 'Bar', 'type', 'the_date', 'number3', '柱状图测试', 'e', NULL, 2, 't', 'x Label', '', '', '', '', '', '', 'y Label', '', '', 't');
INSERT INTO "chart_config" VALUES (3, 'dashboard_line', 'select * from demo_data where true :#searchFilter', 't', 'Line', 'type', 'the_date', 'number4', '趋势图', 'e', NULL, 2, 't', 'X', '', '', '', '', '', '', 'YY', '', '', 't');

INSERT INTO "data_table_config" VALUES (1, 'test1', 'select count(*) from chart_config', 'select * from chart_config', 't', 't');
INSERT INTO "data_table_config" VALUES (2, 'test2', 'select count(*) from demo_data where 1=1 :#searchFilter', 'select * from demo_data where 1=1 :#searchFilter', 't', 't');
INSERT INTO "data_table_config" VALUES (3, 'aaa', 'aaa', 'aaa', 'f', 't');


SELECT setval('"public"."site_info_id_seq"', 2, false);
SELECT setval('"public"."org_info_id_seq"', 2, false);
SELECT setval('"public"."sys_role_id_seq"', 6, false);
SELECT setval('"public"."user_account_id_seq"', 2, false);
SELECT setval('"public"."chart_config_id_seq"', 4, false);
SELECT setval('"public"."data_table_config_id_seq"', 4, false);


