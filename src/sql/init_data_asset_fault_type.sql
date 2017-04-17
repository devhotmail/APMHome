truncate table asset_fault_type;

insert into asset_fault_type(id,asset_group_id, fault_name) values( 1, 1, '扫描数据丢失或重建失败');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 2, 1, '探测器故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 3, 1, '图像质量问题/伪影');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 4, 1, '球管故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 5, 1, '扫描床运动故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 6, 1, '软件/设置问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 7, 1, '操作台故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 8, 1, '扫描架故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 9, 1, '电源分配柜（PDU）故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 10, 1, '电源故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 11, 1, '附属设备故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 12, 1, '操作问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 13, 1, '其它');

insert into asset_fault_type(id,asset_group_id, fault_name) values( 14, 6, '机械问题 床/机架/Telescope 支撑移动异常');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 15, 6, '图像质量问题/伪影');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 16, 6, '外围设备或网络传输故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 17, 6, '探头故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 18, 6, '软件/设置问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 19, 6, '球管故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 20, 6, '电源故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 21, 6, '操作问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 22, 6, '其它');

insert into asset_fault_type(id,asset_group_id, fault_name) values( 23, 2, '图像质量问题/伪影');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 24, 2, '软件/设置问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 25, 2, '线圈故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 26, 2, '梯度系统故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 27, 2, '视频系统故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 28, 2, '接收通路故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 29, 2, '电源故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 30, 2, '水冷系统故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 31, 2, '通讯或网络故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 32, 2, '磁体故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 33, 2, '机械故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 34, 2, '空调系统故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 35, 2, '其它');

insert into asset_fault_type(id,asset_group_id, fault_name) values( 36, 4, '软件/设置问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 37, 4, 'X射线发生器故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 38, 4, '机架/床运动故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 39, 4, '电池组故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 40, 4, '水冷机故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 41, 4, '探测器/影像增强器故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 42, 4, '电源故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 43, 4, '操作问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 44, 4, '其它');

insert into asset_fault_type(id,asset_group_id, fault_name) values( 45, 5, '显示器无显示');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 46, 5, 'Dicom 传输故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 47, 5, '轨迹球失灵');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 48, 5, '按键/键盘故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 49, 5, '探头故障');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 50, 5, '机器无响应');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 51, 5, '蓝屏');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 52, 5, '运行中报错');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 53, 5, '其它');

insert into asset_fault_type(id,asset_group_id, fault_name) values( 54, -1,'设备老化');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 55, -1,'设备损坏');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 56, -1,'耗材损坏');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 57, -1,'操作问题');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 58, -1,'保修过期');
insert into asset_fault_type(id,asset_group_id, fault_name) values( 59, -1,'其它');


SELECT setval('"public"."asset_fault_type_id_seq"', 60, false);