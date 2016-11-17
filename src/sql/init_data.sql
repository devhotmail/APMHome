INSERT INTO "site_info" VALUES ('1', 'GE', 'GE', 'GE', 'GE', null, null, null, null, null, null, null, null);
INSERT INTO "site_info" VALUES ('2', 'Demo医联体', 'DemoHospitalGrup', '医联体', '医联体', null, null, null, null, null, null, null, null);

INSERT INTO "org_info" VALUES ('1', '1', null, 'Digital Healthcare', 'Digital Healthcare', null);
INSERT INTO "org_info" VALUES ('2', '2', null, '医院本部', 'Hospital Headquarter', null);
INSERT INTO "org_info" VALUES ('3', '2', null, '浦东分院', 'Hospital Pudong branch', null);
INSERT INTO "org_info" VALUES ('4', '2', 2, '放射科', null, '2');
INSERT INTO "org_info" VALUES ('5', '2', 2, '超声室', 'cariology Dept', '2');
INSERT INTO "org_info" VALUES ('6', '2', 2, '设备科', 'Asset Dept', '2');


INSERT INTO "public"."sys_role" VALUES ('1', 'HospitalHead', '院长', '/homeHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('2', 'AssetHead', '设备科主任', '/homeAssetHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('3', 'AssetStuff', '设备科科员', '/homeAssetStuff.xhtml');
INSERT INTO "public"."sys_role" VALUES ('4', 'DeptHead', '科室主任', '/homeDeptHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('5', 'Guest', '一般用户', '/home.xhtml');

INSERT INTO "public"."user_account" VALUES ('1', '1', '1', 'admin', 'administrator', '5605ee4b362e9754', 'apm.admin@ge.com', '', 't', 't', 't', 't', 't', '1', '2016-10-17 10:57:00');
INSERT INTO "public"."user_account" VALUES ('2', '2', '2', 'head', '院长', '5605ee4b362e9754', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', '2', null);
INSERT INTO "public"."user_account" VALUES ('3', '6', '2', 'assetHead', '设备科主任', '5605ee4b362e9754', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', '2', null);
INSERT INTO "public"."user_account" VALUES ('4', '4', '2', 'deptHead', '放射科主任', '5605ee4b362e9754', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', '2', null);
INSERT INTO "public"."user_account" VALUES ('5', '6', '2', 'user', '科员', '5605ee4b362e9754', 'asset.staff@a.com', null, 'f', 'f', 'f', 't', 'f', '2', null);

INSERT INTO "public"."user_role" VALUES ('1', '2', '1');
INSERT INTO "public"."user_role" VALUES ('2', '3', '2');
INSERT INTO "public"."user_role" VALUES ('3', '4', '4');
INSERT INTO "public"."user_role" VALUES ('4', '5', '5');


INSERT INTO "chart_config" VALUES (1, 'dashboard_pie', 'select * from demo_data where id>=990 ', 't', 'pie', 'type', 'number1', 'number1', '饼图测试', 'e', NULL, 2, 't', 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 't');
INSERT INTO "chart_config" VALUES (2, 'dashboard_bar', 'select * from demo_data where true :#searchFilter', 't', 'bar', 'type', 'the_date', 'number3', '柱状图测试', 'e', NULL, 2, 't', 'x Label', '', '', '', '', '', '', 'y Label', '', '', 't');
INSERT INTO "chart_config" VALUES (3, 'dashboard_line', 'select * from demo_data where true :#searchFilter', 't', 'line', 'type', 'the_date', 'number4', '趋势图', 'e', NULL, 2, 't', 'X', '', '', '', '', '', '', 'YY', '', '', 'f');

INSERT INTO "data_table_config" VALUES (1, 'test1', 'select count(*) from chart_config', 'select * from chart_config', 't', 't');
INSERT INTO "data_table_config" VALUES (2, 'test2', 'select count(*) from demo_data where 1=1 :#searchFilter', 'select * from demo_data where 1=1 :#searchFilter', 't', 't');
INSERT INTO "data_table_config" VALUES (3, 'aaa', 'aaa', 'aaa', 'f', 't');


INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (1,'casePriority','故障紧急程度');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (2,'caseType','故障类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (3,'caseSubType','故障子类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (4,'assetGroup','资产分组');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (5,'woSteps','维修工单步骤');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (6,'assetStatus','资产状态');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (7,'assetFunctionType','资产功能类别');


SELECT setval('"site_info_id_seq"', 3, false);
SELECT setval('"org_info_id_seq"', 7, false);
SELECT setval('"sys_role_id_seq"', 6, false);
SELECT setval('"user_account_id_seq"', 6, false);
SELECT setval('"user_role_id_seq"', 5, false);
SELECT setval('"chart_config_id_seq"', 4, false);
SELECT setval('"data_table_config_id_seq"', 4, false);
SELECT setval('"field_code_type_id_seq"', 4, false);


