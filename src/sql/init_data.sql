truncate table site_info cascade;
truncate table org_info cascade;
truncate table sys_role cascade;
truncate table user_account cascade;
truncate table user_role cascade;
truncate table chart_config cascade;
truncate table data_table_config cascade;
truncate table field_code_type cascade;


INSERT INTO "site_info" VALUES ('1', 'GE', 'GE', 'GE', 'GE', null, null, null, null, null, null, null, true, false,false, false, false,false);
INSERT INTO "site_info" VALUES ('2', 'Demo医联体', 'DemoHospitalGrup', '医联体', '医联体', null, null, null, null, null, null, null, true, false,false, false, false,false);

INSERT INTO "org_info" VALUES ('1', '1', null, 'Digital Healthcare', 'Digital Healthcare', null);
INSERT INTO "org_info" VALUES ('2', '2', null, '医院本部', 'Hospital Headquarter', null);
INSERT INTO "org_info" VALUES ('3', '2', null, '浦东分院', 'Hospital Pudong branch', null);
INSERT INTO "org_info" VALUES ('4', '2', 2, '放射科', null, '2');
INSERT INTO "org_info" VALUES ('5', '2', 2, '超声室', 'cariology Dept', '2');
INSERT INTO "org_info" VALUES ('6', '2', 2, '设备科', 'Asset Dept', '2');


INSERT INTO "public"."sys_role" VALUES ('1', 'HospitalHead', '院长', '/homeHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('2', 'AssetHead', '设备科主任', '/homeAssetHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('3', 'AssetStaff', '设备科科员', '/homeAssetStaff.xhtml');
INSERT INTO "public"."sys_role" VALUES ('4', 'DeptHead', '科室主任', '/homeDeptHead.xhtml');
INSERT INTO "public"."sys_role" VALUES ('5', 'ITAdmin', 'IT管理员', '/portal/uaa/userAccount/List.xhtml');

INSERT INTO user_account VALUES ('1', '1', '1', '1', 'admin', 'administrator', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'apm.admin@ge.com', '', 't', 't', 't', 't', 't', '2016-10-17 10:57:00');
INSERT INTO user_account VALUES ('2', '2', '2', '2', 'head', '院长', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('3', '2', '2', '6', 'assetHead', '设备科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('4', '2', '2', '4', 'deptHead', '放射科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('5', '2', '2', '6', 'user', '科员', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'asset.staff@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('6', '2', '2', '2', 'HPVP', 'Head of Hospital', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('7', '2', '2', '6', 'EQPHead', 'Head of Equipment Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account VALUES ('8', '2', '2', '4', 'RADHead', 'Head of Radiology Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);

INSERT INTO "public"."user_role" VALUES ('1', '2', '1');
INSERT INTO "public"."user_role" VALUES ('2', '3', '2');
INSERT INTO "public"."user_role" VALUES ('3', '4', '4');
INSERT INTO "public"."user_role" VALUES ('4', '5', '5');
INSERT INTO "public"."user_role" VALUES ('5', '5', '3');
INSERT INTO "public"."user_role" VALUES ('6', '6', '1');
INSERT INTO "public"."user_role" VALUES ('7', '7', '2');
INSERT INTO "public"."user_role" VALUES ('8', '8', '4');


INSERT INTO "chart_config" VALUES (1, 'dashboard_pie', 'select * from demo_data where id>=990 ', 't', 'pie', 'type', 'number1', 'number1', '饼图测试', 'e', NULL, 2, 't', 'x Label', NULL, NULL, NULL, NULL, NULL, NULL, 'y Label', NULL, NULL, 't');
INSERT INTO "chart_config" VALUES (2, 'dashboard_bar', 'select * from demo_data where true :#searchFilter', 't', 'bar', 'type', 'the_date', 'number3', '柱状图测试', 'e', NULL, 2, 't', 'x Label', '', '', '', '', '', '', 'y Label', '', '', 't');
INSERT INTO "chart_config" VALUES (3, 'dashboard_line', 'select * from demo_data where true :#searchFilter', 't', 'line', 'type', 'the_date', 'number4', '趋势图', 'e', NULL, 2, 't', 'X', '', '', '', '', '', '', 'YY', '', '', 'f');

INSERT INTO "data_table_config" VALUES (1, 'test1', 'select count(*) from chart_config', 'select * from chart_config', 't', 't');
INSERT INTO "data_table_config" VALUES (2, 'test2', 'select count(*) from demo_data where 1=1 :#searchFilter', 'select * from demo_data where 1=1 :#searchFilter', 't', 't');
INSERT INTO "data_table_config" VALUES (3, 'aaa', 'aaa', 'aaa', 'f', 't');


INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (1,'caseType','故障类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (2,'caseSubType','故障子类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (3,'assetGroup','资产分组');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (4,'ownAssetGroup','所属设备类型');


SELECT setval('"site_info_id_seq"', 3, false);
SELECT setval('"org_info_id_seq"', 7, false);
SELECT setval('"sys_role_id_seq"', 6, false);
SELECT setval('"user_account_id_seq"', 9, false);
SELECT setval('"user_role_id_seq"', 9, false);
SELECT setval('"chart_config_id_seq"', 4, false);
SELECT setval('"data_table_config_id_seq"', 4, false);
SELECT setval('"field_code_type_id_seq"', 5, false);


