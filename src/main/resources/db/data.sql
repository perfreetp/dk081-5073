USE safety_campus_dev;

INSERT INTO sys_role (id, role_code, role_name, description) VALUES
(1, 'ADMIN', '系统管理员', '拥有所有系统权限'),
(2, 'EDU_ADMIN', '教育局管理员', '教育局管理用户'),
(3, 'EDU_DUTY', '教育局值班', '教育局值班人员'),
(4, 'SCHOOL_SECURITY', '学校保卫', '学校保卫人员'),
(5, 'POLICE_LIAISON', '派出所联络员', '派出所联络人员')
ON DUPLICATE KEY UPDATE role_code = VALUES(role_code);

INSERT INTO sys_user (id, username, password, real_name, phone, email, dept_id, role_type, school_id, police_station_id, status) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800000001', 'admin@safetycampus.com', 1, 1, NULL, NULL, 1),
(2, 'edu_admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '教育局管理员', '13800000002', 'edu_admin@safetycampus.com', 2, 1, NULL, NULL, 1),
(3, 'edu_duty', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '教育局值班', '13800000003', 'edu_duty@safetycampus.com', 2, 2, NULL, NULL, 1)
ON DUPLICATE KEY UPDATE username = VALUES(username);

INSERT INTO sys_department (id, dept_name, parent_id, dept_type, sort_order) VALUES
(1, '市教育局', 0, 1, 1),
(2, '教育局安全科', 1, 1, 2),
(3, '第一中学', 0, 2, 3),
(4, '第二小学', 0, 2, 4),
(5, '实验幼儿园', 0, 2, 5),
(6, '城区派出所', 0, 3, 6),
(7, '郊区派出所', 0, 3, 7)
ON DUPLICATE KEY UPDATE dept_name = VALUES(dept_name);

INSERT INTO police_station (id, station_code, station_name, liaison, liaison_phone, duty_phone, address, sort_order, status) VALUES
(1, 'POLICE001', '城区派出所', '张警官', '13900000001', '010-12345678', '北京市城区平安大街1号', 1, 1),
(2, 'POLICE002', '郊区派出所', '李警官', '13900000002', '010-87654321', '北京市郊区和谐路2号', 2, 1)
ON DUPLICATE KEY UPDATE station_code = VALUES(station_code);

INSERT INTO school_info (id, school_code, school_name, school_type, school_level, address, principal, principal_phone, security_leader, security_phone, police_station_id, group_id, longitude, latitude, student_count, teacher_count, device_count, status) VALUES
(1, 'SCH001', '北京市第一中学', 3, 1, '北京市城区教育路1号', '王校长', '13700000001', '赵主任', '13700000002', 1, 1, 116.4074, 39.9042, 2000, 150, 10, 1),
(2, 'SCH002', '北京市第二小学', 2, 2, '北京市城区文化路2号', '李校长', '13700000003', '钱主任', '13700000004', 1, 1, 116.4174, 39.9142, 1500, 100, 8, 1),
(3, 'SCH003', '北京市实验幼儿园', 1, 3, '北京市郊区育英路3号', '孙园长', '13700000005', '周主任', '13700000006', 2, 2, 116.4274, 39.9242, 500, 50, 5, 1)
ON DUPLICATE KEY UPDATE school_code = VALUES(school_code);

INSERT INTO school_group (id, group_name, group_type, description, sort_order) VALUES
(1, '城区学校', 2, '城区范围内的学校', 1),
(2, '郊区学校', 2, '郊区范围内的学校', 2),
(3, '重点学校', 1, '重点关注学校', 3)
ON DUPLICATE KEY UPDATE group_name = VALUES(group_name);

INSERT INTO contact_group (id, group_name, description, sort_order) VALUES
(1, '紧急联络组', '紧急情况下的第一联络人', 1),
(2, '学校保卫组', '各学校保卫人员', 2),
(3, '联动单位组', '各联动单位联络人', 3)
ON DUPLICATE KEY UPDATE group_name = VALUES(group_name);

INSERT INTO contact_book (id, name, phone, unit_type, unit_name, position, group_id, is_duty, sort_order) VALUES
(1, '张警官', '13900000001', 3, '城区派出所', '警长', 3, 1, 1),
(2, '李警官', '13900000002', 3, '郊区派出所', '警长', 3, 1, 2),
(3, '王医生', '13800000101', 4, '市第一医院', '急诊科主任', 3, 0, 3),
(4, '刘队长', '13800000102', 5, '市消防支队', '中队长', 3, 0, 4),
(5, '赵主任', '13700000002', 2, '北京市第一中学', '保卫主任', 2, 1, 5)
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO holiday_config (id, holiday_date, holiday_name, holiday_type, strategy_type, notify_range) VALUES
(1, '2025-01-01', '元旦', 1, 2, '教育局值班,学校保卫'),
(2, '2025-02-10', '春节', 1, 3, '全体人员'),
(3, '2025-04-05', '清明节', 1, 2, '教育局值班,学校保卫'),
(4, '2025-05-01', '劳动节', 1, 2, '教育局值班,学校保卫'),
(5, '2025-06-01', '儿童节', 1, 1, '学校保卫'),
(6, '2025-09-10', '教师节', 1, 1, '学校保卫'),
(7, '2025-10-01', '国庆节', 1, 3, '全体人员'),
(8, '2025-01-05', '周末', 3, 1, '值班人员'),
(9, '2025-01-06', '周末', 3, 1, '值班人员'),
(10, '2025-01-11', '周末', 3, 1, '值班人员'),
(11, '2025-01-12', '周末', 3, 1, '值班人员')
ON DUPLICATE KEY UPDATE holiday_date = VALUES(holiday_date);

INSERT INTO sys_param (id, param_key, param_value, param_name, description) VALUES
(1, 'alarm.duplicate.time_window', '300', '重复报警时间窗口(秒)', '同一设备在时间窗口内的相同报警视为重复报警'),
(2, 'alarm.escalate.critical_level', '1', '自动上推警情级别', '达到该级别的警情自动上推至教育局'),
(3, 'alarm.escalate.auto_escalate', 'true', '是否自动上推', '重大警情是否自动上推至教育局'),
(4, 'alarm.timeout.first_response', '300', '首次响应超时时间(秒)', '报警后需要在该时间内首次响应'),
(5, 'alarm.timeout.remind_interval', '600', '催办间隔时间(秒)', '超时后每隔多久催办一次'),
(6, 'alarm.holiday.mode', 'strict', '节假日值守模式', '节假日值守模式:strict-严格,enhanced-加强,normal-常规'),
(7, 'alarm.holiday.notify_all', 'true', '节假日是否通知全员', '节假日报警是否通知所有值班人员'),
(8, 'notify.sms.provider', 'aliyun', '短信服务商', '短信发送服务提供商'),
(9, 'notify.sms.template_critical', 'SMS_123456789', '重大警情短信模板', '重大警情短信通知模板编码'),
(10, 'notify.sms.template_warning', 'SMS_123456790', '较大警情短信模板', '较大警情短信通知模板编码'),
(11, 'notify.sms.template_normal', 'SMS_123456791', '一般警情短信模板', '一般警情短信通知模板编码'),
(12, 'notify.app.enabled', 'true', '是否启用APP推送', '是否启用APP消息推送'),
(13, 'notify.call.enabled', 'true', '是否启用语音通知', '是否启用语音电话通知'),
(14, 'system.title', '平安校园一键报警联网平台', '系统名称', '系统显示名称'),
(15, 'system.logo', '/logo.png', '系统Logo', '系统Logo路径'),
(16, 'system.copyright', '© 2025 平安校园', '版权信息', '系统版权信息')
ON DUPLICATE KEY UPDATE param_key = VALUES(param_key);

INSERT INTO alarm_device (id, device_code, device_name, device_type, school_id, location, status) VALUES
(1, 'DEV001', '校门口报警柱', 1, 1, '学校正门左侧', 1),
(2, 'DEV002', '教学楼报警盒', 2, 1, '教学楼一楼大厅', 1),
(3, 'DEV003', '操场报警柱', 1, 1, '操场西北角', 1),
(4, 'DEV004', '校门口报警柱', 1, 2, '学校正门', 1),
(5, 'DEV005', '教学楼报警盒', 2, 2, '教学楼门厅', 1),
(6, 'DEV006', '园区报警柱', 1, 3, '幼儿园大门内', 1),
(7, 'DEV007', '教学楼报警盒', 2, 3, '教学楼入口', 1)
ON DUPLICATE KEY UPDATE device_code = VALUES(device_code);

INSERT INTO town_info (id, town_code, town_name, sort_order) VALUES
(1, 'TOWN001', '城关镇', 1),
(2, 'TOWN002', '城郊镇', 2),
(3, 'TOWN003', '高新街道', 3),
(4, 'TOWN004', '滨河街道', 4),
(5, 'TOWN005', '文峰镇', 5),
(6, 'TOWN006', '太平镇', 6)
ON DUPLICATE KEY UPDATE town_code = VALUES(town_code);

UPDATE school_info SET town_id = 1 WHERE id = 1;
UPDATE school_info SET town_id = 1 WHERE id = 2;
UPDATE school_info SET town_id = 2 WHERE id = 3;
