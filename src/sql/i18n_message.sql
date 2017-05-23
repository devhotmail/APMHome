truncate table i18n_message;

-- common i18n messages ( id from 1 to  199)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1, 'label', 'logout', '注销', 'Logout', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (2, 'label', 'login', '登录系统', 'Login', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3, 'label', 'login_name', '登录用户名', 'Login Name', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (4, 'label', 'password', '密码', 'Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5, 'label', 'home', '我的主页', 'Home', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (6, 'label', 'True2Yes', '是', 'Yes', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (7, 'label', 'False2No', '否', 'No', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (8, 'label', 'ValidationRequire', '输入不可为空！', 'Input is required!', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9, 'label', 'login_error', '用户名或密码错误', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (10, 'label', 'security_error', '用户名或密码错误', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (11, 'label', 'session_expired', '您的会话已经超时，请重新登录.', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (12, 'label', 'PersistenceErrorOccured', '保存数据出错.', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (13, 'label', 'PersistenceErrorDuplicateKey', '输入的数据与已有的记录重复.', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (14, 'label', 'Add', '添加', 'Add', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (15, 'label', 'Create', '新增', 'Create', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (16, 'label', 'View', '查看', 'View', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (17, 'label', 'Edit', '修改', 'Edit', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (18, 'label', 'Delete', '删除', 'Delete', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (19, 'label', 'Close', '关闭', 'Close', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (20, 'label', 'Cancel', '取消', 'Cancel', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (21, 'label', 'Return', '返回', 'Return', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (22, 'label', 'Save', '保存', 'Save', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (23, 'label', 'Submit', '提交', 'Submit', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (24, 'label', 'Confirm', '确认', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (25, 'label', 'Refresh', '刷新', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (26, 'label', 'AssignRoles', '设置角色权限', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (27, 'label', 'SelectOneMessage', '请选择...', 'Select....', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (28, 'label', 'NewPassword', '请输入新密码', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (29, 'label', 'ConfirmNewPassword', '请再次输入新密码', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (30, 'label', 'deleteConfirmation', '您确信要删除此条数据么？', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (31, 'label', 'Created', '已经成功地被创建。', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (32, 'label', 'Updated', '已经成功地被修改。', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (33, 'label', 'Deleted', '已经成功地被删除。', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (34, 'label', 'noRecordFound', '(没有查询到数据)', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (35, 'label', 'recordCount', '总计', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (36, 'label', 'true', '是', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (37, 'label', 'false', '否', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (38, 'label', 'passwordIncorrect', '对不起，您输入的密码不正确', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (39, 'label', 'security_bad_credentials', '用户名或密码错误', 'incorrect user name or password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (40, 'label', 'RowsPerPage', '每页行数', 'Rows/Page', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (41, 'label', 'AssignRole', '角色设置', 'Assign Role', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (42, 'label', 'ChangePassword', '修改密码', 'Change Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (43, 'label', 'ResetPassword', '重置密码', 'Reset Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (44, 'label', 'exportToXLS', '导出到Excel', 'Export To Excel', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (45, 'label', 'List', '列表', 'List', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (46, 'label', 'Download', '下载', 'Download', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (47, 'label', 'Search', '查询', 'Search', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (48, 'label', 'Reset', '重置', 'Reset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (49, 'label','Finish', '完成','Finish ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (50, 'label','Actions', '操作','Actions ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (51, 'label','DataSavedOK', '数据已经保存成功.','data saved successfully ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (52, 'label','Welcome', '欢迎您','Welcome',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (53, 'label','DuplicateLoginName', '登录用户名 "%s" 已有人使用，请更换登录用户名.','Login name already used by others, please change to another login name.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (54, 'label','BatchImport', '批量导入系统配置','BatchImport',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (55, 'label','ClearAll', '清空自定义配置','ClearAll',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (56,'message','Http500ErrorTitle', '系统发生异常','Exception Occured',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (57,'message','Http500ErrorMsg', '请联系系统管理员.','Please contact system administrator.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (58,'message','Http404NotFoundTitle', '网页不存在','Page Not Found',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (59,'message','Http404NotFoundMsg', '您想访问的网页不存在.','The resource you are looking for does not exist.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (60,'message','Http401AccessDeniedTitle', '权限受限','Access Denied',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (61,'message','Http401AccessDeniedMsg', '对不起，您未获得访问此网页的授权.','You are not authorized to access this resource.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (62, 'message','UserRole', '用户权限','User Role',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (63, 'label','Upload', '上传','Upload',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (65, 'label','All', '全部','All',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (66, 'label','From', '从','From',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (67, 'label','To', '到','To',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (68, 'label','Execute', '执行','Execute',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (69, 'label','DuplicateSiteName', 'SiteAdmin名称 "%s" 已有人使用，请更换租户名称.','Site name already used by others, please change to another site name.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (70, 'label','invalidEmail', 'Email: 不是有效的Email地址.','Invalid Email Address.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (71, 'label','DuplicateMsgKey', '编码 "%s" 已存在,请更换其他名称','the code you seted has been used,please change to another one',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (72, 'message','PasswordNotMatch', '输入的密码不匹配','Password not match',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (73, 'label', 'resetPassword', '重置密码', 'Reset Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (74, 'label', 'continue', '继续', 'Continue', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (75, 'label', 'QRCode', '二维码', 'QRCode', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (76, 'label', 'TerminatedAsset', '报废资产', 'Terminated Asset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (77, 'label', 'QRCodeMgmt', '二维码管理', 'Asset Code', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (78, 'label', 'GenerateQRCodes', '生成二维码', 'Generate Code', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (79, 'label', 'QRCodeRefresh', '换一批', 'Refresh QRCode', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (80, 'label', 'GetQRCode', '扫码成功', 'Got QRCode', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (81, 'label', 'FoundAsset', '找到设备', 'Found asset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (82, 'label', 'NotFoundAsset', '未找到设备', 'Cannot founud asset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (83, 'label', 'BindAsset', '绑定已有资产', 'Binding sset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (84, 'label', 'PhotoList', '照片列表', 'Photo List', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (85, 'label', 'ScanCode', '扫码', 'Scan Code', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (86, 'label', 'PictureUpload', '图片上传', 'Upload pictures', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (87, 'label', 'ContinueAdd', '继续添加', 'Add Next One', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (88, 'label', 'Bind', '绑定', 'Binding', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (89, 'label', 'OldPassword', '旧密码', 'Old Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (90, 'label', 'PleaseInput', '请输入', 'Please Input', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (91, 'label', 'NewPass', '新密码', 'New Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (92, 'label', 'ConfirmPass', '确认密码', 'Confirm Password', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (93, 'label', 'QrCodeLib', '二维码创建资产', 'QRCode Create Asset', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (94, 'label', 'issueDate', '发行日期', 'Issue Date', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (95, 'label', 'submitDate', '提交日期', 'Submit Date', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (96, 'label', 'submitWechatId', '提交人', 'Submit People', NULL, -1);


-- menu item names  ( id from 200 to  399)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (200, 'label', 'HomePage', '我的主页', 'Dashboard', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (201, 'label', 'HomePageHospitalHead', '我的主页(院长)', 'Dashboard(Hospital Head)', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (202, 'label', 'HomePageDeptHead', '我的主页(主任)', 'Dashboard(Department Head)', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (203, 'label', 'HomePageAssetHead', '我的主页(设备科)', 'Dashboard(Asset Head)', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (204, 'label', 'AssetMDM', '设备档案', 'Asset Master Data Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (205, 'label', 'AssetList', '基本信息', 'Asset List', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (206, 'label', 'AssetInventory', '设备盘点', 'Asset Inventory', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (207, 'label', 'AssetDocumentMgmt', '电子档案', 'Asset Document Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (208, 'label', 'AssetContractMgmt', '合同管理', 'Asset Contract Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (209, 'label', 'SupplierMgmt', '供应商管理', 'Supplier Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (210, 'label', 'AssetValueAnalysis', '设备价值统计', 'Asset Value Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (211, 'label', 'WorkOrderMgmt', '维修流程', 'Work Order Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (212, 'label', 'ServiceRequestMgmt', '报修管理', 'Service Request Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (213, 'label', 'MyWorkOrder', '报修处理', 'My Work Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (214, 'label', 'MaintainanceRecord', '维修记录', 'Repair Record', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (215, 'label', 'ServiceQualityReview', '维修质量评估', 'Service Quality Review', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (216, 'label', 'Inspection', '巡检管理', 'Inspection', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (217, 'label', 'DeviceMonitor', '设备监控', 'Asset Monitor', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (218, 'label', 'DeviceStatusMonitor', '状态监控', 'Asset Status Monitor', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (219, 'label', 'DeviceDowntimeAnalysis', '停机率分析', 'Device Downtime Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (220, 'label', 'DeviceMaintainanceEventAnalysis', '维修事件统计', 'Repair Event Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (221, 'label', 'DeviceMaintainanceCostAnalysis', '维护成本统计', 'Repair Cost Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (222, 'label', 'DeviceUtilizationAnalysis', '使用情况统计', 'Utilization Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (223, 'label', 'SparePartsConsumptionAnalysis', '备件消耗统计', 'Spare Parts Consumption', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (224, 'label', 'DeviceFailureAnalysis', '故障分类统计', 'Asset Failure Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (225, 'label', 'DeviceOperationMonitor', '运营监控', 'Operation Monitor', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (226, 'label', 'PreventiveMaintainceMgmt', '预防维护', 'Preventive Maint. Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (227, 'label', 'PreventiveMaintaincePlanning', '保养统计', 'Maintainance Report', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (258, 'label', 'PmRecord', '保养记录', 'Maintainance Record', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (228, 'label', 'DeviceMetrologyMgmt', '计量管理', 'Asset Metering Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (229, 'label', 'DeviceQualityControl', '质控管理', 'Asset Quality Control', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (230, 'label', 'DeviceAdverseEventMgmt', '不良事件上报', 'Adverse Event Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (231, 'label', 'DeviceMedicalAccidentMgmt', '医疗事故风险监控', 'Medical Accident Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (232, 'label', 'AssetKnowledgeBaseMgmt', '知识文档', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (233, 'label', 'AssetDocMgmt', '文档管理', 'Asset Documentation Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (234, 'label', 'AssetKMMgmt', '知识库管理', 'Asset KM Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (235, 'label', 'DeviceMgmtForum', '管理论坛', 'Asset Mgmt. Forum', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (236, 'label', 'DeviceApplication', '应用/维修培训管理', 'Asset Training Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (237, 'label', 'DeviceUpgradMgmt', '升级管理', 'DeviceUpgradMgmt', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (238, 'label', 'AdvancedDeviceInfoTracking', '扫描详情统计', 'Detailed Utilization Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (239, 'label', 'DeviceOperationKPIAnalysis', '绩效分析', 'Operation KPI Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (240, 'label', 'DeviceROIAnalysis', '平均投资回报分析', 'Asset ROI Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (241, 'label', 'DeviceKPIAnalysis', '绩效分析', 'Asset KPI Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (242, 'label', 'DeviceKPIForecast', '绩效预测', 'Asset KPI Forecast', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (243, 'label', 'DeviceCostAnalysis', '成本统计分析', 'Asset Cost Analysis', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (244, 'label', 'DevicePurchaseDecisionSupport', '辅助决策分析', 'Purchase Decision Support', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (245, 'label', 'SysAdmin', '系统配置', 'System Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (246, 'label', 'SiteAdmin', '租户管理', 'Site Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (247, 'label', 'OrgAdmin', '组织机构管理', 'Organization Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (248, 'label', 'UserAdmin', '用户管理', 'User Account Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (249, 'label', 'InspectionChecklist', '巡检配置', 'Inspection Checklist', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (250, 'label', 'InspectionOrder', '巡检计划', 'Inspection Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (251, 'label', 'InspectionRecord', '巡检记录', 'Inspection Record', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (252, 'label', 'ChecklistType', '检查类型', 'Checklist Type', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (253, 'label', 'Profile', '我的帐号', 'My Profile', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (254, 'label', 'Setting', '系统设置', 'Setting', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (255, 'label', 'BasicInfo', '基本信息', 'Basic Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (256, 'label', 'UsageAmount', '使用量', 'Usage Amount', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (257, 'label', 'Attachment', '附件', 'Attachment', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (259, 'label', 'ChecklistAdmin', '巡检/计量条目维护', 'Checklist Mgmt.', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (260, 'label', 'checkListSetting', '检查项配置', 'Checklist Configure', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (261, 'label', 'checkListItem', '检查项', 'Checklist Item', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (263, 'label', 'AssetStatusTimeChange', '资产状态与时间变更', 'Asset Status And Confirm Time Change', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (264, 'label', 'AssetContractAmount', '合同总额(元)', 'Asset Contract Amount', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (265, 'label', 'AssetContractAttachment', '合同附件', 'Asset Contract Attachment', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (266, 'label', 'AssetContractName', '合同名称', 'Asset Contract Name', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (267, 'label', 'SysCodeConfig', '系统编码配置', 'Sys Code Config', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (268, 'label', 'ItemConfig', '配置', 'Config', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (269, 'label', 'AssetOwnerSetting', '维修责任人设置', 'Asset owner setting', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (270, 'label', 'SettingOwner', '设置', 'Setting', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (271, 'label', 'AvailableAssets', '备选设备', 'Available Assets', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (272, 'label', 'ChoseAssets', '选中设备', 'Chose Assets', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (273, 'label', 'ChoosingAssets', '选择设备列表', 'Choosing Assets', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (274, 'label', 'FilterCondition', '过滤条件', 'Filter Conditions', NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (277, 'label', 'AssetTagMsgSubscriber', '设备标签关注', 'assetTagMsg Subscriber', NULL, -1);


-- Entity Object Names  ( id from 400 to  599)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (401, 'label', 'SiteInfo', '医联体/租户信息', 'Site Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (402, 'label', 'OrgInfo', '组织机构信息', 'Organization Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (403, 'label', 'UserAccount', '用户帐号信息', 'User Account Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (404, 'label', 'AssetInfo', '资产信息', 'Asset Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (405, 'label', 'WorkOrder', '维修工单', 'Work Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (406, 'label', 'WorkOrderStep', '工单步骤', 'Work Order Steps', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (407, 'label', 'WorkOrderStepDetail', '工单详情', 'Work Order Step Details', NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (409, 'label', 'AssetClinicalRecord', '检查记录', 'Work Order Step Details', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (412, 'label', 'InspectionOrderDetail', '巡检工单详情', 'Inspection Order Detail', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (413, 'label', 'Supplier', '供应商信息', 'Supplier', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (414, 'label', 'InspectionWorkOrder', '巡检工单', 'Inspection Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (415, 'label', 'MetrologWorkOrder', '计量工单', 'Inspection Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (416, 'label', 'QualityCtrlWorkOrder', '质控工单', 'Inspection Order', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (417,'label','DeviceCheck', '设备盘点','device check',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (418, 'label', 'MetrologyOrderDetail', '计量工单详情', 'Metrology Order Detail', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (419, 'label', 'QualityCtrlOrderDetail', '质控工单详情', 'Quality Control Order Detail', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (420, 'label', 'QualityCtrlMonitor', '质量监控', 'QualityCtrl Monitor', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (421, 'label', 'MetrologyMonitor', '计量监控', 'Metrology Monitor', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (422, 'label', 'saveOrder', '保存顺序', 'Save sequence', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (423, 'label', 'I18nMessage', '编码类型配置', 'I18nMessage', NULL, -1);

-- Entity Object field Names  (id from 600 to  1499)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (600,'field_name','accessory','备件','Accessory',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (601,'field_name','accessoryPrice','单价(元)','Accessory Price',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (602,'field_name','accessoryQuantity','数量','Accessory Quantity',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (603,'field_name','address','地址','Address',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (604,'field_name','aliasName','别名','Alias Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (605,'field_name','arriveDate','到货日期','Arrive Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (606,'field_name','assetDeptId','所属设备科','Asset Dept Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (607,'field_name','functionType','设备型号','Function Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (608,'field_name','assetId','设备ID','Asset Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (609,'field_name','assetOwnerId','维修责任人','Asset Owner Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (610,'field_name','assetOwnerName','维修责任人','Asset Owner Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (611,'field_name','assetOwnerTel','维修责任人电话','Asset Owner Tel',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (612,'field_name','attachmentUrl','上传附件','Attachment Url',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (613,'field_name','barcode','条码','Barcode',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (614,'field_name','caseOwnerId','负责人','Case Owner Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (615,'field_name','caseOwnerName','负责人','Case Owner Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (616,'field_name','casePriority','紧急度','Case Priority',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (617,'field_name','caseSubType','故障子类别','Case Sub Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (618,'field_name','caseType','故障类别','Case Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (619,'field_name','city','城市','City',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (620,'field_name','clinicalDeptId','使用科室','Clinical Dept Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (621,'field_name','clinicalDeptName','使用科室','Clinical Dept Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (622,'field_name','closeReason','关单原因','Close Reason',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (623,'field_name','comments','备注','Comments',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (624,'field_name','confirmedEndTime','确认的恢复可用时间','Confirmed End Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (625,'field_name','confirmedStartTime','确认的停机时间','Confirmed Start Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (626,'field_name','contactor','联系人姓名','Contactor',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (627,'field_name','createTime','创建时间','Create Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (628,'field_name','nextStep','下一步','Next Step',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (629,'field_name','creatorName','创建者','Creator Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (630,'field_name','taskOwner','处理人','Task Owner',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (631,'field_name','currentPersonName','当前处理人','Current Person Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (632,'field_name','currentStep','当前处理步骤','Current Step',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (633,'field_name','departNum','设备编号（院方）','Depart Num',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (634,'field_name','depreciationMethod','折旧算法','Depreciation Method',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (635,'field_name','deptId','巡检科室','Dept Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (636,'field_name','deptName','巡检科室','Dept Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (637,'field_name','description','处理描述','Description',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (638,'field_name','endTime','结束时间','End Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (639,'field_name','examDate','检查日期','Exam Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (640,'field_name','examEndTime','结束时间','Exam End Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (641,'field_name','examStartTime','开始时间','Exam Start Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (642,'field_name','exposeCount','曝光量','Expose Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (643,'field_name','fileType','文件类型','File Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (644,'field_name','fileUrl','文件路径','File Url',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (645,'field_name','filmCount','胶片量','Film Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (646,'field_name','financingNum','资产编号','Financing Num',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (647,'field_name','functionGrade','功能等级','Function Grade',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (648,'field_name','functionGroup','功能分组','Function Group',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (649,'field_name','assetGroup','设备类型','Asset Group',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (650,'field_name','hospitalId','所属院区','Hospital Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (651,'field_name','injectCount','注射量','Inject Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (652,'field_name','installDate','安装日期','Install Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (653,'field_name','isClosed','已关单？','Is Closed',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (654,'field_name','isFinished','完成？','Is Finished',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (655,'field_name','intExtType','工单类型','Int Ext Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (656,'field_name','isPassed','通过?','Is Passed',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (657,'field_name','isValid','在用？','Is Valid',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (658,'field_name','item','巡检项目','Item',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (659,'field_name','itemId','巡检项目','Item Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (660,'field_name','itemName','巡检项目','Item Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (661,'field_name','lifecycle','使用年限','Lifecycle',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (662,'field_name','locationCode','安装位置编码','Location Code',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (663,'field_name','locationName','安装位置','Location Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (664,'field_name','maitanance','维修商','Maitanance',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (665,'field_name','maitananceTel','维修电话','Maitanance Tel',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (666,'field_name','manHours','内部工时(小时)','Man Hours',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (667,'field_name','manufactDate','出厂日期','Manufact Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (668,'field_name','manufacture','制造商','Manufacture',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (669,'field_name','modalityId','院内IT系统编号','Modality Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (670,'field_name','modalityType','设备类型','Modality Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (671,'field_name','modalityTypeId','设备类型','Modality Type Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (672,'field_name','name','名称','Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (673,'field_name','nextTime','下次计划时间','Next Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (674,'field_name','ownerId','执行人','Owner Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (675,'field_name','ownerName','责任人','Owner Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (676,'field_name','ownerOrgId','执行人所属部门','Owner Org Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (677,'field_name','ownerOrgName','执行人所属部门','Owner Org Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (678,'field_name','paperUrl','附件','Paper Url',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (679,'field_name','patientAge','年龄','Patient Age',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (680,'field_name','patientGender','性别','Patient Gender',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (681,'field_name','patientId','患者信息','Patient Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (682,'field_name','patientNameEn','姓名','Patient Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (683,'field_name','patientNameZh','姓名','Patient Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (684,'field_name','planDate','计划内最近一次维护日期','Plan Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (685,'field_name','priceAmount','价格','Price Amount',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (686,'field_name','priceUnit','单位','Price Unit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (687,'field_name','procedureId','检查部位','Procedure Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (688,'field_name','procedureName','检查部位','Procedure Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (689,'field_name','procedureStepId','检查步骤','Procedure Step Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (690,'field_name','procedureStepName','检查步骤','Procedure Step Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (691,'field_name','purchaseDate','购买日期','Purchase Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (692,'field_name','purchasePrice','采购价格(元)','Purchase Price',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (693,'field_name','reportUrl','维护报告','Report Url',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (694,'field_name','requestReason','故障现象','Request Reason',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (695,'field_name','requestTime','报修时间','Request Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (696,'field_name','requestorId','报修人','Requestor Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (697,'field_name','requestorName','报修人','Requestor Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (698,'field_name','salvageValue','最终残值(元)','Salvage Value',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (699,'field_name','serialNum','序列号(厂商SN)','Serial Num',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (700,'field_name','startTime','开始时间','Start Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (701,'field_name','status','当前状态','Status',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (702,'field_name','stepName','步骤名称','Step Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (703,'field_name','tel','电话','Tel',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (704,'field_name','terminateDate','报废日期','Terminate Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (705,'field_name','totalManHour','总计人工(小时)','Total Man Hour',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (706,'field_name','totalPrice','总计费用(元)','Total Price',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (707,'field_name','updateDetail','变更内容描述','Update Detail',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (708,'field_name','updatePersonId','变更人','Update Person Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (709,'field_name','updatePersonName','变更人','Update Person Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (710,'field_name','updateTime','处理时间','Update Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (711,'field_name','vendor','供应商','Vendor',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (712,'field_name','warrantyDate','保修截止日期','Warranty Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (713,'field_name','zipcode','邮编','Zipcode',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (714,'field_name','contactEmail','Email','Contact Email',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (715,'field_name','contactPerson','联系人','Contact Person',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (716,'field_name','contactPhone','联系电话','Contact Phone',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (717,'field_name','defaultLang','默认语言','Default Lang',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (718,'field_name','email','Email','Email',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (719,'field_name','homePage','我的主页','Home Page',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (720,'field_name','isActive','激活?','Active?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (721,'field_name','isEnabled','启用?','Enabled?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (722,'field_name','isLocalAdmin','本院管理员','Local Admin?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (723,'field_name','isOnline','在线?','Online?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (724,'field_name','isSiteAdmin','Site管理员?','Site Admin?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (725,'field_name','isSuperAdmin','超级管理员?','Super Admin?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (726,'field_name','lastLoginTime','上次登录时间','Last Login Time',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (727,'field_name','location','地点','Location',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (728,'field_name','locationEn','地点','Location En',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (729,'field_name','nameEn','名称(En)','Name En',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (730,'field_name','orgId','所属部门','Org Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (731,'field_name','parentId','上级部门','Parent Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (732,'field_name','roleDesc','角色描述','Role Desc',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (733,'field_name','roleId','角色','Role Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (734,'field_name','telephone','电话','Telephone',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (735,'field_name','timeZone','时区','Time Zone',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (736,'field_name','siteDescription','详细描述','Description',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (737,'field_name','userName','姓名','Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (738,'field_name','workOrderName','工单名称','work Order Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (739,'field_name','lastPmDate','上次维护时间','Last Maintainance Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (740,'field_name','lastQaDate','上次质控时间','Last Quality Control Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (741,'field_name','lastMeteringDate','上次计量时间','Last Metering Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (742,'field_name','assetName','资产名称','Asset Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (743,'field_name','period','循环周期','Period',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (744,'field_name','wfAutoStep2', '自动审核','Auto Approve',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (745,'field_name','wfAutoStep3', '自动派工','Auto Assign',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (746,'field_name','wfAutoStep4', '自动接单','Auto Accept',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (747,'field_name','wfAutoStep5', '自动维修','Auto Repair',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (748,'field_name','wfAutoStep6', '自动关单','Auto Close',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (749,'field_name','PmOrder', '保养记录','preventive order',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (750,'field_name','msgType', '编码类型','msg type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (751,'field_name','msgKey', '编码值','Code Value',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (752,'field_name','msgValue', '编码名称','Code name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (753,'field_name','displayOrder', '执行顺序','Display order',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (754,'field_name','cyclicStartDate', '循环开始时间','Cyclic Start Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (755,'field_name','cyclicEndDate', '循环结束时间','Cyclic End Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (756,'field_name','risProcedureName', 'ris部位名称','ris precedure name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (757,'field_name','apmProcedureName', 'apm部位名称','apm precedure name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (758,'field_name','AttachedFileInfo', '附件信息','Attachment File Info',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (759,'field_name','otherExpense', '其他费用(元)','Other Expense(RMB)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (760,'field_name','manhourPrice', '内部工时单价(元)','Manhour Price(RMB)',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (761,'field_name','clinicalOwnerId', '使用责任人','Clinical Dept Owner',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (762,'field_name','clinicalOwnerName', '使用责任人','Clinical Dept Owner',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (763,'field_name','clinicalOwnerTel', '使用责任人电话','Clinical Dept Owner Tel',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (764,'field_name','registrationNo', '注册证号','Registration No',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (765,'field_name','factoryWarrantyDate', '质保日期','Manhour Price(RMB)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (766,'field_name','contractType', '合同类型','Contract Type',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (767,'field_name','assetOwnerId2','维修责任人2','2nd Asset Owner Id',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (768,'field_name','assetOwnerName2','维修责任人2','2nd Asset Owner Name',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (769,'field_name','assetOwnerTel2','维修责任人2电话','2nd Asset Owner Tel',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (770,'field_name','eamId','EAM系统编号','EAM ID',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (771,'field_name','systemId','厂商System Id','System ID',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (772,'field_name','isTakeOrderEnabled','允许抢单?','Compte For Order Enabled',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (773, 'label', 'TerminateAssetInfo', '报废资产信息', 'terminate Asset Info', NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (774, 'label', 'ActiveAsset', '激活资产', 'active asset', NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (775, 'label', 'InactiveAsset', '报废资产', 'inactive asset', NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (776, 'label', 'BuildAsset', '资产建档', 'build asset', NULL, -1);


-- field value code types  ( id from 1400)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1400,'assetGroup','1','CT类','CT类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1401,'assetGroup','2','磁共振MRI类','磁共振MRI类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1402,'assetGroup','3','血管造影机(DSA)','血管造影机(DSA)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1403,'assetGroup','4','普放类(XR)','普放类(XR)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1404,'assetGroup','5','超声影像类(US)','超声影像类(US)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1405,'assetGroup','6','核医学类(NW/PET)','核医学类(NW/PET)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1406,'assetGroup','7','放疗类','放疗类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1407,'assetGroup','8','监护类','监护类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1408,'assetGroup','9','呼吸类','呼吸类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1409,'assetGroup','10','麻醉类','麻醉类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1410,'assetGroup','11','软式内窥镜类','软式内窥镜类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1411,'assetGroup','12','硬式内窥镜类','硬式内窥镜类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1412,'assetGroup','13','血液净化类','血液净化类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1413,'assetGroup','14','电刀、超声刀','电刀、超声刀',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1414,'assetGroup','15','医用激光类','医用激光类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1415,'assetGroup','16','输注泵类','输注泵类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1416,'assetGroup','17','手术室设备类','手术室设备类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1417,'assetGroup','18','消毒设备类','消毒设备类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1418,'assetGroup','19','检验室设备类','检验室设备类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1419,'assetGroup','20','病理类','病理类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1420,'assetGroup','21','口腔科设备类','口腔科设备类',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES(1421,'assetGroup','22','其它','其它',null,-1);

/*
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1400,'assetGroup','1','CT','CT',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1401,'assetGroup','2','MR','MR',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1402,'assetGroup','3','DR','DR',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1403,'assetGroup','4','CR','CR',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1404,'assetGroup','5','RF','RF',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1405,'assetGroup','6','DSA','DSA',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1406,'assetGroup','7','乳腺机','乳腺机',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1407,'assetGroup','8','PET','PET',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1408,'assetGroup','9','NM','NM',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1409,'assetGroup','10','PET-CT','PET-CT',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1410,'assetGroup','11','PET-MR','PET-MR',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1411,'assetGroup','12','US','US',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1412,'assetGroup','13','其它','Others',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1490,'contractType','1','采购合同','Purchase Contract',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1491,'contractType','2','维修合同','Maint Contract',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1492,'contractType','3','延保合同','延保合同',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1493,'contractType','4','移机合同','移机合同',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1494,'contractType','5','保养合同','保养合同',null,-1);
*/

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1510,'woSteps','1', '报修','Create',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1511,'woSteps','2', '派工','Assign',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1512,'woSteps','3', '接单','Accept',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1513,'woSteps','4', '维修','Repair',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1514,'woSteps','5', '关单','Closed',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1515,'woSteps','6', '反馈','Feedback',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1517,'assetStatus','1', '正常','Up',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1518,'assetStatus','2', '停机','Down',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1519,'assetStatus','3', '有异常','Partial',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1520,'checklistType','1','巡检','Inspection',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1521,'checklistType','2','计量','Metering',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1522,'checklistType','3','质控','Quality Control',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1523,'casePriority','1','紧急','Normal',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1524,'casePriority','2','重要','Important',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1525,'casePriority','3','一般','Urgent',null,-1);



INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1530,'attachmentType','1','照片','Photo',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1532,'attachmentType','2','用户手册','User Manual',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1533,'attachmentType','3','培训资料','Material for training',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1535,'assetFunctionType','2', '6801基础外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1536,'assetFunctionType','3', '6802显微外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1537,'assetFunctionType','4', '6803神经外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1538,'assetFunctionType','5', '6804眼科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1539,'assetFunctionType','6', '6805耳鼻喉科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1540,'assetFunctionType','7', '6806口腔科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1541,'assetFunctionType','8', '6807胸腔心血管外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1542,'assetFunctionType','9', '6808腹部外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1543,'assetFunctionType','10', '6809泌尿肛肠外科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1544,'assetFunctionType','11', '6810矫形外科（骨科）手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1545,'assetFunctionType','12', '6812妇产科用手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1546,'assetFunctionType','13', '6813计划生育手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1547,'assetFunctionType','14', '6815注射穿刺器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1548,'assetFunctionType','15', '6816烧伤(整形)科手术器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1549,'assetFunctionType','16', '6820普通诊察器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1550,'assetFunctionType','17', '6821医用电子仪器设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1551,'assetFunctionType','18', '6822医用光学器具、仪器及内窥镜设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1552,'assetFunctionType','19', '6823医用超声仪器及有关设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1553,'assetFunctionType','20', '6824医用激光仪器设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1554,'assetFunctionType','21', '6825医用高频仪器设备',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1555,'assetFunctionType','22', '6826物理治疗及康复设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1556,'assetFunctionType','23', '6827中医器械',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1557,'assetFunctionType','24', '6828医用磁共振设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1558,'assetFunctionType','25', '6830医用X射线设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1559,'assetFunctionType','26', '6831医用X射线附属设备及部件',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1560,'assetFunctionType','27', '6832医用高能射线设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1561,'assetFunctionType','28', '6833医用核素设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1562,'assetFunctionType','29', '6834医用射线防护用品、装置',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1563,'assetFunctionType','30', '6840临床检验分析仪器',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1564,'assetFunctionType','31', '6841医用化验和基础设备器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1565,'assetFunctionType','32', '6845体外循环及血液处理设备',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1566,'assetFunctionType','33', '6846植入材料和人工器官',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1567,'assetFunctionType','34', '6854手术室、急救室、诊疗室设备及器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1568,'assetFunctionType','35', '6855口腔科设备及器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1569,'assetFunctionType','36', '6856病房护理设备及器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1570,'assetFunctionType','37', '6857消毒和灭菌设备及器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1571,'assetFunctionType','38', '6858医用冷疗、低温、冷藏设备及器具',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1572,'assetFunctionType','39', '6863口腔科材料',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1573,'assetFunctionType','40', '6864医用卫生材料及敷料',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1574,'assetFunctionType','41', '6865医用缝合材料及粘合剂',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1575,'assetFunctionType','42', '6866医用高分子材料及制品',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1576,'assetFunctionType','43', '6870软件',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1577,'assetFunctionType','44', '6877介入器材',null,null,-1);

/*
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1580,'caseType','1', '设备老化',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1581,'caseType','2', '设备损坏',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1582,'caseType','3', '耗材损坏',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1583,'caseType','4', '保修过期',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1584,'caseType','5', '其他',null,null,-1);
*/

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1585,'caseSubType','1', '故障子类型1',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1586,'caseSubType','2', '故障子类型2',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1587,'caseSubType','3', '故障子类型3',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1588,'caseSubType','4', '故障子类型4',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1589,'caseSubType','5', '故障子类型5',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1595,'assetFunctionGrade','1', '等级1',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1596,'assetFunctionGrade','2', '等级2',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1597,'assetFunctionGrade','3', '等级3',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1601,'ownAssetGroup','1', '所属设备类型1',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1602,'ownAssetGroup','2', '所属设备类型2',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1603,'ownAssetGroup','3', '所属设备类型3',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1604,'ownAssetGroup','4', '所属设备类型4',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1605,'ownAssetGroup','5', '所属设备类型5',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1611,'inspectionPeriod','1', '每日',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1612,'inspectionPeriod','2', '每周',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1613,'inspectionPeriod','3', '每两周',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1614,'inspectionPeriod','4', '每月',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1615,'inspectionPeriod','5', '每季度',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1616,'inspectionPeriod','6', '每半年',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1617,'inspectionPeriod','7', '每年',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1621,'depreciationMethodList','1', '平均',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1622,'depreciationMethodList','2', '加速双倍余额',null,null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1623,'depreciationMethodList','3', '加速年限',null,null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1628, 'label', 'AssetInfoDetail', '设备详细信息', 'Asset Detail Info', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1629, 'label', 'AssetInventoryUnmatchedList', '与实物不符的设备列表', 'Asset Inventory Unmatched List', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1630, 'label', 'AssetInventoryDate', '设备盘点日期', 'Asset Inventory Date', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1631, 'label', 'DefaultHospitalName', '总部/院区', 'Hospital Name', NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1632, 'label', 'ForeighKeyErrorWithSiteId', '该租户不可删除: 请先删除其下面的组织机构信息', NULL, NULL, -1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1633, 'label', 'ForeighKeyErrorWithOrgId', '该组织机构不可删除: 请先删除其下面的组织机构或用户信息', NULL, NULL, -1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1634, 'label', 'TerminateAssetInfoDetail', '报废资产信息', 'terminate Asset Detail Info', NULL, -1);
--qr_code_lib status ( id from 1700)
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1700,'qrCodeLibStatus','1', '已发行','issued',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1701,'qrCodeLibStatus','2', '已上传','uploaded',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1702,'qrCodeLibStatus','3', '已建档','Documented ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1699,'qrCodeLibStatus','4', '已作废','Discard ',null,-1);

--work_order status 
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1703,'status','1', '在修','Fixing',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1704,'status','2', '完成','Closed',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1705,'status','3', '取消','Cancel',null,-1);

--work_order intExtType 
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1706,'intExtType','1', '内部','Internal',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1707,'intExtType','2', '外部','External',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (1708,'intExtType','3', '混合','Mixed',null,-1);

-- module's messages  (id from 3000)
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3000,'message','DeleteConformation', '删除确认','Delete Confirmation',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3001,'message','DeleteConformationMsg', '您确定要删除选定的记录么?','Are you sure to delete selected record?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3002,'message','devicePhoto', '设备照片文件','device Photo',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3003,'message','deviceContract', '设备合同文件','deviceContract',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3004,'message','noAssetSelected', '请选择一个设备','Please select an asset',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3005,'message','searchAssetByDept', '按科室选设备','Select By Department',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3006,'message','searchAsset', '高级查询','Advanced Search',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3007,'message','selectAssetOwner', '请选择责任人...','Select Asset Owner...',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3008,'message','TransferOrder', '转单','Transfer To..',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3009,'message','noWorkOrderSelected', '请选择一个工单','Please select a Work Order.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3010,'message','ProcessWorkOrder', '处理工单','Process Work Order',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3011,'message','shouldEarly', '{0}应该早于{1}!','{0} should be earlier than {1}!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3012,'message','shouldLate', '{0}应该晚于{1}!','{0} should be later than {1}!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3013,'message','todayDate', '当前日期',' current date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3014,'message','Hospital', '院区','Hospital',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3015,'message','Department', '科室','Department',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3017,'message','invalidSizeMessage', '文件内容不能大于','File size must less than ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3018,'message','remainingCharacters', '还有{0}字','{0} characters remaining.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3019,'message','fileTransFail', '文件传输失败','File transform Failure',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3020,'message','DeviceInventorySuccess', '设备盘点成功！','file inventory success',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3021,'message','I18nCode', '编码','i18n code',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3022,'message','noAvailableCheckListItem', '没有可用的检查项，点此进行配置','There is no available checklist item, please do setting firstly!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3023,'message','fileLimitMessage', '文件个数不能大于','File number must less than ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3024,'message','is uploaded.', '%s已上传.','%s is upload',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3025,'message','DeleteFileConformationMsg', '确认删除文件吗？',' Confirm to remove this file?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3026,'message','SelectUploadFile', '请选择上传文件','Please choose one file to upload!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3027,'message','SuccessUploaded', '上传成功',' is uploaded',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3028,'message','InvalidPictureFileType', '文件类型应该属于(gif,jpe,jpeg,png,bmp)','Invalid Picture File Type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3029,'message','WhenIsFinished', '任务完成时，%s不能为空.',' when the record is finished,%s cannot be null',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3030,'message','ResetConformation', '重置确认','Reset Confirmation',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3031,'message','ResetConformationMsg', '您确定要重置选定用户的密码么?','Are you sure to reset selected user password?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3032,'message','passwordResetTips', '您使用的是系统初始密码。为保证您的帐号安全，建议您立即修改密码.','Please change the initial password for safety.',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3033,'message','AllHospitals', '所有院区','All Hospitals',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3034,'message','ViewHistoryWorkOrder', '查看历史报修','View History Work Orders',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3035,'message','WarningForUpdateQRCode', '此设备已绑定二维码，继续此操作会更新编码','This Asset already has had a QRcode,Continue this operation will update the QRCode!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3036,'message','SuccessForBinding', '绑定设备成功!','Success For Binding!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3037,'message','ChooseAssets', '请选择设备!','Please choose assets first!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3038,'message','ChooseAssetsOwner', '请选择设备负责人!','Please choose assets owner first!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3039,'message','InvalidQRCode', '无效的二维码!','Invalid QRCode!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3040,'message','WrongHospitalQRCode', '非本院二维码!','Not for this Hospital!',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (3041,'message','AlreadyUsingQrCode', '已经使用中二维码!','Already using Code!',null,-1);

-- Chart label/lengend （id from 5000 to 5500）
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5000,'label','deviceScanhd', '设备扫描量（次）','Scan #',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5001,'label','deviceScanlg', '扫描次数','Scan',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5002,'label','deviceExpohd', '设备曝光量（次）','Exposure #',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5003,'label','deviceExpolg_1', '曝光量','Exposure',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5004,'label','deviceExpolg_2', '基准曝光量','Scan (Baseline)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5005,'label','deviceROIhd', '设备投资回报（元）','ROI (CNY）',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5006,'label','deviceROIlg_1', '收入','Revenue',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5007,'label','deviceROIlg_2', '利润','Profit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5008,'label','countlb', '次','Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5009,'label','hourslb', '小时','Hours',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5010,'label','CNYlb', '元','CNY',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5011,'label','deviceUsagehd', '设备使用（小时）','Usage',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5012,'label','deviceUsagelg_1', '使用','In-Use',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5013,'label','deviceUsagelg_2', '等待','Idle',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5014,'label','deviceDThd', '设备停机率（％）','Downtime (Percent)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5015,'label','deviceDTlg_1', '停机率','Downtime',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5016,'label','deviceDTlg_2', '基准停机率','Downtime (Baseline)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5017,'label','pctlb', '百分比','Percent',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5018,'label','devicelb', '设备','Device',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5019,'label','deviceUsageStat_1', '总等待时间','In-use (Hours)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5020,'label','deviceUsageStat_2', '总使用时间','Idle (Hours)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5021,'label','deviceUsageStat_3', '总停机时间','Downtime (Hours)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5022,'label','deviceScanStat', '总扫描量','Scan (Times)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5023,'label','deviceExpoStat', '总曝光量','Exposure (Times)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5025,'label','deviceDTlg_3', '停机','Downtime',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5026,'label','devicePerfhd', '设备绩效分析','Performance',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5027,'label','assetTopPerfhd', '利润最高设备类型','Top asset type',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5028,'label','deptTopPerfhd', '利润最高科室','Top department',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5029,'label','assetsDashboardhd', '设备绩效分析汇总','Asset KPI Detail List',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5030,'label','assetsDashboardclm1', '资产名称','Asset',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5031,'label','assetsDashboardclm4', '时间','Date',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5032,'label','assetsDashboardclm2','序列号','Serial Number',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5033,'label','assetsDashboardclm3', '所在科室','Department',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5034,'label','assetsDashboardclm5', '收入(元)','Revenue',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5035,'label','assetsDashboardclm6', '运营成本(元)','Cost',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5036,'label','assetsDashboardclm7', '利润(元)','Profit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5037,'label','assetsDashboardclm8', '扫描量(次)','Scan',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5038,'label','assetsDashboardclm9', '曝光量(次)','Exposure',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5039,'label','assetsDashboardclm10', '维修(次)','Maintainance',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5040,'label','assetsDashboardclm11', '累计停机时长(小时)','Downtime',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5041,'label','deviceValueScanhd', '总扫描量','Scan',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5042,'label','deviceValueExpohd', '总曝光量','Exposure',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5045,'label','selectAssetSingle', '选择单台设备','Select a device',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5046,'label','selectAssetChoose', '选择设备','Select devices',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5047,'label','WorkOrderConfig', '维修工单流程配置','WorkOrder Config',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5048,'label','deviceValueProfithd', '设备总利润','Profit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5049,'label','deviceValueRevenuehd', '设备总成本','Revenue',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5050,'label','deviceValueCosthd', '设备总成本','Cost',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5051,'label','groupByMonth', '月','Month',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5052,'label','groupByDay', '日','Day',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5053,'label','groupByYear', '年','Year',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5054,'label','checkIntervalNotice_1', '提示','Notice',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5055,'label','checkIntervalNotice_2', '选择范围控制最小1个月，最大3年','Search interval between 1 month and 3 years',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5056,'label','selectSearchInterval_1', '到','to',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5057,'label','selectSearchInterval_2', '确定','Submit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5058,'label','deviceUsagelg_3', '停机','Downtime',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5059,'label','friendlyTips', '友情提示','friendly tips',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5060,'dispatchMode','1','专人派工','specially-assigned',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5061,'dispatchMode','2','抢单','ask for ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5062,'dispatchMode','3','自动派工','automatic',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5063,'label','WorkFlowConfig','工作流配置','workflow config',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5064,'label','dispatchMode','派工模式','dispatch mode',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5065,'label','dispatchUserName','派工人','dispatch username',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5066,'label','timeoutDispatch','派工超时时间(分钟)','dispatch timeout',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5067,'label','timeoutAccept','接单超时时间(分钟)','accept timeout',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5068,'label','timeoutRepair','维修超时时间(分钟)','repair timeout',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5069,'label','timeoutClose','关单超时时间(分钟)','close timeout',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5070,'label','orderReopenTimeframe','二次开单间隔(天)','order reopen timeframe',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5071,'label','unbundWX','是否解除微信绑定？','unbundWX?',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5072,'label','maxMessageCount','最大消息次数','max message count',null,-1);


-- asset head

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5401,'label','assetHead_status', '设备状态','Device Status',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5402,'label','assetHead_tile_maintenance', '维修中','In Repairing',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5403,'label','assetHead_tile_off', '停机中','Down(Out of Service)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5404,'label','assetHead_tile_out', '保修期到期（2个月内）','MSA to be expired in 2 months',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5405,'label','assetHead_tile_preventive', '保养（1周内）','Preventive Maintenance in 1 week',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5406,'label','assetHead_tile_metrology', '设备计量（2个月内）','Metering in 2 months',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5407,'label','assetHead_tile_quality', '设备质控（2个月内）','Quality Control in 2 months',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5411,'label','assetHead_tile_maintenance_progressSeries', '维修流程','Progress',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5412,'label','assetHead_tile_maintenance_progress', '维修流程：','Progress:',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5413,'label','assetHead_tile_maintenance_owner', '维修人：','Owner',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5414,'label','assetHead_tile_off_time', '停机时间：','Down Time:',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5415,'label','assetHead_tile_off_reason', '原因：','Reason:',null,-1);

-- staff

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5431,'label','staff_title', '我的工单管理','My Work Orders',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5435,'label','staff_tile_inspection', '巡检','Inspect',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5436,'label','staff_tile_meter', '计量','Meter',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5437,'label','staff_tile_quality', '质控','QC',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5438,'label','staff_tile_maintenance', '维修','Repair',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5439,'label','staff_tile_preventive', '保养','Maintain',null,-1);

-- device maintenance

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5501,'label','maintenanceAnalysis_title', '设备维修事件统计分析','Device Maintenance Analysis',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5506,'label','maintenanceAnalysis_empty', '未填','Unspecified',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5507,'label','maintenanceAnalysis_otherReason', '其他原因','Others',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5508,'label','maintenanceAnalysis_otherCategory', '其他设备类型','Others',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5509,'label','maintenanceAnalysis_otherRoom', '其他科室','Others',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5511,'label','maintenanceAnalysis_reasonTile', '故障主要原因','Most Common Reason',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5512,'label','maintenanceAnalysis_stepTile', '故障处理流程最耗时步骤','Most Time-Consuming Step',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5513,'label','maintenanceAnalysis_roomTile', '故障主要发生的科室','Most Frequent Room',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5514,'label','maintenanceAnalysis_categoryTile', '故障主要发生的设备类型','Most Frequent Device',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5515,'label','maintenanceAnalysis_occurrenceTile', '设备故障数','Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5516,'label','maintenanceAnalysis_rankTile_room', '在科室中排名','Rank in Room',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5517,'label','maintenanceAnalysis_rankTile_category', '在同类设备中排名','Rank in Category',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5518,'label','maintenanceAnalysis_rankTile_device', '在所有设备中排名','Rank Overall',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5521,'label','maintenanceAnalysis_reasonChart', '设备故障原因分析（次）','Reason Analysis',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5522,'label','maintenanceAnalysis_reasonChart_xAxis', '故障原因','Reason',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5523,'label','maintenanceAnalysis_reasonChart_yAxis', '故障次数','Occurrence',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5531,'label','maintenanceAnalysis_timeChart', '设备故障处理流程响应时间分布','Response Time Composition',null,-1);


INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5532,'label','maintenanceAnalysis_timeChart_section', '耗时最长的三个步骤的具体响应时间分布','Time Distribution in 3 most time-consuming steps',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5533,'label','maintenanceAnalysis_timeChart_minute', '%s%d分钟','%s%d minute(s)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5534,'label','maintenanceAnalysis_timeChart_hour', '%s%.1f小时','%s%.1f hour(s)',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5535,'label','maintenanceAnalysis_timeChart_legend_0', '未响应','No Response',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5536,'label','maintenanceAnalysis_timeChart_legend_1', '小于30分钟','Less Than 30 Minutes',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5537,'label','maintenanceAnalysis_timeChart_legend_2', '30分钟到1小时以内','30 Minutes to 1 Hour',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5538,'label','maintenanceAnalysis_timeChart_legend_3', '1小时到1天以内','1 Hour to 1 Day',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5539,'label','maintenanceAnalysis_timeChart_legend_4', '1天以上','Over 1 Day',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5541,'label','maintenanceAnalysis_distributionChart', '设备故障分布（次）','Distribution',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5543,'label','maintenanceAnalysis_distributionChart_yAxis', '故障次数','Occurrence',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5544,'label','maintenanceAnalysis_distributionChart_room', '按科室','By Room',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5545,'label','maintenanceAnalysis_distributionChart_room_xAxis', '科室','Room',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5546,'label','maintenanceAnalysis_distributionChart_category', '按设备类型','By Category',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5547,'label','maintenanceAnalysis_distributionChart_category_xAxis', '设备类型','Category',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5548,'label','maintenanceAnalysis_distributionChart_device', '按单台设备（前40台）','By Device (Top 40)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5549,'label','maintenanceAnalysis_distributionChart_device_xAxis', '设备','Device',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5551,'label','maintenanceAnalysis_percentageChart', '设备故障所占比例','Numbers',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5552,'label','maintenanceAnalysis_counting', '台','Device(s)',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5553,'label','maintenanceAnalysis_this', '本台设备','This Device',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5554,'label','maintenanceAnalysis_that', '其它设备','Others',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5555,'label','maintenanceAnalysis_percentageChart_percentage', '所占比例：','Ratio: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5556,'label','maintenanceAnalysis_percentageChart_occurrence', '故障数：','This device: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5561,'label','maintenanceAnalysis_percentageChart_room', '占所在科室总故障比例','Ratio in the room',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5562,'label','maintenanceAnalysis_percentageChart_room_occurrence', '所在科室故障数：','The room: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5563,'label','maintenanceAnalysis_percentageChart_room_rank', '在所在科室内排名：','Rank in the room: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5564,'label','maintenanceAnalysis_percentageChart_category', '占所在设备类型故障比例','Ratio in the category',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5565,'label','maintenanceAnalysis_percentageChart_category_occurrence', '所在类型故障数：','The category: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5566,'label','maintenanceAnalysis_percentageChart_category_rank', '在此类设备中排名：','Rank in the category: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5567,'label','maintenanceAnalysis_percentageChart_device', '占所有设备故障比例','Ratio overall',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5568,'label','maintenanceAnalysis_percentageChart_device_occurrence', '所有设备故障数：','Overall: ',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5569,'label','maintenanceAnalysis_percentageChart_device_rank', '在所有设备中排名：','Rank overall: ',null,-1);

-- device maintenance (preventive)

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5571,'label','preventiveMaintenanceAnalysis_title', '设备保养排期／跟踪／统计分析','Maintenance Schedule and Analysis',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5581,'label','preventiveMaintenanceAnalysis_planChart', 'PM统计表','Preventive Maintenance Schedule',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5582,'label','preventiveMaintenanceAnalysis_predicationChart', '设备维护预测','Preventive Maintenance Forecast',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5585,'label','preventiveMaintenanceAnalysis_year', '%d年','%dY',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5586,'label','preventiveMaintenanceAnalysis_month', '%d月','%dM',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5587,'label','preventiveMaintenanceAnalysis_allDevices', '全部设备','All',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5588,'label','preventiveMaintenanceAnalysis_deviceName', '设备名称','Name',null,-1);

-- device usage

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5591,'label','operationMonitor_count', '检查部位总数','Count',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5592,'label','operationMonitor_summary', '设备检查部位统计（个）','Summary',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5593,'label','operationMonitor_device', '个','devices',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5597,'label','operationMonitor_day', '天','Day',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5598,'label','operationMonitor_week', '周','Week',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5599,'label','operationMonitor_month', '月','Month',null,-1);

-- Home pages start from 5601 to 5700

-- Hospital head home page
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5601,'label','assetTotalProfit', '设备总利润','Asset Total Profit',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5602,'label','assetTotalProfitForecast', '设备总利润预测','Asset Total Profit Forecast',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5603,'label','assetAveROI', '设备平均投资回报（元）','Asset Ave ROI',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5604,'label','assetROI', '设备投资回报','Asset ROI',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5605,'label','profitDistro', '利润科室分布','Profit Distribution by Department',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5606,'label','assetType', '设备类型','Asset Type',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5621,'label','assetAnnual', '设备绩效年报（元）','Asset Performance Annual Report',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5622,'label','assetAnnualForecast', '设备绩效年报预测（元）','Asset Performance Forecast Report',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (5623,'label','assetAnnualForecast_title', '设备绩效预测','Asset Performance Annual Report',null,-1);

INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9001,'month','1',  '一月','January',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9002,'month','2',  '二月','Feburary',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9003,'month','3',  '三月','March',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9004,'month','4',  '四月','April',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9005,'month','5',  '五月','May',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9006,'month','6',  '六月','June',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9007,'month','7',  '七月','July',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9008,'month','8',  '八月','August',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9009,'month','9',  '九月','September',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9010,'month','10', '十月','October',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9011,'month','11', '十一月','November',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9012,'month','12', '十二月','December',null,-1);


INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9100,'focusOption','0',  '所有工单流程消息','all workorder msg',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9101,'focusOption','1',  '仅工单创建和关单消息','only create and shutdown msg',null,-1);
INSERT INTO i18n_message(id,msg_type,msg_key,value_zh,value_en,value_tw,site_id) VALUES (9102,'focusOption','2',  '不推送工单消息','no need push workorder msg',null,-1);






