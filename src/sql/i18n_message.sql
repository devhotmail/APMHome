﻿truncate table i18n_message;

-- common i18n messages ( id from 1 to  199)

INSERT INTO "i18n_message" VALUES (1, 'label', 'logout', '注销', 'Logout', NULL, -1);
INSERT INTO "i18n_message" VALUES (2, 'label', 'login', '登录', 'Login', NULL, -1);
INSERT INTO "i18n_message" VALUES (3, 'label', 'login_name', '登录用户名', 'Login Name', NULL, -1);
INSERT INTO "i18n_message" VALUES (4, 'label', 'password', '密码', 'Password', NULL, -1);
INSERT INTO "i18n_message" VALUES (5, 'label', 'home', '首页', 'Home', NULL, -1);
INSERT INTO "i18n_message" VALUES (6, 'label', 'True2Yes', '是', 'Yes', NULL, -1);
INSERT INTO "i18n_message" VALUES (7, 'label', 'False2No', '否', 'No', NULL, -1);
INSERT INTO "i18n_message" VALUES (8, 'label', 'ValidationRequire', '输入不可为空！', 'Input is required!', NULL, -1);
INSERT INTO "i18n_message" VALUES (9, 'label', 'login_error', '用户名或密码错误', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (10, 'label', 'security_error', '用户名或密码错误', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (11, 'label', 'session_expired', '您的会话已经超时，请重新登录.', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (12, 'label', 'PersistenceErrorOccured', '保存数据出错.', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (13, 'label', 'PersistenceErrorDuplicateKey', '输入的数据与已有的记录重复.', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (14, 'label', 'Add', '添加', 'Add', NULL, -1);
INSERT INTO "i18n_message" VALUES (15, 'label', 'Create', '新增', 'Create', NULL, -1);
INSERT INTO "i18n_message" VALUES (16, 'label', 'View', '查看', 'View', NULL, -1);
INSERT INTO "i18n_message" VALUES (17, 'label', 'Edit', '修改', 'Edit', NULL, -1);
INSERT INTO "i18n_message" VALUES (18, 'label', 'Delete', '删除', 'Delete', NULL, -1);
INSERT INTO "i18n_message" VALUES (19, 'label', 'Close', '关闭', 'Close', NULL, -1);
INSERT INTO "i18n_message" VALUES (20, 'label', 'Cancel', '取消', 'Cancel', NULL, -1);
INSERT INTO "i18n_message" VALUES (21, 'label', 'Return', '返回', 'Return', NULL, -1);
INSERT INTO "i18n_message" VALUES (22, 'label', 'Save', '保存', 'Save', NULL, -1);
INSERT INTO "i18n_message" VALUES (23, 'label', 'Submit', '提交', 'Submit', NULL, -1);
INSERT INTO "i18n_message" VALUES (24, 'label', 'Confirm', '确认', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (25, 'label', 'Refresh', '刷新', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (26, 'label', 'AssignRoles', '设置角色权限', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (27, 'label', 'SelectOneMessage', '请选择...', 'Select....', NULL, -1);
INSERT INTO "i18n_message" VALUES (28, 'label', 'NewPassword', '请输入新密码', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (29, 'label', 'ConfirmNewPassword', '请再次输入新密码', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (30, 'label', 'deleteConfirmation', '您确信要删除此条数据么？', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (31, 'label', 'Created', '已经成功地被创建。', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (32, 'label', 'Updated', '已经成功地被修改。', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (33, 'label', 'Deleted', '已经成功地被删除。', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (34, 'label', 'noRecordFound', '(没有查询到数据)', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (35, 'label', 'recordCount', '总计', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (36, 'label', 'true', '是', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (37, 'label', 'false', '否', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (38, 'label', 'passwordIncorrect', '对不起，您输入的密码不正确', NULL, NULL, -1);
INSERT INTO "i18n_message" VALUES (39, 'label', 'security_bad_credentials', '用户名或密码错误', 'incorrect user name or password', NULL, -1);
INSERT INTO "i18n_message" VALUES (40, 'label', 'RowsPerPage', '每页行数', 'Rows/Page', NULL, -1);
INSERT INTO "i18n_message" VALUES (41, 'label', 'AssignRole', '角色设置', 'Assign Role', NULL, -1);
INSERT INTO "i18n_message" VALUES (42, 'label', 'ChangePassword', '修改密码', 'Change Password', NULL, -1);
INSERT INTO "i18n_message" VALUES (43, 'label', 'ResetPassword', '重置密码', 'Reset Password', NULL, -1);


-- menu item names  ( id from 200 to  399)

INSERT INTO "i18n_message" VALUES (200, 'label', 'HomePage', '我的主页', 'Dashboard', NULL, -1);
INSERT INTO "i18n_message" VALUES (201, 'label', 'HomePageHospitalHead', '我的主页(院长)', 'Dashboard(Hospital Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (202, 'label', 'HomePageDeptHead', '我的主页(主任)', 'Dashboard(Department Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (203, 'label', 'HomePageAssetHead', '我的主页(设备科)', 'Dashboard(Asset Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (204, 'label', 'AssetMDM', '设备资产档案管理', 'Asset Master Data Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (205, 'label', 'AssetList', '设备基本信息管理', 'Asset List', NULL, -1);
INSERT INTO "i18n_message" VALUES (206, 'label', 'AssetInventory', '设备落位管理/盘点', 'Asset Inventory', NULL, -1);
INSERT INTO "i18n_message" VALUES (207, 'label', 'AssetDocumentMgmt', '设备电子档案管理', 'Asset Document Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (208, 'label', 'AssetContractMgmt', '设备合同管理', 'Asset Contract Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (209, 'label', 'SupplierMgmt', '设备供应商管理', 'Supplier Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (210, 'label', 'AssetValueAnalysis', '设备资产价值统计分析', 'Asset Value Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (211, 'label', 'WorkOrderMgmt', '设备维修流程管理', 'Work Order Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (212, 'label', 'ServiceRequestMgmt', '报修管理', 'Service Request Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (213, 'label', 'MyWorkOrder', '我的工单', 'My Work Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (214, 'label', 'MaintainanceRecord', '维修记录', 'Maintainance Record', NULL, -1);
INSERT INTO "i18n_message" VALUES (215, 'label', 'ServiceQualityReview', '维修质量评估', 'Service Quality Review', NULL, -1);
INSERT INTO "i18n_message" VALUES (216, 'label', 'Inspection', '巡检', 'Inspection', NULL, -1);
INSERT INTO "i18n_message" VALUES (217, 'label', 'DeviceMonitor', '设备监控', 'Device Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (218, 'label', 'DeviceStatusMonitor', '设备状态监控', 'Device Status Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (219, 'label', 'DeviceDowntimeAnalysis', '设备停机率分析', 'Device Downtime Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (220, 'label', 'DeviceMaintainanceEventAnalysis', '设备维修事件统计分析', 'Maint. Event Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (221, 'label', 'DeviceMaintainanceCostAnalysis', '设备维护成本统计分析', 'Maint. Cost Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (222, 'label', 'DeviceUtilizationAnalysis', '设备使用情况', 'Utilization Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (223, 'label', 'SparePartsConsumptionAnalysis', '设备备件消耗分析', 'Spare Parts Consumption', NULL, -1);
INSERT INTO "i18n_message" VALUES (224, 'label', 'DeviceFailureAnalysis', '设备故障分类统计分析', 'Device Failure Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (225, 'label', 'DeviceOperationMonitor', '设备运营监控', 'Operation Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (226, 'label', 'PreventiveMaintainceMgmt', '预防性设备维护管理', 'Preventive Maint. Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (227, 'label', 'PreventiveMaintaincePlanning', '设备保养排期', 'Preventive Maint. Planning', NULL, -1);
INSERT INTO "i18n_message" VALUES (228, 'label', 'DeviceMetrologyMgmt', '设备计量管理', 'Device Metrology Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (229, 'label', 'DeviceQualityControl', '设备质量控制', 'Device Quality Control', NULL, -1);
INSERT INTO "i18n_message" VALUES (230, 'label', 'DeviceAdverseEventMgmt', '不良事件记录和上报', 'Adverse Event Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (231, 'label', 'DeviceMedicalAccidentMgmt', '医疗事故风险监控', 'Medical Accident Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (232, 'label', 'AssetKnowledgeBaseMgmt', '设备文档和知识库管理', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (233, 'label', 'AssetDocMgmt', '设备文档管理', 'Asset Documentation Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (234, 'label', 'AssetKMMgmt', '设备知识库管理', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (235, 'label', 'DeviceMgmtForum', '设备管理论坛', 'Device Mgmt. Forum', NULL, -1);
INSERT INTO "i18n_message" VALUES (236, 'label', 'DeviceApplication', '设备应用/维修培训管理', 'Device Training Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (237, 'label', 'DeviceUpgradMgmt', '设备升级管理', 'DeviceUpgradMgmt', NULL, -1);
INSERT INTO "i18n_message" VALUES (238, 'label', 'AdvancedDeviceInfoTracking', '先进设备信息收集跟踪', 'Advanced Machine Data Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (239, 'label', 'DeviceOperationKPIAnalysis', '设备运营绩效智能化分析', 'Operation KPI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (240, 'label', 'DeviceROIAnalysis', '设备平均投资回报分析', 'Device ROI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (241, 'label', 'DeviceKPIAnalysis', '设备绩效分析', 'Device KPI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (242, 'label', 'DeviceKPIForecast', '设备绩效预测', 'Device KPI Forecast', NULL, -1);
INSERT INTO "i18n_message" VALUES (243, 'label', 'DeviceCostAnalysis', '设备成本统计分析', 'Device Cost Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (244, 'label', 'DevicePurchaseDecisionSupport', '设备采购辅助决策分析', 'Purchase Decision Support', NULL, -1);
INSERT INTO "i18n_message" VALUES (245, 'label', 'UaaAdmin', '组织机构与用户管理', 'Organization & User Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (246, 'label', 'SiteAdmin', '租户管理', 'Site Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (247, 'label', 'OrgAdmin', '组织机构管理', 'Organization Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (248, 'label', 'UserAdmin', '用户管理', 'User Account Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (249, 'label', 'InspectionChecklist', '巡检配置', 'Inspection Checklist', NULL, -1);
INSERT INTO "i18n_message" VALUES (250, 'label', 'InspectionOrder', '巡检计划', 'Inspection Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (251, 'label', 'InspectionRecord', '巡检记录', 'Inspection Record', NULL, -1);
INSERT INTO "i18n_message" VALUES (252, 'label', 'ChecklistType', '检查类型', 'Checklist Type', NULL, -1);
INSERT INTO "i18n_message" VALUES (253, 'label', 'Profile', '我的帐号', 'My Profile', NULL, -1);
INSERT INTO "i18n_message" VALUES (254, 'label', 'Setting', '系统设置', 'Setting', NULL, -1);


-- Entity Object Names  ( id from 400 to  599)

INSERT INTO "i18n_message" VALUES (401, 'label', 'SiteInfo', '医联体/租户信息', 'Site Info', NULL, -1);
INSERT INTO "i18n_message" VALUES (402, 'label', 'OrgInfo', '组织机构信息', 'Organization Info', NULL, -1);
INSERT INTO "i18n_message" VALUES (403, 'label', 'UserAccount', '用户帐号信息', 'User Account Info', NULL, -1);
INSERT INTO "i18n_message" VALUES (404, 'label', 'AssetInfo', '资产信息', 'Asset Info', NULL, -1);
INSERT INTO "i18n_message" VALUES (405, 'label', 'WorkOrder', '维修工单', 'Preventive Maintainance Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (406, 'label', 'WorkOrderStep', '工单步骤', 'Work Order Steps', NULL, -1);
INSERT INTO "i18n_message" VALUES (407, 'label', 'WorkOrderStepDetail', '工单详情', 'Work Order Step Details', NULL, -1);
                                   
INSERT INTO "i18n_message" VALUES (409, 'label', 'AssetClinicalRecord', '检查记录', 'Work Order Step Details', NULL, -1);
INSERT INTO "i18n_message" VALUES (410, 'label', 'InspectionChecklist', '巡检Checklist', 'Inspection Checklist', NULL, -1);
INSERT INTO "i18n_message" VALUES (411, 'label', 'InspectionOrder', '巡检工单', 'Inspection Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (412, 'label', 'InspectionOrderDetail', '巡检工单详情', 'Inspection Order Detail', NULL, -1);
INSERT INTO "i18n_message" VALUES (413, 'label', 'Supplier', '供应商信息', 'Supplier', NULL, -1);


-- Entity Object field Names  (id from 600 to  1499)

INSERT INTO "i18n_message" VALUES (600,'field_name','accessory','备件','Accessory',null,-1);
INSERT INTO "i18n_message" VALUES (601,'field_name','accessoryPrice','备件单价','Accessory Price',null,-1);
INSERT INTO "i18n_message" VALUES (602,'field_name','accessoryQuantity','备件数量','Accessory Quantity',null,-1);
INSERT INTO "i18n_message" VALUES (603,'field_name','address','地址','Address',null,-1);
INSERT INTO "i18n_message" VALUES (604,'field_name','aliasName','别名','Alias Name',null,-1);
INSERT INTO "i18n_message" VALUES (605,'field_name','arriveDate','到货日期','Arrive Date',null,-1);
INSERT INTO "i18n_message" VALUES (606,'field_name','assetDeptId','所属设备科','Asset Dept Id',null,-1);
INSERT INTO "i18n_message" VALUES (607,'field_name','assetGroup','所属设备类型','Asset Group',null,-1);
INSERT INTO "i18n_message" VALUES (608,'field_name','assetId','设备ID','Asset Id',null,-1);
INSERT INTO "i18n_message" VALUES (609,'field_name','assetOwnerId','责任人','Asset Owner Id',null,-1);
INSERT INTO "i18n_message" VALUES (610,'field_name','assetOwnerName','责任人','Asset Owner Name',null,-1);
INSERT INTO "i18n_message" VALUES (611,'field_name','assetOwnerTel','责任人电话','Asset Owner Tel',null,-1);
INSERT INTO "i18n_message" VALUES (612,'field_name','attachmentUrl','上传附件','Attachment Url',null,-1);
INSERT INTO "i18n_message" VALUES (613,'field_name','barcode','条码','Barcode',null,-1);
INSERT INTO "i18n_message" VALUES (614,'field_name','caseOwnerId','负责人','Case Owner Id',null,-1);
INSERT INTO "i18n_message" VALUES (615,'field_name','caseOwnerName','负责人','Case Owner Name',null,-1);
INSERT INTO "i18n_message" VALUES (616,'field_name','casePriority','紧急程度','Case Priority',null,-1);
INSERT INTO "i18n_message" VALUES (617,'field_name','caseSubType','故障子类别','Case Sub Type',null,-1);
INSERT INTO "i18n_message" VALUES (618,'field_name','caseType','故障类别','Case Type',null,-1);
INSERT INTO "i18n_message" VALUES (619,'field_name','city','城市','City',null,-1);
INSERT INTO "i18n_message" VALUES (620,'field_name','clinicalDeptId','所属科室','Clinical Dept Id',null,-1);
INSERT INTO "i18n_message" VALUES (621,'field_name','clinicalDeptName','所属科室','Clinical Dept Name',null,-1);
INSERT INTO "i18n_message" VALUES (622,'field_name','closeReason','关单原因','Close Reason',null,-1);
INSERT INTO "i18n_message" VALUES (623,'field_name','comments','结论','Comments',null,-1);
INSERT INTO "i18n_message" VALUES (624,'field_name','confirmedEndTime','确认的恢复可用时间','Confirmed End Time',null,-1);
INSERT INTO "i18n_message" VALUES (625,'field_name','confirmedStartTime','确认的停机时间','Confirmed Start Time',null,-1);
INSERT INTO "i18n_message" VALUES (626,'field_name','contactor','联系人姓名','Contactor',null,-1);
INSERT INTO "i18n_message" VALUES (627,'field_name','createTime','创建时间','Create Time',null,-1);
INSERT INTO "i18n_message" VALUES (628,'field_name','creatorId','创建方','Creator Id',null,-1);
INSERT INTO "i18n_message" VALUES (629,'field_name','creatorName','创建方','Creator Name',null,-1);
INSERT INTO "i18n_message" VALUES (630,'field_name','currentPersonId','当前处理人','Current Person Id',null,-1);
INSERT INTO "i18n_message" VALUES (631,'field_name','currentPersonName','当前处理人','Current Person Name',null,-1);
INSERT INTO "i18n_message" VALUES (632,'field_name','currentStep','当前处理步骤','Current Step',null,-1);
INSERT INTO "i18n_message" VALUES (633,'field_name','departNum','设备编号（院方）','Depart Num',null,-1);
INSERT INTO "i18n_message" VALUES (634,'field_name','depreciationMethod','折旧算法','Depreciation Method',null,-1);
INSERT INTO "i18n_message" VALUES (635,'field_name','deptId','巡检科室','Dept Id',null,-1);
INSERT INTO "i18n_message" VALUES (636,'field_name','deptName','巡检科室','Dept Name',null,-1);
INSERT INTO "i18n_message" VALUES (637,'field_name','description','处理描述','Description',null,-1);
INSERT INTO "i18n_message" VALUES (638,'field_name','endTime','结束时间','End Time',null,-1);
INSERT INTO "i18n_message" VALUES (639,'field_name','examDate','检查日期','Exam Date',null,-1);
INSERT INTO "i18n_message" VALUES (640,'field_name','examEndTime','结束时间','Exam End Time',null,-1);
INSERT INTO "i18n_message" VALUES (641,'field_name','examStartTime','开始时间','Exam Start Time',null,-1);
INSERT INTO "i18n_message" VALUES (642,'field_name','exposeCount','曝光量','Expose Count',null,-1);
INSERT INTO "i18n_message" VALUES (643,'field_name','fileType','文件类型','File Type',null,-1);
INSERT INTO "i18n_message" VALUES (644,'field_name','fileUrl','文件路径','File Url',null,-1);
INSERT INTO "i18n_message" VALUES (645,'field_name','filmCount','胶片量','Film Count',null,-1);
INSERT INTO "i18n_message" VALUES (646,'field_name','financingNum','资产编号','Financing Num',null,-1);
INSERT INTO "i18n_message" VALUES (647,'field_name','functionGrade','功能等级','Function Grade',null,-1);
INSERT INTO "i18n_message" VALUES (648,'field_name','functionGroup','功能分组','Function Group',null,-1);
INSERT INTO "i18n_message" VALUES (649,'field_name','functionType','设备类型','Function Type',null,-1);
INSERT INTO "i18n_message" VALUES (650,'field_name','hospitalId','所属院区','Hospital Id',null,-1);
INSERT INTO "i18n_message" VALUES (651,'field_name','injectCount','注射量','Inject Count',null,-1);
INSERT INTO "i18n_message" VALUES (652,'field_name','installDate','安装日期','Install Date',null,-1);
INSERT INTO "i18n_message" VALUES (653,'field_name','isClosed','是否已关单','Is Closed',null,-1);
INSERT INTO "i18n_message" VALUES (654,'field_name','isFinished','是否完成','Is Finished',null,-1);
INSERT INTO "i18n_message" VALUES (655,'field_name','isInternal','是否内部工单？','Is Internal',null,-1);
INSERT INTO "i18n_message" VALUES (656,'field_name','isPassed','是否通过','Is Passed',null,-1);
INSERT INTO "i18n_message" VALUES (657,'field_name','isValid','是否有效（使用）','Is Valid',null,-1);
INSERT INTO "i18n_message" VALUES (658,'field_name','item','巡检项目','Item',null,-1);
INSERT INTO "i18n_message" VALUES (659,'field_name','itemId','巡检项目','Item Id',null,-1);
INSERT INTO "i18n_message" VALUES (660,'field_name','itemName','巡检项目','Item Name',null,-1);
INSERT INTO "i18n_message" VALUES (661,'field_name','lifecycle','使用年限','Lifecycle',null,-1);
INSERT INTO "i18n_message" VALUES (662,'field_name','locationCode','安装位置编码','Location Code',null,-1);
INSERT INTO "i18n_message" VALUES (663,'field_name','locationName','安装位置','Location Name',null,-1);
INSERT INTO "i18n_message" VALUES (664,'field_name','maitanance','维修商','Maitanance',null,-1);
INSERT INTO "i18n_message" VALUES (665,'field_name','maitananceTel','维修电话','Maitanance Tel',null,-1);
INSERT INTO "i18n_message" VALUES (666,'field_name','manHours','工时','Man Hours',null,-1);
INSERT INTO "i18n_message" VALUES (667,'field_name','manufactDate','出厂日期','Manufact Date',null,-1);
INSERT INTO "i18n_message" VALUES (668,'field_name','manufacture','制造商','Manufacture',null,-1);
INSERT INTO "i18n_message" VALUES (669,'field_name','modalityId','院内IT系统编号','Modality Id',null,-1);
INSERT INTO "i18n_message" VALUES (670,'field_name','modalityType','设备类型','Modality Type',null,-1);
INSERT INTO "i18n_message" VALUES (671,'field_name','modalityTypeId','设备类型','Modality Type Id',null,-1);
INSERT INTO "i18n_message" VALUES (672,'field_name','name','名称','Name',null,-1);
INSERT INTO "i18n_message" VALUES (673,'field_name','nextTime','下次计划时间','Next Time',null,-1);
INSERT INTO "i18n_message" VALUES (674,'field_name','ownerId','执行人','Owner Id',null,-1);
INSERT INTO "i18n_message" VALUES (675,'field_name','ownerName','责任人','Owner Name',null,-1);
INSERT INTO "i18n_message" VALUES (676,'field_name','ownerOrgId','执行人所属部门','Owner Org Id',null,-1);
INSERT INTO "i18n_message" VALUES (677,'field_name','ownerOrgName','执行人所属部门','Owner Org Name',null,-1);
INSERT INTO "i18n_message" VALUES (678,'field_name','paperUrl','扫描件上传（备用）','Paper Url',null,-1);
INSERT INTO "i18n_message" VALUES (679,'field_name','patientAge','年龄','Patient Age',null,-1);
INSERT INTO "i18n_message" VALUES (680,'field_name','patientGender','性别','Patient Gender',null,-1);
INSERT INTO "i18n_message" VALUES (681,'field_name','patientId','患者信息','Patient Id',null,-1);
INSERT INTO "i18n_message" VALUES (682,'field_name','patientNameEn','姓名','Patient Name',null,-1);
INSERT INTO "i18n_message" VALUES (683,'field_name','patientNameZh','姓名','Patient Name',null,-1);
INSERT INTO "i18n_message" VALUES (684,'field_name','planDate','计划内最近一次维护日期','Plan Date',null,-1);
INSERT INTO "i18n_message" VALUES (685,'field_name','priceAmount','价格','Price Amount',null,-1);
INSERT INTO "i18n_message" VALUES (686,'field_name','priceUnit','单位','Price Unit',null,-1);
INSERT INTO "i18n_message" VALUES (687,'field_name','procedureId','检查部位','Procedure Id',null,-1);
INSERT INTO "i18n_message" VALUES (688,'field_name','procedureName','检查部位','Procedure Name',null,-1);
INSERT INTO "i18n_message" VALUES (689,'field_name','procedureStepId','检查步骤','Procedure Step Id',null,-1);
INSERT INTO "i18n_message" VALUES (690,'field_name','procedureStepName','检查步骤','Procedure Step Name',null,-1);
INSERT INTO "i18n_message" VALUES (691,'field_name','purchaseDate','购买日期','Purchase Date',null,-1);
INSERT INTO "i18n_message" VALUES (692,'field_name','purchasePrice','采购价格','Purchase Price',null,-1);
INSERT INTO "i18n_message" VALUES (693,'field_name','reportUrl','维护报告','Report Url',null,-1);
INSERT INTO "i18n_message" VALUES (694,'field_name','requestReason','故障现象','Request Reason',null,-1);
INSERT INTO "i18n_message" VALUES (695,'field_name','requestTime','报修时间','Request Time',null,-1);
INSERT INTO "i18n_message" VALUES (696,'field_name','requestorId','报修人','Requestor Id',null,-1);
INSERT INTO "i18n_message" VALUES (697,'field_name','requestorName','报修人','Requestor Name',null,-1);
INSERT INTO "i18n_message" VALUES (698,'field_name','salvageValue','最终残值','Salvage Value',null,-1);
INSERT INTO "i18n_message" VALUES (699,'field_name','serialNum','设备编号（制造商）','Serial Num',null,-1);
INSERT INTO "i18n_message" VALUES (700,'field_name','startTime','开始时间','Start Time',null,-1);
INSERT INTO "i18n_message" VALUES (701,'field_name','status','当前状态','Status',null,-1);
INSERT INTO "i18n_message" VALUES (702,'field_name','stepName','步骤名称','Step Name',null,-1);
INSERT INTO "i18n_message" VALUES (703,'field_name','tel','电话','Tel',null,-1);
INSERT INTO "i18n_message" VALUES (704,'field_name','terminateDate','报废日期','Terminate Date',null,-1);
INSERT INTO "i18n_message" VALUES (705,'field_name','totalManHour','总计人工','Total Man Hour',null,-1);
INSERT INTO "i18n_message" VALUES (706,'field_name','totalPrice','总计费用','Total Price',null,-1);
INSERT INTO "i18n_message" VALUES (707,'field_name','updateDetail','变更内容描述','Update Detail',null,-1);
INSERT INTO "i18n_message" VALUES (708,'field_name','updatePersonId','变更人','Update Person Id',null,-1);
INSERT INTO "i18n_message" VALUES (709,'field_name','updatePersonName','变更人','Update Person Name',null,-1);
INSERT INTO "i18n_message" VALUES (710,'field_name','updateTime','处理时间','Update Time',null,-1);
INSERT INTO "i18n_message" VALUES (711,'field_name','vendor','供应商','Vendor',null,-1);
INSERT INTO "i18n_message" VALUES (712,'field_name','warrantyDate','保证截止日期','Warranty Date',null,-1);
INSERT INTO "i18n_message" VALUES (713,'field_name','zipcode','邮编','Zipcode',null,-1);
INSERT INTO "i18n_message" VALUES (714,'field_name','contactEmail','Email','Contact Email',null,-1);
INSERT INTO "i18n_message" VALUES (715,'field_name','contactPerson','联系人','Contact Person',null,-1);
INSERT INTO "i18n_message" VALUES (716,'field_name','contactPhone','联系电话','Contact Phone',null,-1);
INSERT INTO "i18n_message" VALUES (717,'field_name','defaultLang','默认语言','Default Lang',null,-1);
INSERT INTO "i18n_message" VALUES (718,'field_name','email','Email','Email',null,-1);
INSERT INTO "i18n_message" VALUES (719,'field_name','homePage','首页','Home Page',null,-1);
INSERT INTO "i18n_message" VALUES (720,'field_name','isActive','激活?','Active?',null,-1);
INSERT INTO "i18n_message" VALUES (721,'field_name','isEnabled','启用?','Enabled?',null,-1);
INSERT INTO "i18n_message" VALUES (722,'field_name','isLocalAdmin','本院管理员','Local Admin?',null,-1);
INSERT INTO "i18n_message" VALUES (723,'field_name','isOnline','在线?','Online?',null,-1);
INSERT INTO "i18n_message" VALUES (724,'field_name','isSiteAdmin','Site管理员?','Site Admin?',null,-1);
INSERT INTO "i18n_message" VALUES (725,'field_name','isSuperAdmin','超级管理员?','Super Admin?',null,-1);
INSERT INTO "i18n_message" VALUES (726,'field_name','lastLoginTime','上次登录时间','Last Login Time',null,-1);
INSERT INTO "i18n_message" VALUES (727,'field_name','location','地点','Location',null,-1);
INSERT INTO "i18n_message" VALUES (728,'field_name','locationEn','地点','Location En',null,-1);
INSERT INTO "i18n_message" VALUES (729,'field_name','nameEn','名称(En)','Name En',null,-1);
INSERT INTO "i18n_message" VALUES (730,'field_name','orgId','所属部门','Org Id',null,-1);
INSERT INTO "i18n_message" VALUES (731,'field_name','parentId','上级部门','Parent Id',null,-1);
INSERT INTO "i18n_message" VALUES (732,'field_name','roleDesc','角色描述','Role Desc',null,-1);
INSERT INTO "i18n_message" VALUES (733,'field_name','roleId','角色','Role Id',null,-1);
INSERT INTO "i18n_message" VALUES (734,'field_name','telephone','电话','Telephone',null,-1);
INSERT INTO "i18n_message" VALUES (735,'field_name','timeZone','时区','Time Zone',null,-1);
INSERT INTO "i18n_message" VALUES (736,'field_name','siteDescription','详细描述','Description',null,-1);
INSERT INTO "i18n_message" VALUES (737,'field_name','userName','姓名','Name',null,-1);
INSERT INTO "i18n_message" VALUES (738,'field_name','workOrderName','工单名称','work Order Name',null,-1);


-- field value code types  ( id from 1500)

INSERT INTO "i18n_message" VALUES (1500,'asset_group','1','CT','CT',null,-1);
INSERT INTO "i18n_message" VALUES (1501,'asset_group','2','MR','MR',null,-1);
INSERT INTO "i18n_message" VALUES (1502,'asset_group','3','XRay','XRay',null,-1);
INSERT INTO "i18n_message" VALUES (1503,'asset_group','4','DR','DR',null,-1);

INSERT INTO "i18n_message" VALUES (1510,'wo_steps','1', '报修','Create',null,-1);
INSERT INTO "i18n_message" VALUES (1511,'wo_steps','2', '审核/审批','Approve',null,-1);
INSERT INTO "i18n_message" VALUES (1512,'wo_steps','3', '派工','Assign',null,-1);
INSERT INTO "i18n_message" VALUES (1513,'wo_steps','4', '领工','Accept',null,-1);
INSERT INTO "i18n_message" VALUES (1514,'wo_steps','5', '维修','Repair',null,-1);
INSERT INTO "i18n_message" VALUES (1515,'wo_steps','6', '关单','Close',null,-1);

INSERT INTO "i18n_message" VALUES (1520,'checklist_type','1','巡检','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1521,'checklist_type','2','计量','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1522,'checklist_type','3','质控','Inspection',null,-1);

INSERT INTO "i18n_message" VALUES (1530,'attachment_type','1','照片','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1531,'attachment_type','2','合同','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1532,'attachment_type','3','用户手册','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1533,'attachment_type','4','培训资料','Inspection',null,-1);

-- module's messages  (id from 3000)

INSERT INTO "i18n_message" VALUES (3000,'message','devicePhoto', '设备照片文件','device Photo',null,-1);
INSERT INTO "i18n_message" VALUES (3001,'message','deviceContract', '设备合同文件','deviceContract',null,-1);


-- update id sequence value
SELECT setval('"public"."i18n_message_id_seq"', 4000, false);
