/*
Navicat PGSQL Data Transfer

Source Server         : localhost_5432
Source Server Version : 90601
Source Host           : localhost:5432
Source Database       : ge_apm
Source Schema         : public

Target Server Type    : PGSQL
Target Server Version : 90601
File Encoding         : 65001

Date: 2017-04-25 16:12:47
*/


-- ----------------------------
-- Table structure for asset_info
-- ----------------------------
DROP TABLE IF EXISTS "public"."asset_info";
CREATE TABLE "public"."asset_info" (
"id" int4 DEFAULT nextval('asset_info_id_seq'::regclass) NOT NULL,
"site_id" int4 NOT NULL,
"name" varchar(64) COLLATE "default" NOT NULL,
"alias_name" varchar(64) COLLATE "default",
"function_group" int4,
"function_type" varchar(64) COLLATE "default",
"function_grade" int4,
"manufacture" varchar(64) COLLATE "default",
"vendor" varchar(64) COLLATE "default",
"maitanance" varchar(64) COLLATE "default",
"maitanance_tel" varchar(32) COLLATE "default",
"serial_num" varchar(64) COLLATE "default",
"depart_num" varchar(64) COLLATE "default",
"financing_num" varchar(64) COLLATE "default",
"barcode" varchar(64) COLLATE "default",
"modality_id" varchar(64) COLLATE "default",
"location_code" varchar(64) COLLATE "default",
"location_name" varchar(64) COLLATE "default",
"hospital_id" int4 NOT NULL,
"clinical_dept_id" int4,
"clinical_dept_name" varchar(64) COLLATE "default",
"asset_group" int4,
"asset_dept_id" int4 NOT NULL,
"asset_owner_id" int4 NOT NULL,
"asset_owner_name" varchar(16) COLLATE "default" NOT NULL,
"asset_owner_tel" varchar(16) COLLATE "default",
"is_valid" bool NOT NULL,
"status" int4 NOT NULL,
"manufact_date" date,
"purchase_date" date,
"arrive_date" date,
"install_date" date,
"warranty_date" date,
"terminate_date" date,
"last_pm_date" date,
"last_metering_date" date,
"last_qa_date" date,
"last_stocktake_date" date,
"purchase_price" float8,
"salvage_value" float8,
"lifecycle" int4,
"depreciation_method" int4,
"clinical_owner_id" int4,
"clinical_owner_name" varchar(16) COLLATE "default",
"clinical_owner_tel" varchar(16) COLLATE "default",
"registration_no" varchar(64) COLLATE "default",
"factory_warranty_date" date,
"supplier_id" int4,
"qr_code" varchar(256) COLLATE "default"
)
WITH (OIDS=FALSE)

;

-- ----------------------------
-- Records of asset_info
-- ----------------------------
INSERT INTO "public"."asset_info" VALUES ('1', '1', 'CT-1', null, null, '7', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '1', '3', null, '1', '100', '3', 'asset-owner-3', null, 't', '1', null, null, null, '2013-05-17', '2020-09-12', null, '2013-10-23', '2016-05-28', '2013-03-31', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('2', '2', 'MR-2', null, null, '3', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '2', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-05-03', '2018-05-05', null, '2013-11-27', '2013-08-24', '2016-02-10', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('3', '2', 'DR-3', null, null, '5', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '3', '100', '5', '科员', null, 't', '2', null, null, null, '2013-02-18', '2018-05-25', null, '2016-05-14', '2013-11-10', '2015-10-16', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('4', '2', 'NM-4', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '9', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-05-20', '2019-05-20', null, '2014-11-08', '2016-06-26', '2013-12-23', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('5', '2', 'DR-5', null, null, '3', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '3', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-04-27', '2017-06-14', null, '2015-11-21', '2014-01-15', '2013-07-23', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('6', '2', '乳腺机-6', null, null, '2', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '7', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-05-18', '2017-08-07', null, '2016-03-15', '2015-04-25', '2014-08-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('7', '1', 'MR-7', null, null, '4', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '1', '2', null, '2', '100', '3', 'asset-owner-3', null, 't', '1', null, null, null, '2013-04-29', '2018-11-24', null, '2016-06-04', '2015-12-21', '2013-08-08', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('8', '1', 'DSA-8', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '6', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-05-14', '2019-02-18', null, '2013-04-14', '2015-10-26', '2013-05-15', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('9', '2', 'DR-9', null, null, '6', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '3', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-01-15', '2018-10-07', null, '2015-07-29', '2015-01-23', '2015-07-14', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('10', '1', 'DSA-10', null, null, '8', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '1', '2', null, '6', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-05-10', '2020-09-25', null, '2016-11-28', '2013-05-06', '2015-06-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('11', '2', 'DR-11', null, null, '2', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '3', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-03-27', '2018-07-01', null, '2015-12-14', '2013-07-31', '2016-03-15', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('12', '2', 'RF-12', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '5', '100', '5', '科员', null, 't', '2', null, null, null, '2013-02-23', '2018-04-20', null, '2015-09-06', '2014-01-10', '2013-02-25', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('13', '2', 'PET-CT-13', null, null, '7', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '2', '5', null, '10', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-01-04', '2018-05-22', null, '2014-03-22', '2016-01-28', '2015-11-19', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('14', '2', 'DSA-14', null, null, '6', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '6', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-02-06', '2019-03-10', null, '2015-09-08', '2015-10-02', '2015-03-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('15', '2', 'CR-15', null, null, '10', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '1', '5', null, '4', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-02-06', '2019-05-20', null, '2016-09-12', '2016-07-16', '2013-02-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('16', '2', 'CR-16', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '4', '100', '3', 'asset-owner-3', null, 't', '1', null, null, null, '2013-03-26', '2018-11-02', null, '2016-12-03', '2016-12-03', '2014-07-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('17', '2', 'NM-17', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '9', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-04-01', '2019-06-02', null, '2015-03-14', '2014-05-20', '2013-05-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('18', '2', 'DR-18', null, null, '10', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '3', '100', '5', '科员', null, 't', '3', null, null, null, '2013-04-19', '2017-09-29', null, '2013-06-08', '2016-06-25', '2014-11-06', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('19', '2', 'CR-19', null, null, '3', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '3', '2', null, '4', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-03-26', '2018-01-12', null, '2014-07-24', '2016-03-06', '2015-05-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('20', '2', 'RF-20', null, null, '1', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '5', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-01-15', '2018-07-02', null, '2013-02-22', '2016-09-03', '2016-02-18', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('21', '2', 'NM-21', null, null, '2', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '9', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-01-26', '2017-04-11', null, '2014-04-30', '2014-09-09', '2013-03-12', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('22', '2', 'PET-CT-22', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '10', '100', '5', '科员', null, 't', '3', null, null, null, '2013-05-08', '2019-10-04', null, '2014-07-23', '2016-04-14', '2015-07-08', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('23', '2', 'DSA-23', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '6', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-05-07', '2019-01-06', null, '2013-07-03', '2016-08-25', '2016-11-21', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('24', '2', 'MR-24', null, null, '6', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '2', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-04-30', '2019-03-09', null, '2013-06-08', '2016-04-03', '2013-02-12', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('25', '2', 'US-25', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '12', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-03-20', '2019-06-16', null, '2014-03-23', '2014-08-21', '2013-03-10', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('26', '2', 'PET-CT-26', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '10', '100', '3', 'asset-owner-3', null, 't', '2', null, null, null, '2013-05-10', '2019-09-12', null, '2015-03-26', '2015-10-13', '2015-08-16', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('27', '2', '乳腺机-27', null, null, '1', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '2', '5', null, '7', '100', '4', 'asset-owner-4', null, 't', '1', null, null, null, '2013-02-22', '2018-07-15', null, '2014-09-12', '2015-01-07', '2013-07-23', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('28', '1', 'RF-28', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '5', '100', '3', 'asset-owner-3', null, 't', '2', null, null, null, '2013-03-15', '2017-11-09', null, '2016-09-05', '2015-06-20', '2015-08-12', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('29', '2', 'PET-29', null, null, '10', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '8', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-01-29', '2019-08-15', null, '2013-01-10', '2016-10-12', '2015-04-05', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('30', '1', 'NM-30', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '9', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-04-27', '2019-05-09', null, '2015-11-11', '2014-06-21', '2015-05-31', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('31', '1', 'CT-31', null, null, '1', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '1', '3', null, '1', '100', '4', 'asset-owner-4', null, 't', '3', null, null, null, '2013-05-12', '2019-12-30', null, '2014-10-01', '2013-01-24', '2013-12-06', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('32', '2', 'CR-32', null, null, '8', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '4', '100', '2', 'asset-owner-2', null, 't', '3', null, null, null, '2013-03-20', '2018-09-28', null, '2016-09-23', '2016-06-02', '2016-08-22', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('33', '2', 'CT-33', null, null, '1', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '1', '2', null, '1', '100', '5', '科员', null, 't', '3', null, null, null, '2013-03-31', '2018-05-16', null, '2014-05-10', '2015-11-24', '2014-10-18', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('34', '2', 'DSA-34', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '6', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-03-16', '2017-07-12', null, '2015-01-16', '2015-03-29', '2016-11-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('35', '2', 'PET-35', null, null, '7', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '3', '1', null, '8', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-04-19', '2019-10-08', null, '2013-01-16', '2013-05-12', '2014-10-08', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('36', '2', 'PET-36', null, null, '6', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '8', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-04-16', '2018-08-25', null, '2014-12-24', '2015-03-31', '2016-02-27', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('37', '2', 'RF-37', null, null, '10', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '2', '5', null, '5', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-01-29', '2020-01-26', null, '2013-12-10', '2016-05-07', '2014-12-30', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('38', '1', 'MR-38', null, null, '1', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '1', '5', null, '2', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-04-16', '2019-11-08', null, '2015-03-22', '2016-02-14', '2015-12-23', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('39', '1', 'PET-MR-39', null, null, '8', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '11', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-03-05', '2019-07-14', null, '2013-10-25', '2016-03-14', '2013-07-25', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('40', '2', 'PET-40', null, null, '4', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '2', '3', null, '8', '100', '3', 'asset-owner-3', null, 't', '1', null, null, null, '2013-04-16', '2017-07-14', null, '2014-05-17', '2016-07-05', '2013-07-19', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('41', '2', 'US-41', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '12', '100', '3', 'asset-owner-3', null, 't', '1', null, null, null, '2013-03-10', '2019-02-23', null, '2015-12-09', '2015-04-30', '2014-12-17', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('42', '1', 'RF-42', null, null, '8', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '5', '100', '4', 'asset-owner-4', null, 't', '3', null, null, null, '2013-01-08', '2019-08-13', null, '2013-12-31', '2013-02-23', '2013-08-20', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('43', '2', 'NM-43', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '9', '100', '4', 'asset-owner-4', null, 't', '3', null, null, null, '2013-04-16', '2017-04-18', null, '2014-02-21', '2014-03-26', '2014-11-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('44', '1', 'PET-MR-44', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '11', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-01-28', '2018-10-04', null, '2016-04-27', '2015-11-19', '2014-04-07', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('45', '2', 'PET-45', null, null, '9', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '8', '100', '5', '科员', null, 't', '1', null, null, null, '2013-05-13', '2020-02-03', null, '2016-07-24', '2014-04-09', '2013-10-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('46', '2', 'MR-46', null, null, '5', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '2', '3', null, '2', '100', '5', '科员', null, 't', '3', null, null, null, '2013-01-13', '2020-03-17', null, '2014-03-09', '2016-04-13', '2013-02-21', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('47', '1', 'MR-47', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '2', '100', '4', 'asset-owner-4', null, 't', '1', null, null, null, '2013-03-07', '2019-02-05', null, '2013-07-16', '2016-08-05', '2015-12-28', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('48', '2', 'RF-48', null, null, '2', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '2', '1', null, '5', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-03-19', '2019-06-20', null, '2015-10-01', '2016-02-09', '2014-10-31', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('49', '2', 'RF-49', null, null, '3', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '2', '1', null, '5', '100', '4', 'asset-owner-4', null, 't', '1', null, null, null, '2013-03-27', '2017-06-22', null, '2014-09-11', '2015-03-22', '2016-10-31', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('50', '1', 'NM-50', null, null, '1', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '3', '5', null, '9', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-05-05', '2017-09-11', null, '2013-10-17', '2015-02-12', '2014-03-19', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('51', '2', 'PET-CT-51', null, null, '6', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '10', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-02-14', '2017-03-28', null, '2015-05-09', '2014-03-27', '2013-05-31', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('52', '2', 'MR-52', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '2', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-01-12', '2020-06-25', null, '2015-03-13', '2013-06-30', '2013-10-15', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('53', '2', 'US-53', null, null, '6', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '2', '5', null, '12', '100', '5', '科员', null, 't', '2', null, null, null, '2013-03-25', '2017-12-08', null, '2016-05-19', '2015-06-09', '2016-01-01', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('54', '1', 'MR-54', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '2', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-05-02', '2017-05-04', null, '2014-05-28', '2014-03-19', '2016-10-29', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('55', '2', 'RF-55', null, null, '10', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '5', '100', '5', '科员', null, 't', '2', null, null, null, '2013-04-28', '2019-09-28', null, '2015-12-17', '2015-01-13', '2016-03-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('56', '2', 'PET-56', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '8', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-01-21', '2020-04-01', null, '2015-02-03', '2016-05-15', '2013-09-07', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('57', '2', 'CR-57', null, null, '8', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '4', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-04-14', '2017-04-27', null, '2013-11-08', '2016-06-27', '2013-08-21', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('58', '1', 'NM-58', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '9', '100', '3', 'asset-owner-3', null, 't', '3', null, null, null, '2013-04-09', '2018-05-15', null, '2013-04-22', '2014-06-09', '2016-05-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('59', '2', 'PET-MR-59', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '11', '100', '3', 'asset-owner-3', null, 't', '3', null, null, null, '2013-04-21', '2018-02-01', null, '2016-02-15', '2015-10-27', '2013-01-19', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('60', '2', 'PET-60', null, null, '8', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '2', '3', null, '8', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-02-18', '2020-10-27', null, '2015-07-30', '2013-03-24', '2013-02-08', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('61', '1', 'DSA-61', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '6', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-02-22', '2020-05-01', null, '2014-01-03', '2016-06-24', '2013-10-30', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('62', '2', 'DSA-62', null, null, '8', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '6', '100', '5', '科员', null, 't', '3', null, null, null, '2013-05-09', '2017-06-13', null, '2016-10-20', '2015-05-07', '2016-01-03', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('63', '2', 'CT-63', null, null, '9', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '1', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-01-08', '2019-09-22', null, '2015-09-15', '2016-09-26', '2016-02-10', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('64', '1', 'MR-64', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '2', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-03-30', '2017-12-20', null, '2016-05-28', '2016-07-01', '2014-05-07', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('65', '1', 'CR-65', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '4', '100', '4', 'asset-owner-4', null, 't', '1', null, null, null, '2013-02-03', '2018-04-12', null, '2014-05-27', '2013-08-25', '2016-11-18', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('66', '2', 'PET-CT-66', null, null, '7', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '2', '5', null, '10', '100', '3', 'asset-owner-3', null, 't', '3', null, null, null, '2013-04-12', '2020-05-16', null, '2016-04-06', '2015-03-30', '2013-06-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('67', '2', 'RF-67', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '5', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-05-19', '2020-10-07', null, '2013-05-20', '2015-11-21', '2015-02-12', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('68', '2', 'RF-68', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '5', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-01-22', '2017-04-04', null, '2015-08-05', '2016-09-17', '2016-08-25', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('69', '2', 'PET-MR-69', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '11', '100', '2', 'asset-owner-2', null, 't', '3', null, null, null, '2013-04-20', '2020-10-04', null, '2015-09-20', '2015-05-12', '2016-08-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('70', '2', 'DSA-70', null, null, '3', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '2', '1', null, '6', '100', '5', '科员', null, 't', '2', null, null, null, '2013-01-19', '2018-10-21', null, '2015-11-07', '2014-12-18', '2013-06-28', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('71', '2', 'DSA-71', null, null, '2', null, null, null, null, null, null, null, null, null, null, '5', '五楼', '3', '5', null, '6', '100', '4', 'asset-owner-4', null, 't', '1', null, null, null, '2013-01-10', '2020-01-24', null, '2016-04-16', '2014-07-07', '2013-03-03', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('72', '1', 'RF-72', null, null, '10', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '5', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-04-20', '2018-05-28', null, '2014-07-11', '2015-03-14', '2016-11-05', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('73', '2', 'US-73', null, null, '6', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '12', '100', '2', 'asset-owner-2', null, 't', '1', null, null, null, '2013-01-30', '2017-12-22', null, '2015-11-10', '2013-09-24', '2015-03-07', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('74', '2', 'CT-74', null, null, '3', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '1', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-05-16', '2018-08-29', null, '2013-04-25', '2015-07-02', '2014-11-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('75', '2', 'PET-75', null, null, '5', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '8', '100', '5', '科员', null, 't', '1', null, null, null, '2013-05-16', '2019-05-26', null, '2015-11-02', '2015-09-30', '2014-10-24', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('76', '1', 'PET-CT-76', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '10', '100', '2', 'asset-owner-2', null, 't', '3', null, null, null, '2013-03-31', '2018-09-22', null, '2014-12-24', '2016-11-13', '2013-06-20', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('77', '2', 'CT-77', null, null, '7', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '2', '3', null, '1', '100', '4', 'asset-owner-4', null, 't', '3', null, null, null, '2013-02-18', '2018-05-04', null, '2015-07-07', '2016-04-04', '2014-03-06', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('78', '2', 'NM-78', null, null, '4', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '1', '1', null, '9', '100', '1', 'asset-owner-1', null, 't', '3', null, null, null, '2013-02-08', '2017-10-16', null, '2016-03-20', '2013-05-04', '2016-05-11', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('79', '1', 'DSA-79', null, null, '4', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '6', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-04-11', '2016-12-28', null, '2014-09-10', '2016-10-09', '2014-07-17', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('80', '1', 'PET-80', null, null, '2', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '8', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-01-22', '2020-01-22', null, '2015-08-17', '2015-11-25', '2014-06-18', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('81', '1', 'PET-MR-81', null, null, '2', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '11', '100', '2', 'asset-owner-2', null, 't', '3', null, null, null, '2013-03-11', '2018-06-26', null, '2015-10-10', '2014-05-15', '2013-01-11', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('82', '2', 'US-82', null, null, '5', null, null, null, null, null, null, null, null, null, null, '3', '三楼', '3', '3', null, '12', '100', '4', 'asset-owner-4', null, 't', '2', null, null, null, '2013-02-28', '2017-09-28', null, '2013-01-04', '2015-07-24', '2014-10-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('83', '2', 'MR-83', null, null, '5', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '2', '1', null, '2', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-04-12', '2017-12-23', null, '2014-12-05', '2013-10-16', '2013-09-02', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('84', '2', 'DR-84', null, null, '3', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '2', '2', null, '3', '100', '1', 'asset-owner-1', null, 't', '2', null, null, null, '2013-03-03', '2019-11-21', null, '2014-08-21', '2016-06-28', '2014-09-13', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('85', '2', 'US-85', null, null, '7', null, null, null, null, null, null, null, null, null, null, '1', '一楼', '2', '1', null, '12', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-05-12', '2018-11-05', null, '2016-02-12', '2014-11-12', '2015-05-10', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('86', '2', 'DSA-86', null, null, '1', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '6', '100', '4', 'asset-owner-4', null, 't', '3', null, null, null, '2013-03-11', '2019-12-14', null, '2015-11-03', '2014-06-14', '2015-12-24', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('87', '2', 'MR-87', null, null, '9', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '2', '100', '5', '科员', null, 't', '3', null, null, null, '2013-04-28', '2019-06-24', null, '2014-11-26', '2015-09-24', '2015-04-25', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('88', '2', 'US-88', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '1', '4', null, '12', '100', '2', 'asset-owner-2', null, 't', '2', null, null, null, '2013-05-18', '2019-10-29', null, '2013-05-22', '2014-05-05', '2014-02-04', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('89', '2', 'DSA-89', null, null, '7', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '3', '4', null, '6', '100', '1', 'asset-owner-1', null, 't', '1', null, null, null, '2013-05-21', '2020-01-05', null, '2015-05-26', '2014-06-15', '2015-06-03', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('90', '2', 'US-90', null, null, '6', null, null, null, null, null, null, null, null, null, null, '4', '四楼', '2', '4', null, '12', '100', '3', 'asset-owner-3', null, 't', '3', null, null, null, '2013-05-03', '2019-05-31', null, '2013-04-19', '2016-07-14', '2016-11-24', null, null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO "public"."asset_info" VALUES ('91', '2', 'NM-91', null, null, '3', null, null, null, null, null, null, null, null, null, null, '2', '二楼', '3', '2', null, '9', '100', '2', 'asset-owner-2', null, 't', '3', null, null, null, '2013-04-18', '2018-04-08', null, '2015-01-07', '2016-03-23', '2015-02-22', null, null, null, null, null, null, null, null, null, null, null, null);

-- ----------------------------
-- Alter Sequences Owned By 
-- ----------------------------

-- ----------------------------
-- Primary Key structure for table asset_info
-- ----------------------------
ALTER TABLE "public"."asset_info" ADD PRIMARY KEY ("id");

-- ----------------------------
-- Foreign Key structure for table "public"."asset_info"
-- ----------------------------
ALTER TABLE "public"."asset_info" ADD FOREIGN KEY ("hospital_id") REFERENCES "public"."org_info" ("id") ON DELETE NO ACTION ON UPDATE NO ACTION;