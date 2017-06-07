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
INSERT INTO "public"."sys_role" VALUES ('6', 'MultiHospital', '多院区管理', '');

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


INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (2,'caseSubType','故障子类别');
INSERT INTO field_code_type (id,msg_type,msg_type_name) VALUES (3,'assetGroup','设备类型');


SELECT setval('"site_info_id_seq"', 3, false);
SELECT setval('"org_info_id_seq"', 7, false);
SELECT setval('"sys_role_id_seq"', 7, false);
SELECT setval('"user_account_id_seq"', 9, false);
SELECT setval('"user_role_id_seq"', 9, false);
SELECT setval('"chart_config_id_seq"', 4, false);
SELECT setval('"data_table_config_id_seq"', 4, false);
SELECT setval('"field_code_type_id_seq"', 5, false);


--create index ix_exam_summit_site_id on exam_summit(site_id);
--create index ix_exam_summit_hospital_id on exam_summit(hospital_id);
--create index ix_exam_summit_asset_id on exam_summit(asset_id);
--create index ix_exam_summit_asset_group on exam_summit(asset_group);
--create index ix_exam_summit_dept_id on exam_summit(dept_id);
--create index ix_exam_summit_part_id on exam_summit(part_id);
--create index ix_exam_summit_subpart_id on exam_summit(subpart_id);
--create index ix_exam_summit_step_id on exam_summit(step_id);

insert into supplier(site_id,name) values (1, '通用电气');
insert into supplier(site_id,name) values (1, '西门子');
insert into supplier(site_id,name) values (1, '飞利浦');
insert into supplier(site_id,name) values (1, '霍尼韦尔');

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

