truncate table tenant_info cascade;
truncate table org_info cascade;
truncate table sys_role cascade;
truncate table user_account cascade;
truncate table user_role cascade;
truncate table chart_config cascade;
truncate table data_table_config cascade;
truncate table field_code_type cascade;


INSERT INTO tenant_info(id,name,name_en,alias_name,site_description,contact_person,contact_phone,contact_email,location,location_en,time_zone,default_lang,is_enabled,wf_auto_step2,wf_auto_step3,wf_auto_step4,wf_auto_step5,wf_auto_step6, manhour_price) VALUES ('1', 'GE', 'GE', 'GE', 'GE', null, null, null, null, null, null, null, true, false,false, false, false,false, 1000);
INSERT INTO tenant_info(id,name,name_en,alias_name,site_description,contact_person,contact_phone,contact_email,location,location_en,time_zone,default_lang,is_enabled,wf_auto_step2,wf_auto_step3,wf_auto_step4,wf_auto_step5,wf_auto_step6, manhour_price) VALUES ('2', 'Demo医联体', 'DemoHospitalGrup', '医联体', '医联体', null, null, null, null, null, null, null, true, false,false, false, false,false, 1000);

INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('1', '1', null, 'Digital Healthcare', 'Digital Healthcare', null);
INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('2', '2', null, '医院本部', 'Hospital Headquarter', null);
INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('3', '2', null, '浦东分院', 'Hospital Pudong branch', null);
INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('4', '2', 2, '放射科', null, '2');
INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('5', '2', 2, '超声室', 'cariology Dept', '2');
INSERT INTO org_info(id,site_id,hospital_id,name,name_en,parent_id) values ('6', '2', 2, '设备科', 'Asset Dept', '2');

INSERT INTO sys_role(id,name,role_desc,home_page) values ('1', 'HospitalHead', '院长', '/homeHead.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('2', 'AssetHead', '设备科主任', '/homeAssetHead.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('3', 'AssetStaff', '设备科科员', '/homeAssetStaff.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('4', 'DeptHead', '科室主任', '/homeDeptHead.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('5', 'ITAdmin', '后台运营管理', '/portal/uaa/userAccount/List.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('6', 'MultiHospital', '多院区管理', '');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('7', 'ClinicalStaff', '临床科室人员', '/homeAssetStaff.xhtml');

INSERT INTO sys_role(id,name,role_desc,home_page) values ('9', 'Guest', '临时报修帐号', '/homeAssetStaff.xhtml');


INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) VALUES ('0', '1', '1', '1', 'ad341656e9bd67a165df', 'guest', 'e4c22e5a7207c3f9', 'd340ff7d7dee42ad341656e9bd67a165dfb67d0d6fb07e4ba856ee12c560ccd9', '90cd3b3d5546809287c0@qq.com', '00000000', 'f', 'f', 'f', 't', 'f', '2017-05-20 13:07:33');
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('1', '1', '1', '1', 'admin', 'administrator', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'apm.admin@ge.com', '', 't', 't', 't', 't', 't', '2016-10-17 10:57:00');
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('2', '2', '2', '2', 'head', '院长', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('3', '2', '2', '6', 'assetHead', '设备科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('4', '2', '2', '4', 'deptHead', '放射科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('5', '2', '2', '6', 'user', '科员', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'asset.staff@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('6', '2', '2', '2', 'HPVP', 'Head of Hospital', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('7', '2', '2', '6', 'EQPHead', 'Head of Equipment Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('8', '2', '2', '4', 'RADHead', 'Head of Radiology Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);

INSERT INTO user_role(id,user_id,role_id) VALUES ('0', '0', '9');
INSERT INTO user_role(id,user_id,role_id) VALUES ('1', '2', '1');
INSERT INTO user_role(id,user_id,role_id) VALUES ('2', '3', '2');
INSERT INTO user_role(id,user_id,role_id) VALUES ('3', '4', '4');
INSERT INTO user_role(id,user_id,role_id) VALUES ('4', '5', '5');
INSERT INTO user_role(id,user_id,role_id) VALUES ('5', '5', '3');
INSERT INTO user_role(id,user_id,role_id) VALUES ('6', '6', '1');
INSERT INTO user_role(id,user_id,role_id) VALUES ('7', '7', '2');
INSERT INTO user_role(id,user_id,role_id) VALUES ('8', '8', '4');


SELECT setval('"tenant_info_id_seq"', 3, false);
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

INSERT INTO public.supplier (id, site_id, name) VALUES (-1, -1, '未知');
update asset_info set supplier_id = -1 where supplier_id is null;

INSERT INTO public.proc_part (id, name) VALUES (1, '头部');
INSERT INTO public.proc_part (id, name) VALUES (2, '颈部');
INSERT INTO public.proc_part (id, name) VALUES (3, '胸部');
INSERT INTO public.proc_part (id, name) VALUES (4, '腹部');
INSERT INTO public.proc_part (id, name) VALUES (5, '盆部');
INSERT INTO public.proc_part (id, name) VALUES (6, '脊柱');
INSERT INTO public.proc_part (id, name) VALUES (7, '上肢');
INSERT INTO public.proc_part (id, name) VALUES (8, '下肢');
INSERT INTO public.proc_part (id, name) VALUES (9, '其它');

INSERT INTO public.proc_step (id, part_id, name) VALUES (1, 1, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (2, 1, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (3, 1, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (4, 1, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (5, 1, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (6, 2, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (7, 2, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (8, 2, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (9, 2, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (10, 2, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (11, 3, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (12, 3, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (13, 3, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (14, 3, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (15, 3, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (16, 4, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (17, 4, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (18, 4, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (19, 4, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (20, 4, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (21, 5, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (22, 5, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (23, 5, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (24, 5, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (25, 5, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (26, 6, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (27, 6, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (28, 6, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (29, 6, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (30, 6, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (31, 7, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (32, 7, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (33, 7, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (34, 7, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (35, 7, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (36, 8, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (37, 8, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (38, 8, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (39, 8, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (40, 8, 'step5');
INSERT INTO public.proc_step (id, part_id, name) VALUES (41, 9, 'step1');
INSERT INTO public.proc_step (id, part_id, name) VALUES (42, 9, 'step2');
INSERT INTO public.proc_step (id, part_id, name) VALUES (43, 9, 'step3');
INSERT INTO public.proc_step (id, part_id, name) VALUES (44, 9, 'step4');
INSERT INTO public.proc_step (id, part_id, name) VALUES (45, 9, 'step5');

INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (8911, 1, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (8956, 2, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9001, 3, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9046, 4, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9091, 5, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9136, 6, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9181, 7, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9226, 8, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9271, 9, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9316, 10, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9361, 11, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9406, 12, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9451, 13, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9496, 14, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9541, 15, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9586, 16, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9631, 17, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9676, 18, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9721, 19, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9766, 20, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9811, 21, 1, null, 1);
INSERT INTO public.proc_map (id, asset_group, part_id, subpart_id, step_id) VALUES (9856, 22, 1, null, 1);

/*insert into supplier(site_id,name) values (1, '通用电气');
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
insert into proc_step(part_id, name) values (9,'else_step5');*/

