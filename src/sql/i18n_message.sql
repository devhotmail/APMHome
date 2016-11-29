truncate table i18n_message;

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
INSERT INTO "i18n_message" VALUES (44, 'label', 'exportToXLS', '导出到Excel', 'Export To Excel', NULL, -1);
INSERT INTO "i18n_message" VALUES (45, 'label', 'List', '列表', 'List', NULL, -1);
INSERT INTO "i18n_message" VALUES (46, 'label', 'Download', '下载', 'Download', NULL, -1);
INSERT INTO "i18n_message" VALUES (47, 'label', 'Search', '查询', 'Search', NULL, -1);
INSERT INTO "i18n_message" VALUES (48, 'label', 'Reset', '重置', 'Reset', NULL, -1);
INSERT INTO "i18n_message" VALUES (49, 'label','Finish', '完成','Finish ',null,-1);
INSERT INTO "i18n_message" VALUES (50, 'label','Actions', '操作','Actions ',null,-1);
INSERT INTO "i18n_message" VALUES (51, 'label','DataSavedOK', '数据已经保存成功.','data saved successfully ',null,-1);
INSERT INTO "i18n_message" VALUES (52, 'label','Welcome', '欢迎您','Welcome',null,-1);
INSERT INTO "i18n_message" VALUES (53, 'label','DuplicateLoginName', '登录用户名 "%s" 已有人使用，请更换登录用户名.','Login name already used by others, please change to another login name.',null,-1);
INSERT INTO "i18n_message" VALUES (54, 'label','BatchImport', '批量导入系统配置','BatchImport',null,-1);
INSERT INTO "i18n_message" VALUES (55, 'label','ClearAll', '清空自定义配置','ClearAll',null,-1);
INSERT INTO "i18n_message" VALUES (55, 'label','Execute', '执行','Execute',null,-1);
INSERT INTO "i18n_message" VALUES (56,'message','Http500ErrorTitle', '系统发生异常','Exception Occured',null,-1);
INSERT INTO "i18n_message" VALUES (57,'message','Http500ErrorMsg', '请联系系统管理员.','Please contact system administrator.',null,-1);
INSERT INTO "i18n_message" VALUES (58,'message','Http404NotFoundTitle', '网页不存在','Page Not Found',null,-1);
INSERT INTO "i18n_message" VALUES (59,'message','Http404NotFoundMsg', '您想访问的网页不存在.','The resource you are looking for does not exist.',null,-1);
INSERT INTO "i18n_message" VALUES (60,'message','Http401AccessDeniedTitle', '权限受限','Access Denied',null,-1);
INSERT INTO "i18n_message" VALUES (61,'message','Http401AccessDeniedMsg', '对不起，您未获得访问此网页的授权.','You are not authorized to access this resource.',null,-1);


-- menu item names  ( id from 200 to  399)

INSERT INTO "i18n_message" VALUES (200, 'label', 'HomePage', '我的主页', 'Dashboard', NULL, -1);
INSERT INTO "i18n_message" VALUES (201, 'label', 'HomePageHospitalHead', '我的主页(院长)', 'Dashboard(Hospital Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (202, 'label', 'HomePageDeptHead', '我的主页(主任)', 'Dashboard(Department Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (203, 'label', 'HomePageAssetHead', '我的主页(设备科)', 'Dashboard(Asset Head)', NULL, -1);
INSERT INTO "i18n_message" VALUES (204, 'label', 'AssetMDM', '设备档案', 'Asset Master Data Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (205, 'label', 'AssetList', '基本信息', 'Asset List', NULL, -1);
INSERT INTO "i18n_message" VALUES (206, 'label', 'AssetInventory', '设备盘点', 'Asset Inventory', NULL, -1);
INSERT INTO "i18n_message" VALUES (207, 'label', 'AssetDocumentMgmt', '电子档案', 'Asset Document Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (208, 'label', 'AssetContractMgmt', '合同管理', 'Asset Contract Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (209, 'label', 'SupplierMgmt', '供应商管理', 'Supplier Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (210, 'label', 'AssetValueAnalysis', '设备价值统计', 'Asset Value Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (211, 'label', 'WorkOrderMgmt', '维修流程', 'Work Order Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (212, 'label', 'ServiceRequestMgmt', '报修管理', 'Service Request Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (213, 'label', 'MyWorkOrder', '报修处理', 'Work Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (214, 'label', 'MaintainanceRecord', '维修记录', 'Maintainance Record', NULL, -1);
INSERT INTO "i18n_message" VALUES (215, 'label', 'ServiceQualityReview', '维修质量评估', 'Service Quality Review', NULL, -1);
INSERT INTO "i18n_message" VALUES (216, 'label', 'Inspection', '巡检管理', 'Inspection', NULL, -1);
INSERT INTO "i18n_message" VALUES (217, 'label', 'DeviceMonitor', '设备监控', 'Device Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (218, 'label', 'DeviceStatusMonitor', '状态监控', 'Device Status Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (219, 'label', 'DeviceDowntimeAnalysis', '停机率分析', 'Device Downtime Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (220, 'label', 'DeviceMaintainanceEventAnalysis', '维修事件统计', 'Maint. Event Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (221, 'label', 'DeviceMaintainanceCostAnalysis', '维护成本统计', 'Maint. Cost Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (222, 'label', 'DeviceUtilizationAnalysis', '使用情况统计', 'Utilization Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (223, 'label', 'SparePartsConsumptionAnalysis', '备件消耗统计', 'Spare Parts Consumption', NULL, -1);
INSERT INTO "i18n_message" VALUES (224, 'label', 'DeviceFailureAnalysis', '故障分类统计', 'Device Failure Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (225, 'label', 'DeviceOperationMonitor', '运营监控', 'Operation Monitor', NULL, -1);
INSERT INTO "i18n_message" VALUES (226, 'label', 'PreventiveMaintainceMgmt', '预防维护', 'Preventive Maint. Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (227, 'label', 'PreventiveMaintaincePlanning', '保养统计', 'Preventive Maint. Planning', NULL, -1);
INSERT INTO "i18n_message" VALUES (258, 'label', 'PmRecord', '保养记录', 'Checklist Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (228, 'label', 'DeviceMetrologyMgmt', '计量管理', 'Device Metrology Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (229, 'label', 'DeviceQualityControl', '质控管理', 'Device Quality Control', NULL, -1);
INSERT INTO "i18n_message" VALUES (230, 'label', 'DeviceAdverseEventMgmt', '不良事件上报', 'Adverse Event Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (231, 'label', 'DeviceMedicalAccidentMgmt', '医疗事故风险监控', 'Medical Accident Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (232, 'label', 'AssetKnowledgeBaseMgmt', '知识文档', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (233, 'label', 'AssetDocMgmt', '文档管理', 'Asset Documentation Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (234, 'label', 'AssetKMMgmt', '知识库管理', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (235, 'label', 'DeviceMgmtForum', '管理论坛', 'Device Mgmt. Forum', NULL, -1);
INSERT INTO "i18n_message" VALUES (236, 'label', 'DeviceApplication', '应用/维修培训管理', 'Device Training Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (237, 'label', 'DeviceUpgradMgmt', '升级管理', 'DeviceUpgradMgmt', NULL, -1);
INSERT INTO "i18n_message" VALUES (238, 'label', 'AdvancedDeviceInfoTracking', '扫描详情统计', 'Advanced Machine Data Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (239, 'label', 'DeviceOperationKPIAnalysis', '绩效分析', 'Operation KPI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (240, 'label', 'DeviceROIAnalysis', '平均投资回报分析', 'Device ROI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (241, 'label', 'DeviceKPIAnalysis', '绩效分析', 'Device KPI Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (242, 'label', 'DeviceKPIForecast', '绩效预测', 'Device KPI Forecast', NULL, -1);
INSERT INTO "i18n_message" VALUES (243, 'label', 'DeviceCostAnalysis', '成本统计分析', 'Device Cost Analysis', NULL, -1);
INSERT INTO "i18n_message" VALUES (244, 'label', 'DevicePurchaseDecisionSupport', '辅助决策分析', 'Purchase Decision Support', NULL, -1);
INSERT INTO "i18n_message" VALUES (245, 'label', 'SysAdmin', '系统配置', 'System Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (246, 'label', 'SiteAdmin', '租户管理', 'Site Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (247, 'label', 'OrgAdmin', '组织机构管理', 'Organization Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (248, 'label', 'UserAdmin', '用户管理', 'User Account Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (249, 'label', 'InspectionChecklist', '巡检配置', 'Inspection Checklist', NULL, -1);
INSERT INTO "i18n_message" VALUES (250, 'label', 'InspectionOrder', '巡检计划', 'Inspection Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (251, 'label', 'InspectionRecord', '巡检记录', 'Inspection Record', NULL, -1);
INSERT INTO "i18n_message" VALUES (252, 'label', 'ChecklistType', '检查类型', 'Checklist Type', NULL, -1);
INSERT INTO "i18n_message" VALUES (253, 'label', 'Profile', '我的帐号', 'My Profile', NULL, -1);
INSERT INTO "i18n_message" VALUES (254, 'label', 'Setting', '系统设置', 'Setting', NULL, -1);
INSERT INTO "i18n_message" VALUES (255, 'label', 'BasicInfo', '基本信息', 'Basic Info', NULL, -1);
INSERT INTO "i18n_message" VALUES (256, 'label', 'UsageAmount', '使用量', 'Usage Amount', NULL, -1);
INSERT INTO "i18n_message" VALUES (257, 'label', 'Attachment', '附件', 'Attachment', NULL, -1);
INSERT INTO "i18n_message" VALUES (259, 'label', 'ChecklistAdmin', '巡检/质控/计量条目维护', 'Checklist Mgmt.', NULL, -1);
INSERT INTO "i18n_message" VALUES (260, 'label', 'checkListSetting', '检查项配置', 'Checklist Configure', NULL, -1);


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
INSERT INTO "i18n_message" VALUES (412, 'label', 'InspectionOrderDetail', '巡检工单详情', 'Inspection Order Detail', NULL, -1);
INSERT INTO "i18n_message" VALUES (413, 'label', 'Supplier', '供应商信息', 'Supplier', NULL, -1);
INSERT INTO "i18n_message" VALUES (414, 'label', 'InspectionOrder', '质检工单', 'Inspection Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (415, 'label', 'MetrologOrder', '计量工单', 'Inspection Order', NULL, -1);
INSERT INTO "i18n_message" VALUES (416, 'label', 'QualityCtrlOrder', '巡检工单', 'Inspection Order', NULL, -1);


-- Entity Object field Names  (id from 600 to  1499)

INSERT INTO "i18n_message" VALUES (600,'field_name','accessory','备件','Accessory',null,-1);
INSERT INTO "i18n_message" VALUES (601,'field_name','accessoryPrice','单价','Accessory Price',null,-1);
INSERT INTO "i18n_message" VALUES (602,'field_name','accessoryQuantity','数量','Accessory Quantity',null,-1);
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
INSERT INTO "i18n_message" VALUES (616,'field_name','casePriority','紧急度','Case Priority',null,-1);
INSERT INTO "i18n_message" VALUES (617,'field_name','caseSubType','故障子类别','Case Sub Type',null,-1);
INSERT INTO "i18n_message" VALUES (618,'field_name','caseType','故障类别','Case Type',null,-1);
INSERT INTO "i18n_message" VALUES (619,'field_name','city','城市','City',null,-1);
INSERT INTO "i18n_message" VALUES (620,'field_name','clinicalDeptId','所属科室','Clinical Dept Id',null,-1);
INSERT INTO "i18n_message" VALUES (621,'field_name','clinicalDeptName','所属科室','Clinical Dept Name',null,-1);
INSERT INTO "i18n_message" VALUES (622,'field_name','closeReason','关单原因','Close Reason',null,-1);
INSERT INTO "i18n_message" VALUES (623,'field_name','comments','备注','Comments',null,-1);
INSERT INTO "i18n_message" VALUES (624,'field_name','confirmedEndTime','确认的恢复可用时间','Confirmed End Time',null,-1);
INSERT INTO "i18n_message" VALUES (625,'field_name','confirmedStartTime','确认的停机时间','Confirmed Start Time',null,-1);
INSERT INTO "i18n_message" VALUES (626,'field_name','contactor','联系人姓名','Contactor',null,-1);
INSERT INTO "i18n_message" VALUES (627,'field_name','createTime','创建时间','Create Time',null,-1);
INSERT INTO "i18n_message" VALUES (628,'field_name','nextStep','下一步','Next Step',null,-1);
INSERT INTO "i18n_message" VALUES (629,'field_name','creatorName','创建者','Creator Name',null,-1);
INSERT INTO "i18n_message" VALUES (630,'field_name','taskOwner','处理人','Task Owner',null,-1);
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
INSERT INTO "i18n_message" VALUES (653,'field_name','isClosed','已关单？','Is Closed',null,-1);
INSERT INTO "i18n_message" VALUES (654,'field_name','isFinished','完成？','Is Finished',null,-1);
INSERT INTO "i18n_message" VALUES (655,'field_name','isInternal','内部工单？','Is Internal',null,-1);
INSERT INTO "i18n_message" VALUES (656,'field_name','isPassed','通过?','Is Passed',null,-1);
INSERT INTO "i18n_message" VALUES (657,'field_name','isValid','在用？','Is Valid',null,-1);
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
INSERT INTO "i18n_message" VALUES (739,'field_name','lastPmDate','上次维护时间','Last Maintainance Date',null,-1);
INSERT INTO "i18n_message" VALUES (740,'field_name','lastQaDate','上次质控时间','Last Quality Control Date',null,-1);
INSERT INTO "i18n_message" VALUES (741,'field_name','lastMeteringDate','上次计量时间','Last Metering Date',null,-1);
INSERT INTO "i18n_message" VALUES (742,'field_name','assetName','资产名称','Asset Name',null,-1);
INSERT INTO "i18n_message" VALUES (743,'field_name','period','周期','Period',null,-1);

-- field value code types  ( id from 1400)

INSERT INTO "i18n_message" VALUES (1400,'assetGroup','1','CT','CT',null,-1);
INSERT INTO "i18n_message" VALUES (1401,'assetGroup','2','MR','MR',null,-1);
INSERT INTO "i18n_message" VALUES (1402,'assetGroup','3','DR','DR',null,-1);
INSERT INTO "i18n_message" VALUES (1403,'assetGroup','4','CR','CR',null,-1);
INSERT INTO "i18n_message" VALUES (1404,'assetGroup','5','RF','RF',null,-1);
INSERT INTO "i18n_message" VALUES (1405,'assetGroup','6','DSA','DSA',null,-1);
INSERT INTO "i18n_message" VALUES (1406,'assetGroup','7','乳腺机','乳腺机',null,-1);
INSERT INTO "i18n_message" VALUES (1407,'assetGroup','8','PET','PET',null,-1);
INSERT INTO "i18n_message" VALUES (1408,'assetGroup','9','NM','NM',null,-1);
INSERT INTO "i18n_message" VALUES (1409,'assetGroup','10','PET-CT','PET-CT',null,-1);
INSERT INTO "i18n_message" VALUES (1410,'assetGroup','11','PET-MR','PET-MR',null,-1);
INSERT INTO "i18n_message" VALUES (1411,'assetGroup','12','US','US',null,-1);


INSERT INTO "i18n_message" VALUES (1510,'woSteps','1', '报修','Create',null,-1);
INSERT INTO "i18n_message" VALUES (1511,'woSteps','2', '审核','Approve',null,-1);
INSERT INTO "i18n_message" VALUES (1512,'woSteps','3', '派工','Assign',null,-1);
INSERT INTO "i18n_message" VALUES (1513,'woSteps','4', '领工','Accept',null,-1);
INSERT INTO "i18n_message" VALUES (1514,'woSteps','5', '维修','Repair',null,-1);
INSERT INTO "i18n_message" VALUES (1515,'woSteps','6', '关单','Close',null,-1);

INSERT INTO "i18n_message" VALUES (1517,'assetStatus','1', '正常','Up',null,-1);
INSERT INTO "i18n_message" VALUES (1518,'assetStatus','2', '停机','Down',null,-1);
INSERT INTO "i18n_message" VALUES (1519,'assetStatus','3', '有异常','Partial',null,-1);

INSERT INTO "i18n_message" VALUES (1520,'checklistType','1','巡检','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1521,'checklistType','2','计量','Inspection',null,-1);
INSERT INTO "i18n_message" VALUES (1522,'checklistType','3','质控','Inspection',null,-1);

INSERT INTO "i18n_message" VALUES (1523,'casePriority','1','紧急','Normal',null,-1);
INSERT INTO "i18n_message" VALUES (1524,'casePriority','2','重要','Important',null,-1);
INSERT INTO "i18n_message" VALUES (1525,'casePriority','3','一般','Emergent',null,-1);



INSERT INTO "i18n_message" VALUES (1530,'attachmentType','1','照片','Photo',null,-1);
INSERT INTO "i18n_message" VALUES (1531,'attachmentType','2','合同','Contract',null,-1);
INSERT INTO "i18n_message" VALUES (1532,'attachmentType','3','用户手册','User Manual',null,-1);
INSERT INTO "i18n_message" VALUES (1533,'attachmentType','4','培训资料','Material for training',null,-1);

INSERT INTO "i18n_message" VALUES (1535,'assetFunctionType','2', '6801基础外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1536,'assetFunctionType','3', '6802显微外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1537,'assetFunctionType','4', '6803神经外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1538,'assetFunctionType','5', '6804眼科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1539,'assetFunctionType','6', '6805耳鼻喉科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1540,'assetFunctionType','7', '6806口腔科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1541,'assetFunctionType','8', '6807胸腔心血管外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1542,'assetFunctionType','9', '6808腹部外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1543,'assetFunctionType','10', '6809泌尿肛肠外科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1544,'assetFunctionType','11', '6810矫形外科（骨科）手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1545,'assetFunctionType','12', '6812妇产科用手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1546,'assetFunctionType','13', '6813计划生育手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1547,'assetFunctionType','14', '6815注射穿刺器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1548,'assetFunctionType','15', '6816烧伤(整形)科手术器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1549,'assetFunctionType','16', '6820普通诊察器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1550,'assetFunctionType','17', '6821医用电子仪器设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1551,'assetFunctionType','18', '6822医用光学器具、仪器及内窥镜设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1552,'assetFunctionType','19', '6823医用超声仪器及有关设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1553,'assetFunctionType','20', '6824医用激光仪器设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1554,'assetFunctionType','21', '6825医用高频仪器设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1555,'assetFunctionType','22', '6826物理治疗及康复设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1556,'assetFunctionType','23', '6827中医器械',null,null,-1);
INSERT INTO "i18n_message" VALUES (1557,'assetFunctionType','24', '6828医用磁共振设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1558,'assetFunctionType','25', '6830医用X射线设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1559,'assetFunctionType','26', '6831医用X射线附属设备及部件',null,null,-1);
INSERT INTO "i18n_message" VALUES (1560,'assetFunctionType','27', '6832医用高能射线设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1561,'assetFunctionType','28', '6833医用核素设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1562,'assetFunctionType','29', '6834医用射线防护用品、装置',null,null,-1);
INSERT INTO "i18n_message" VALUES (1563,'assetFunctionType','30', '6840临床检验分析仪器',null,null,-1);
INSERT INTO "i18n_message" VALUES (1564,'assetFunctionType','31', '6841医用化验和基础设备器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1565,'assetFunctionType','32', '6845体外循环及血液处理设备',null,null,-1);
INSERT INTO "i18n_message" VALUES (1566,'assetFunctionType','33', '6846植入材料和人工器官',null,null,-1);
INSERT INTO "i18n_message" VALUES (1567,'assetFunctionType','34', '6854手术室、急救室、诊疗室设备及器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1568,'assetFunctionType','35', '6855口腔科设备及器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1569,'assetFunctionType','36', '6856病房护理设备及器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1570,'assetFunctionType','37', '6857消毒和灭菌设备及器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1571,'assetFunctionType','38', '6858医用冷疗、低温、冷藏设备及器具',null,null,-1);
INSERT INTO "i18n_message" VALUES (1572,'assetFunctionType','39', '6863口腔科材料',null,null,-1);
INSERT INTO "i18n_message" VALUES (1573,'assetFunctionType','40', '6864医用卫生材料及敷料',null,null,-1);
INSERT INTO "i18n_message" VALUES (1574,'assetFunctionType','41', '6865医用缝合材料及粘合剂',null,null,-1);
INSERT INTO "i18n_message" VALUES (1575,'assetFunctionType','42', '6866医用高分子材料及制品',null,null,-1);
INSERT INTO "i18n_message" VALUES (1576,'assetFunctionType','43', '6870软件',null,null,-1);
INSERT INTO "i18n_message" VALUES (1577,'assetFunctionType','44', '6877介入器材',null,null,-1);


INSERT INTO "i18n_message" VALUES (1580,'caseType','1', '故障类型1',null,null,-1);
INSERT INTO "i18n_message" VALUES (1581,'caseType','2', '故障类型2',null,null,-1);
INSERT INTO "i18n_message" VALUES (1582,'caseType','3', '故障类型3',null,null,-1);
INSERT INTO "i18n_message" VALUES (1583,'caseType','4', '故障类型4',null,null,-1);
INSERT INTO "i18n_message" VALUES (1584,'caseType','5', '故障类型5',null,null,-1);

INSERT INTO "i18n_message" VALUES (1585,'caseSubType','1', '故障子类型1',null,null,-1);
INSERT INTO "i18n_message" VALUES (1586,'caseSubType','2', '故障子类型2',null,null,-1);
INSERT INTO "i18n_message" VALUES (1587,'caseSubType','3', '故障子类型3',null,null,-1);
INSERT INTO "i18n_message" VALUES (1588,'caseSubType','4', '故障子类型4',null,null,-1);
INSERT INTO "i18n_message" VALUES (1589,'caseSubType','5', '故障子类型5',null,null,-1);

INSERT INTO "i18n_message" VALUES (1595,'assetFunctionGrade','1', '功能等级1',null,null,-1);
INSERT INTO "i18n_message" VALUES (1596,'assetFunctionGrade','2', '功能等级2',null,null,-1);
INSERT INTO "i18n_message" VALUES (1597,'assetFunctionGrade','3', '功能等级3',null,null,-1);
INSERT INTO "i18n_message" VALUES (1598,'assetFunctionGrade','4', '功能等级4',null,null,-1);
INSERT INTO "i18n_message" VALUES (1599,'assetFunctionGrade','5', '功能等级5',null,null,-1);

INSERT INTO "i18n_message" VALUES (1601,'ownAssetGroup','1', '所属设备类型1',null,null,-1);
INSERT INTO "i18n_message" VALUES (1602,'ownAssetGroup','2', '所属设备类型2',null,null,-1);
INSERT INTO "i18n_message" VALUES (1603,'ownAssetGroup','3', '所属设备类型3',null,null,-1);
INSERT INTO "i18n_message" VALUES (1604,'ownAssetGroup','4', '所属设备类型4',null,null,-1);
INSERT INTO "i18n_message" VALUES (1605,'ownAssetGroup','5', '所属设备类型5',null,null,-1);

INSERT INTO "i18n_message" VALUES (1611,'inspectionPeriod','1', '每日',null,null,-1);
INSERT INTO "i18n_message" VALUES (1612,'inspectionPeriod','2', '每周',null,null,-1);
INSERT INTO "i18n_message" VALUES (1613,'inspectionPeriod','3', '每两周',null,null,-1);
INSERT INTO "i18n_message" VALUES (1614,'inspectionPeriod','4', '每月',null,null,-1);
INSERT INTO "i18n_message" VALUES (1615,'inspectionPeriod','5', '每季度',null,null,-1);

INSERT INTO "i18n_message" VALUES (1621,'depreciationMethodList','1', '折旧算法1',null,null,-1);
INSERT INTO "i18n_message" VALUES (1622,'depreciationMethodList','2', '折旧算法2',null,null,-1);
INSERT INTO "i18n_message" VALUES (1623,'depreciationMethodList','3', '折旧算法3',null,null,-1);
INSERT INTO "i18n_message" VALUES (1624,'depreciationMethodList','4', '折旧算法4',null,null,-1);
INSERT INTO "i18n_message" VALUES (1625,'depreciationMethodList','5', '折旧算法5',null,null,-1);



-- module's messages  (id from 3000)
INSERT INTO "i18n_message" VALUES (3000,'message','DeleteConformation', '删除确认','Delete Confirmation',null,-1);
INSERT INTO "i18n_message" VALUES (3001,'message','DeleteConformationMsg', '您确定要删除选定的记录么?','Are you sure to delete selected record?',null,-1);
INSERT INTO "i18n_message" VALUES (3002,'message','devicePhoto', '设备照片文件','device Photo',null,-1);
INSERT INTO "i18n_message" VALUES (3003,'message','deviceContract', '设备合同文件','deviceContract',null,-1);
INSERT INTO "i18n_message" VALUES (3004,'message','noAssetSelected', '请选择一个设备','Please select an asset',null,-1);
INSERT INTO "i18n_message" VALUES (3005,'message','searchAssetByDept', '按科室选设备','Select By Department',null,-1);
INSERT INTO "i18n_message" VALUES (3006,'message','searchAsset', '高级查询','Advanced Search',null,-1);
INSERT INTO "i18n_message" VALUES (3007,'message','selectAssetOwner', '请选择责任人...','Select Asset Owner...',null,-1);
INSERT INTO "i18n_message" VALUES (3008,'message','TransferOrder', '转单','Transfer To..',null,-1);
INSERT INTO "i18n_message" VALUES (3009,'message','noWorkOrderSelected', '请选择一个工单','Please select a Work Order.',null,-1);
INSERT INTO "i18n_message" VALUES (3010,'message','ProcessWorkOrder', '处理工单','Process Work Order',null,-1);
INSERT INTO "i18n_message" VALUES (3011,'message','shouldEarly', '{0}应该早于{1}!','{0} should be earlier than {1}!',null,-1);
INSERT INTO "i18n_message" VALUES (3012,'message','shouldLate', '{0}应该晚于{1}!','{0} should be later than {1}!',null,-1);
INSERT INTO "i18n_message" VALUES (3013,'message','todayDate', '当前日期',' current date',null,-1);
INSERT INTO "i18n_message" VALUES (3014,'message','Hospital', '院区','Hospital',null,-1);
INSERT INTO "i18n_message" VALUES (3015,'message','Department', '科室','Department',null,-1);
INSERT INTO "i18n_message" VALUES (3016,'message','DeleteAllConformation', '您确定要删除所有的自定义配置么？','Are you sure to delete all config created by yourself?',null,-1);


-- Chart label/lengend （id from 5000 to 5500）
INSERT INTO "i18n_message" VALUES (5000,'label','deviceScanhd', '设备扫描量(次)','Device Scan (Times)',null,-1);
INSERT INTO "i18n_message" VALUES (5001,'label','deviceScanlg', '扫描次数','Device Scan',null,-1);
INSERT INTO "i18n_message" VALUES (5002,'label','deviceExpohd', '设备曝光量(小时)','Device Exposure (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5003,'label','deviceExpolg_1', '曝光时长','Exposure',null,-1);
INSERT INTO "i18n_message" VALUES (5004,'label','deviceExpolg_2', '基准曝光时长','Device Scan (Baseline)',null,-1);
INSERT INTO "i18n_message" VALUES (5005,'label','deviceROIhd', '设备投资回报（元）','Device ROI (CNY）',null,-1);
INSERT INTO "i18n_message" VALUES (5006,'label','deviceROIlg_1', '收入','Revenue',null,-1);
INSERT INTO "i18n_message" VALUES (5007,'label','deviceROIlg_2', '利润','Profit',null,-1);
INSERT INTO "i18n_message" VALUES (5008,'label','countlb', '次','Count',null,-1);
INSERT INTO "i18n_message" VALUES (5009,'label','hourslb', '小时','Hours',null,-1);
INSERT INTO "i18n_message" VALUES (5010,'label','CNYlb', '元','CNY',null,-1);
INSERT INTO "i18n_message" VALUES (5011,'label','deviceUsagehd', '设备使用 (小时)','Device Usage',null,-1);
INSERT INTO "i18n_message" VALUES (5012,'label','deviceUsagelg_1', '使用','In-Use',null,-1);
INSERT INTO "i18n_message" VALUES (5013,'label','deviceUsagelg_2', '空闲','Idle',null,-1);
INSERT INTO "i18n_message" VALUES (5014,'label','deviceDThd', '设备停机率','Device Downtime',null,-1);
INSERT INTO "i18n_message" VALUES (5015,'label','deviceDTlg_1', '停机率','Downtime',null,-1);
INSERT INTO "i18n_message" VALUES (5016,'label','deviceDTlg_2', '基准停机率','Downtime (Baseline)',null,-1);
INSERT INTO "i18n_message" VALUES (5017,'label','pctlb', '百分比','Percent',null,-1);
INSERT INTO "i18n_message" VALUES (5018,'label','devicelb', '设备','Device',null,-1);
INSERT INTO "i18n_message" VALUES (5019,'label','deviceUsageStat_1', '设备总等待时间 (小时)','In-use (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5020,'label','deviceUsageStat_2', '设备总使用时间 (小时)','Idle (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5021,'label','deviceUsageStat_3', '设备总停机时间 (小时)','Downtime (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5022,'label','deviceScanStat', '设备总扫描次数 (次)','Device Scan (Times)',null,-1);
INSERT INTO "i18n_message" VALUES (5023,'label','deviceExpoStat', '设备总曝光时间 (小时)','Device Exposure (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5025,'label','deviceDTlg_3', '停机','Downtime',null,-1);
INSERT INTO "i18n_message" VALUES (5026,'label','devicePerfhd', '设备绩效分析','Device Performance',null,-1);
INSERT INTO "i18n_message" VALUES (5027,'label','assetTopPerfhd', '利润最高设备','Top Asset',null,-1);
INSERT INTO "i18n_message" VALUES (5028,'label','deptTopPerfhd', '利润最高科室','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5029,'label','assetsDashboardhd', '设备绩效分析汇总','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5030,'label','assetsDashboardclm1', '资产名称','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5031,'label','assetsDashboardclm2', '资产规格','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5032,'label','assetsDashboardclm3', '序列号','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5033,'label','assetsDashboardclm4', '所在科室','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5034,'label','assetsDashboardclm5', '收入（元）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5035,'label','assetsDashboardclm6', '运营成本（元）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5036,'label','assetsDashboardclm7', '利润（元）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5037,'label','assetsDashboardclm7', '扫描量（次）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5038,'label','assetsDashboardclm7', '曝光量（小时）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5039,'label','assetsDashboardclm7', '维修（次）','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5040,'label','assetsDashboardclm7', '停机时长（小时） ','Top Department',null,-1);
INSERT INTO "i18n_message" VALUES (5041,'label','deviceValueScanhd', '设备总扫描量(次)','Device Scan (Times)',null,-1);
INSERT INTO "i18n_message" VALUES (5042,'label','deviceValueExpohd', '设备总曝光量(小时)','Device Exposure (Hours)',null,-1);
INSERT INTO "i18n_message" VALUES (5043,'label','deviceValueROIhd', '设备总利润（元）','Device ROI （CNY)',null,-1);

-- device maintenance

INSERT INTO "i18n_message" VALUES (5501,'label','maintenanceAnalysis_title', '设备维修事件统计分析','Device Maintenance Analysis',null,-1);

INSERT INTO "i18n_message" VALUES (5506,'label','maintenanceAnalysis_empty', '未填','Unspecified',null,-1);
INSERT INTO "i18n_message" VALUES (5507,'label','maintenanceAnalysis_otherReason', '其他原因','Others',null,-1);
INSERT INTO "i18n_message" VALUES (5508,'label','maintenanceAnalysis_otherCategory', '其他设备类型','Others',null,-1);

INSERT INTO "i18n_message" VALUES (5511,'label','maintenanceAnalysis_reasonTile', '设备故障主要原因','Most Common Reason',null,-1);
INSERT INTO "i18n_message" VALUES (5512,'label','maintenanceAnalysis_stepTile', '设备故障处理流程最耗时步骤','Most Time-Consuming Step',null,-1);
INSERT INTO "i18n_message" VALUES (5513,'label','maintenanceAnalysis_roomTile', '设备故障主要发生的科室','Most Frequent Room',null,-1);
INSERT INTO "i18n_message" VALUES (5514,'label','maintenanceAnalysis_categoryTile', '设备故障主要发生的设备类型','Most Frequent Device',null,-1);
INSERT INTO "i18n_message" VALUES (5515,'label','maintenanceAnalysis_occurrenceTile', '设备故障数','Count',null,-1);
INSERT INTO "i18n_message" VALUES (5516,'label','maintenanceAnalysis_rankTile_room', '在科室中排名','Rank in Room',null,-1);
INSERT INTO "i18n_message" VALUES (5517,'label','maintenanceAnalysis_rankTile_category', '在同类设备中排名','Rank in Category',null,-1);
INSERT INTO "i18n_message" VALUES (5518,'label','maintenanceAnalysis_rankTile_device', '在所有设备中排名','Rank Overall',null,-1);

INSERT INTO "i18n_message" VALUES (5521,'label','maintenanceAnalysis_reasonChart', '设备故障原因分析','Reason Analysis',null,-1);
INSERT INTO "i18n_message" VALUES (5522,'label','maintenanceAnalysis_reasonChart_xAxis', '故障原因','Reason',null,-1);
INSERT INTO "i18n_message" VALUES (5523,'label','maintenanceAnalysis_reasonChart_yAxis', '故障次数','Occurrence',null,-1);

INSERT INTO "i18n_message" VALUES (5531,'label','maintenanceAnalysis_timeChart', '设备故障处理流程响应时间分布','Response Time Composition',null,-1);


INSERT INTO "i18n_message" VALUES (5532,'label','maintenanceAnalysis_timeChart_section', '耗时最长的三个步骤的具体响应时间分布','Time Distribution in 3 most time-consuming steps',null,-1);

INSERT INTO "i18n_message" VALUES (5533,'label','maintenanceAnalysis_timeChart_minute', '%s%d分钟','%s%d minute(s)',null,-1);
INSERT INTO "i18n_message" VALUES (5534,'label','maintenanceAnalysis_timeChart_hour', '%s%.1f小时','%s%.1f hour(s)',null,-1);

INSERT INTO "i18n_message" VALUES (5535,'label','maintenanceAnalysis_timeChart_legend_0', '未响应','No Response',null,-1);
INSERT INTO "i18n_message" VALUES (5536,'label','maintenanceAnalysis_timeChart_legend_1', '小于30分钟','Less Than 30 Minutes',null,-1);
INSERT INTO "i18n_message" VALUES (5537,'label','maintenanceAnalysis_timeChart_legend_2', '30分钟到1小时以内','30 Minutes to 1 Hour',null,-1);
INSERT INTO "i18n_message" VALUES (5538,'label','maintenanceAnalysis_timeChart_legend_3', '1小时到1天以内','1 Hour to 1 Day',null,-1);
INSERT INTO "i18n_message" VALUES (5539,'label','maintenanceAnalysis_timeChart_legend_4', '1天以上','Over 1 Day',null,-1);

INSERT INTO "i18n_message" VALUES (5541,'label','maintenanceAnalysis_distributionChart', '设备故障分布','Distribution',null,-1);
INSERT INTO "i18n_message" VALUES (5543,'label','maintenanceAnalysis_distributionChart_yAxis', '故障次数','Occurrence',null,-1);
INSERT INTO "i18n_message" VALUES (5544,'label','maintenanceAnalysis_distributionChart_room', '按科室','By Room',null,-1);
INSERT INTO "i18n_message" VALUES (5545,'label','maintenanceAnalysis_distributionChart_room_xAxis', '科室','Room',null,-1);
INSERT INTO "i18n_message" VALUES (5546,'label','maintenanceAnalysis_distributionChart_category', '按设备类型','By Category',null,-1);
INSERT INTO "i18n_message" VALUES (5547,'label','maintenanceAnalysis_distributionChart_category_xAxis', '设备类型','Category',null,-1);
INSERT INTO "i18n_message" VALUES (5548,'label','maintenanceAnalysis_distributionChart_device', '按单台设备（前40台）','By Device (Top 40)',null,-1);
INSERT INTO "i18n_message" VALUES (5549,'label','maintenanceAnalysis_distributionChart_device_xAxis', '设备','Device',null,-1);

INSERT INTO "i18n_message" VALUES (5551,'label','maintenanceAnalysis_percentageChart', '设备故障所占比例','Numbers',null,-1);
INSERT INTO "i18n_message" VALUES (5555,'label','maintenanceAnalysis_percentageChart_percentage', '所占比例：','Ratio: ',null,-1);
INSERT INTO "i18n_message" VALUES (5556,'label','maintenanceAnalysis_percentageChart_occurrence', '此台设备故障数：','This device: ',null,-1);
INSERT INTO "i18n_message" VALUES (5561,'label','maintenanceAnalysis_percentageChart_room', '此台设备故障占所在科室总故障比例','Ratio in the room',null,-1);
INSERT INTO "i18n_message" VALUES (5562,'label','maintenanceAnalysis_percentageChart_room_occurrence', '此台设备所在科室故障数：','The room: ',null,-1);
INSERT INTO "i18n_message" VALUES (5563,'label','maintenanceAnalysis_percentageChart_room_rank', '此台设备故障数在科室内排名：','Rank in the room: ',null,-1);
INSERT INTO "i18n_message" VALUES (5564,'label','maintenanceAnalysis_percentageChart_category', '此台设备占所在设备类型故障比例','Ratio in the category',null,-1);
INSERT INTO "i18n_message" VALUES (5565,'label','maintenanceAnalysis_percentageChart_category_occurrence', '此台设备所在类型故障数：','The category: ',null,-1);
INSERT INTO "i18n_message" VALUES (5566,'label','maintenanceAnalysis_percentageChart_category_rank', '此台设备故障数在此类设备中排名：','Rank in the category: ',null,-1);
INSERT INTO "i18n_message" VALUES (5567,'label','maintenanceAnalysis_percentageChart_device', '此台设备故障占所有设备故障比例','Ratio overall',null,-1);
INSERT INTO "i18n_message" VALUES (5568,'label','maintenanceAnalysis_percentageChart_device_occurrence', '所有设备故障数：','Overall: ',null,-1);
INSERT INTO "i18n_message" VALUES (5569,'label','maintenanceAnalysis_percentageChart_device_rank', '此台设备故障数在所有设备中排名：','Rank overall: ',null,-1);

-- device maintenance (preventive)

INSERT INTO "i18n_message" VALUES (5571,'label','preventiveMaintenanceAnalysis_title', '设备保养排期／跟踪／统计分析','Maintenance Schedule and Analysis',null,-1);

INSERT INTO "i18n_message" VALUES (5581,'label','preventiveMaintenanceAnalysis_planChart', 'PM统计表','Preventive Maintenance Schedule',null,-1);
INSERT INTO "i18n_message" VALUES (5582,'label','preventiveMaintenanceAnalysis_predicationChart', '设备维护预测','Preventive Maintenance Forecast',null,-1);

INSERT INTO "i18n_message" VALUES (5585,'label','preventiveMaintenanceAnalysis_year', '%d年','%dY',null,-1);
INSERT INTO "i18n_message" VALUES (5586,'label','preventiveMaintenanceAnalysis_month', '%d月','%dM',null,-1);
INSERT INTO "i18n_message" VALUES (5587,'label','preventiveMaintenanceAnalysis_allDevices', '全部设备','All',null,-1);
INSERT INTO "i18n_message" VALUES (5588,'label','preventiveMaintenanceAnalysis_deviceName', '设备名称','Name',null,-1);


-- Home pages start from 5601 to 5700

-- Hospital head home page
INSERT INTO "i18n_message" VALUES (5601,'label','assetTotalProfit', '设备总利润（单位：元）','Asset Total Profit',null,-1);
INSERT INTO "i18n_message" VALUES (5602,'label','assetTotalProfitForecast', '设备总利润预测（单位：元）','Asset Total Profit Forecast',null,-1);
INSERT INTO "i18n_message" VALUES (5603,'label','assetAveROI', '设备平均投资回报（单位：元）','Asset Ave ROI',null,-1);
INSERT INTO "i18n_message" VALUES (5604,'label','assetROI', '设备投资回报','Asset ROI',null,-1);
INSERT INTO "i18n_message" VALUES (5605,'label','profitDistro', '利润科室分布（单位：元）','Profit Distribution by Department',null,-1);
INSERT INTO "i18n_message" VALUES (5606,'label','assetType', '设备类型','Asset Type',null,-1);

INSERT INTO "i18n_message" VALUES (5607,'clinicalDeptId','1', '超声诊断室','Radiation Dept',null,-1);
INSERT INTO "i18n_message" VALUES (5608,'clinicalDeptId','2', '肿瘤中心','Tumour Dept',null,-1);
INSERT INTO "i18n_message" VALUES (5609,'clinicalDeptId','3', '心超室','Heart Ultrasonic Dept',null,-1);
INSERT INTO "i18n_message" VALUES (5610,'clinicalDeptId','4', '放射科','Ultrasonic Dept',null,-1);
INSERT INTO "i18n_message" VALUES (5611,'clinicalDeptId','5', '心导管室','Heart Dept',null,-1);

INSERT INTO "i18n_message" VALUES (5612,'label','assetAnnual', '设备绩效年报（单位：元）','Asset Performance Annual Report',null,-1);
INSERT INTO "i18n_message" VALUES (5613,'label','assetAnnualForecast', '设备绩效年报预测（单位：元）','Asset Performance Forecast Report',null,-1);


-- update id sequence value
SELECT setval('"public"."i18n_message_id_seq"', 10000, false);
