truncate table site_info cascade;
truncate table org_info cascade;
truncate table sys_role cascade;
truncate table user_account cascade;
truncate table user_role cascade;
truncate table chart_config cascade;
truncate table data_table_config cascade;
truncate table field_code_type cascade;


INSERT INTO site_info(id,name,name_en,alias_name,site_description,contact_person,contact_phone,contact_email,location,location_en,time_zone,default_lang,is_enabled,wf_auto_step2,wf_auto_step3,wf_auto_step4,wf_auto_step5,wf_auto_step6) VALUES ('1', 'GE', 'GE', 'GE', 'GE', null, null, null, null, null, null, null, true, false,false, false, false,false);
INSERT INTO site_info(id,name,name_en,alias_name,site_description,contact_person,contact_phone,contact_email,location,location_en,time_zone,default_lang,is_enabled,wf_auto_step2,wf_auto_step3,wf_auto_step4,wf_auto_step5,wf_auto_step6) VALUES ('2', 'Demo医联体', 'DemoHospitalGrup', '医联体', '医联体', null, null, null, null, null, null, null, true, false,false, false, false,false);

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
INSERT INTO sys_role(id,name,role_desc,home_page) values ('5', 'ITAdmin', 'IT管理员', '/portal/uaa/userAccount/List.xhtml');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('6', 'MultiHospital', '多院区管理', '');
INSERT INTO sys_role(id,name,role_desc,home_page) values ('7', 'ClinicalStaff', '临床科室人员', '/homeAssetStaff.xhtml');


INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('1', '1', '1', '1', 'admin', 'administrator', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'apm.admin@ge.com', '', 't', 't', 't', 't', 't', '2016-10-17 10:57:00');
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('2', '2', '2', '2', 'head', '院长', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('3', '2', '2', '6', 'assetHead', '设备科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('4', '2', '2', '4', 'deptHead', '放射科主任', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('5', '2', '2', '6', 'user', '科员', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'asset.staff@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('6', '2', '2', '2', 'HPVP', 'Head of Hospital', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'head@a.com', null, 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('7', '2', '2', '6', 'EQPHead', 'Head of Equipment Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'assethead@a.com', '', 'f', 'f', 'f', 't', 'f', null);
INSERT INTO user_account(id,site_id,hospital_id,org_id,login_name,name,pwd_salt,password,email,telephone,is_super_admin,is_site_admin,is_local_admin,is_active,is_online,last_login_time) values ('8', '2', '2', '4', 'RADHead', 'Head of Radiology Dept', 'b380e7bb58d700d5','092561820bad0790cd3b3d5546809287c0cb17e67e6b8dd28665b292560298da', 'radiologyHead@a.com', null, 'f', 'f', 'f', 't', 'f', null);

INSERT INTO user_role(id,user_id,role_id) VALUES ('1', '2', '1');
INSERT INTO user_role(id,user_id,role_id) VALUES ('2', '3', '2');
INSERT INTO user_role(id,user_id,role_id) VALUES ('3', '4', '4');
INSERT INTO user_role(id,user_id,role_id) VALUES ('4', '5', '5');
INSERT INTO user_role(id,user_id,role_id) VALUES ('5', '5', '3');
INSERT INTO user_role(id,user_id,role_id) VALUES ('6', '6', '1');
INSERT INTO user_role(id,user_id,role_id) VALUES ('7', '7', '2');
INSERT INTO user_role(id,user_id,role_id) VALUES ('8', '8', '4');


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


