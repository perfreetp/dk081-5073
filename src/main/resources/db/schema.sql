CREATE DATABASE IF NOT EXISTS safety_campus_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE safety_campus_dev;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    dept_id BIGINT COMMENT '部门ID',
    role_type TINYINT NOT NULL DEFAULT 3 COMMENT '角色类型:1-教育局管理员,2-教育局值班,3-学校保卫,4-派出所联络员',
    school_id BIGINT COMMENT '关联学校ID(学校保卫)',
    police_station_id BIGINT COMMENT '关联派出所ID(联络员)',
    status TINYINT DEFAULT 1 COMMENT '状态:0-禁用,1-启用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username(username),
    INDEX idx_phone(phone),
    INDEX idx_school(school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE,
    role_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    dept_type TINYINT COMMENT '1-教育局,2-学校,3-派出所',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

CREATE TABLE IF NOT EXISTS school_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_code VARCHAR(50) NOT NULL UNIQUE COMMENT '学校编码',
    school_name VARCHAR(100) NOT NULL COMMENT '学校名称',
    school_type TINYINT NOT NULL COMMENT '学校类型:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校',
    school_level TINYINT DEFAULT 3 COMMENT '风险等级:1-重点,2-关注,3-普通',
    address VARCHAR(200) COMMENT '地址',
    principal VARCHAR(50) COMMENT '校长',
    principal_phone VARCHAR(20) COMMENT '校长电话',
    security_leader VARCHAR(50) COMMENT '保卫主任',
    security_phone VARCHAR(20) COMMENT '保卫电话',
    police_station_id BIGINT COMMENT '属地派出所ID',
    group_id BIGINT COMMENT '学校分组ID',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    student_count INT DEFAULT 0 COMMENT '学生人数',
    teacher_count INT DEFAULT 0 COMMENT '教职工人数',
    device_count INT DEFAULT 0 COMMENT '报警设备数量',
    status TINYINT DEFAULT 1 COMMENT '状态:0-停用,1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_school_code(school_code),
    INDEX idx_school_type(school_type),
    INDEX idx_group(group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校信息表';

CREATE TABLE IF NOT EXISTS school_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL,
    group_type TINYINT COMMENT '分组类型:1-按类型,2-按片区,3-自定义',
    description VARCHAR(200),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校分组表';

CREATE TABLE IF NOT EXISTS alarm_device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编码',
    device_name VARCHAR(100) COMMENT '设备名称',
    device_type TINYINT COMMENT '设备类型:1-一键报警柱,2-桌面报警盒,3-APP客户端',
    school_id BIGINT NOT NULL,
    location VARCHAR(200) COMMENT '安装位置',
    status TINYINT DEFAULT 1 COMMENT '状态:0-离线,1-在线,2-故障',
    last_online_at DATETIME COMMENT '最后在线时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_device_code(device_code),
    INDEX idx_school(school_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警设备表';

CREATE TABLE IF NOT EXISTS alarm_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_no VARCHAR(32) NOT NULL UNIQUE COMMENT '警情编号',
    school_id BIGINT NOT NULL,
    device_id BIGINT COMMENT '报警设备ID',
    alarm_type TINYINT NOT NULL COMMENT '报警类型:1-紧急求助,2-火灾,3-治安,4-校园欺凌,5-食物中毒,6-自然灾害,7-其他',
    alarm_level TINYINT NOT NULL COMMENT '警情级别:1-重大,2-较大,3-一般',
    alarm_title VARCHAR(200) NOT NULL COMMENT '警情标题',
    alarm_content TEXT COMMENT '警情详情',
    reporter_name VARCHAR(50) COMMENT '报警人姓名',
    reporter_phone VARCHAR(20) COMMENT '报警人电话',
    location VARCHAR(200) COMMENT '报警位置',
    longitude DECIMAL(10,6) COMMENT '经度',
    latitude DECIMAL(10,6) COMMENT '纬度',
    parent_id BIGINT DEFAULT 0 COMMENT '合并主警情ID',
    merged_count INT DEFAULT 1 COMMENT '合并报警次数',
    status TINYINT DEFAULT 1 COMMENT '状态:1-待处置,2-处置中,3-已督办,4-已处置,5-已关闭',
    is_escalated TINYINT DEFAULT 0 COMMENT '是否已上推:0-否,1-是',
    escalated_at DATETIME COMMENT '上推时间',
    is_holiday TINYINT DEFAULT 0 COMMENT '是否节假日',
    first_response_at DATETIME COMMENT '首次响应时间',
    handled_at DATETIME COMMENT '处置完成时间',
    closed_at DATETIME COMMENT '关闭时间',
    handler_id BIGINT COMMENT '处置人ID',
    supervisor_id BIGINT COMMENT '督办人ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_alarm_no(alarm_no),
    INDEX idx_school(school_id),
    INDEX idx_level(alarm_level),
    INDEX idx_status(status),
    INDEX idx_created_at(created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报警记录表';

CREATE TABLE IF NOT EXISTS alarm_flow (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    flow_type TINYINT NOT NULL COMMENT '流转类型:1-创建,2-派单,3-督办,4-响应,5-处置,6-催办,7-关闭',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    operator_role VARCHAR(50) COMMENT '操作人角色',
    party_type TINYINT COMMENT '参与方类型:1-教育局,2-学校,3-派出所,4-系统',
    party_id BIGINT COMMENT '参与方ID',
    party_name VARCHAR(50) COMMENT '参与方名称',
    duration_seconds INT COMMENT '耗时(秒)',
    location VARCHAR(200) COMMENT '操作位置',
    remark TEXT COMMENT '处理意见',
    attach_url VARCHAR(500) COMMENT '附件URL',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_alarm(alarm_id),
    INDEX idx_type(flow_type),
    INDEX idx_party_type(party_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='警情流转记录表';

CREATE TABLE IF NOT EXISTS alarm_remind (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT NOT NULL,
    remind_type TINYINT NOT NULL COMMENT '催办类型:1-首次响应超时,2-处置超时',
    remind_count INT DEFAULT 1 COMMENT '催办次数',
    last_remind_at DATETIME,
    next_remind_at DATETIME,
    status TINYINT DEFAULT 1 COMMENT '1-待催办,2-已完成',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_alarm(alarm_id),
    INDEX idx_next_remind(next_remind_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='超时催办表';

CREATE TABLE IF NOT EXISTS police_station (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    station_code VARCHAR(50) NOT NULL UNIQUE,
    station_name VARCHAR(100) NOT NULL,
    liaison VARCHAR(50) COMMENT '联络人',
    liaison_phone VARCHAR(20) COMMENT '联络电话',
    duty_phone VARCHAR(20) COMMENT '值班电话',
    address VARCHAR(200) COMMENT '地址',
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='派出所信息表';

CREATE TABLE IF NOT EXISTS contact_book (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    unit_type TINYINT NOT NULL COMMENT '单位类型:1-教育局,2-学校,3-派出所,4-医院,5-消防,6-其他',
    unit_name VARCHAR(100) COMMENT '单位名称',
    position VARCHAR(50) COMMENT '职务',
    group_id BIGINT COMMENT '分组ID',
    is_duty TINYINT DEFAULT 0 COMMENT '是否值班人员',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_unit_type(unit_type),
    INDEX idx_group(group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联动通讯录表';

CREATE TABLE IF NOT EXISTS contact_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通讯录分组表';

CREATE TABLE IF NOT EXISTS notify_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alarm_id BIGINT COMMENT '关联警情ID',
    notify_type TINYINT NOT NULL COMMENT '通知类型:1-短信,2-APP推送,3-电话,4-邮件',
    notify_target VARCHAR(200) NOT NULL COMMENT '通知目标(手机号/设备ID)',
    target_name VARCHAR(50) COMMENT '目标姓名',
    title VARCHAR(200),
    content TEXT,
    template_code VARCHAR(50),
    status TINYINT DEFAULT 0 COMMENT '0-待发送,1-已发送,2-发送失败',
    fail_reason VARCHAR(500),
    sent_at DATETIME,
    read_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_alarm(alarm_id),
    INDEX idx_type(notify_type),
    INDEX idx_status(status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知记录表';

CREATE TABLE IF NOT EXISTS duty_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    duty_date DATE NOT NULL,
    duty_type TINYINT NOT NULL COMMENT '值班类型:1-工作日,2-周末,3-节假日',
    user_id BIGINT NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    shift_type TINYINT COMMENT '班次:1-白班,2-夜班',
    start_time TIME,
    end_time TIME,
    is_standby TINYINT DEFAULT 0 COMMENT '是否备班',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_date_user(duty_date, user_id, shift_type),
    INDEX idx_date(duty_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='值班排班表';

CREATE TABLE IF NOT EXISTS holiday_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    holiday_name VARCHAR(50) NOT NULL,
    holiday_type TINYINT COMMENT '1-法定假日,2-调休,3-周末',
    strategy_type TINYINT DEFAULT 1 COMMENT '值守策略:1-常规,2-加强,3-严格',
    notify_range VARCHAR(200) COMMENT '额外通知范围',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_date(holiday_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节假日配置表';

CREATE TABLE IF NOT EXISTS school_risk (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    stat_month VARCHAR(7) NOT NULL COMMENT '统计月份YYYY-MM',
    total_alarms INT DEFAULT 0 COMMENT '总报警数',
    critical_alarms INT DEFAULT 0 COMMENT '重大警情数',
    avg_response_time INT DEFAULT 0 COMMENT '平均响应时间(秒)',
    avg_handle_time INT DEFAULT 0 COMMENT '平均处置时间(秒)',
    timeout_count INT DEFAULT 0 COMMENT '超时次数',
    school_level TINYINT COMMENT '学校等级',
    risk_score DECIMAL(5,2) COMMENT '风险评分',
    risk_level TINYINT COMMENT '风险等级:1-高,2-中,3-低',
    indicators JSON COMMENT '详细指标',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_school_month(school_id, stat_month),
    INDEX idx_risk_level(risk_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校风险画像表';

CREATE TABLE IF NOT EXISTS assess_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    school_id BIGINT NOT NULL,
    stat_quarter VARCHAR(7) NOT NULL COMMENT '统计季度YYYY-QN',
    total_score DECIMAL(6,2) COMMENT '总分',
    alarm_score DECIMAL(6,2) COMMENT '报警指标分',
    response_score DECIMAL(6,2) COMMENT '响应速度分',
    handle_score DECIMAL(6,2) COMMENT '处置质量分',
    duty_score DECIMAL(6,2) COMMENT '值班值守分',
    rank_num INT COMMENT '排名',
    grade CHAR(1) COMMENT '等级:A/B/C/D',
    assess_date DATE COMMENT '考核日期',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_school_quarter(school_id, stat_quarter),
    INDEX idx_rank(rank_num)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核排名表';

CREATE TABLE IF NOT EXISTS sys_param (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    param_key VARCHAR(100) NOT NULL UNIQUE,
    param_value TEXT,
    param_name VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统参数表';

CREATE TABLE IF NOT EXISTS sys_audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    username VARCHAR(50),
    module VARCHAR(50),
    operation VARCHAR(200),
    ip VARCHAR(50),
    user_agent VARCHAR(500),
    params TEXT,
    result TEXT,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user(user_id),
    INDEX idx_module(module),
    INDEX idx_created(created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志表';

CREATE TABLE IF NOT EXISTS notify_rule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    rule_type TINYINT NOT NULL COMMENT '规则类型:1-默认规则,2-节假日规则,3-夜间规则,4-自定义规则',
    priority INT DEFAULT 0 COMMENT '优先级,数字越大优先级越高',
    is_enabled TINYINT DEFAULT 1 COMMENT '是否启用:0-禁用,1-启用',
    holiday_mode TINYINT DEFAULT 0 COMMENT '节假日模式:0-否,1-是',
    night_mode TINYINT DEFAULT 0 COMMENT '夜间模式:0-否,1-是',
    school_types VARCHAR(200) COMMENT '适用学校类型,逗号分隔:1-幼儿园,2-小学,3-初中,4-高中,5-中职,6-高校',
    alarm_levels VARCHAR(100) COMMENT '适用警情级别,逗号分隔:1-重大,2-较大,3-一般',
    notify_channels VARCHAR(100) COMMENT '通知渠道,逗号分隔:1-短信,2-APP,3-电话,4-邮件',
    notify_targets TEXT COMMENT '通知目标配置(JSON)',
    notify_template VARCHAR(500) COMMENT '通知模板',
    description VARCHAR(500) COMMENT '规则描述',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_rule_type(rule_type),
    INDEX idx_is_enabled(is_enabled),
    INDEX idx_priority(priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知规则表';

CREATE TABLE IF NOT EXISTS notify_rule_target (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    rule_id BIGINT NOT NULL COMMENT '规则ID',
    target_type TINYINT NOT NULL COMMENT '目标类型:1-教育局值班,2-学校保卫,3-派出所,4-指定人员',
    target_id BIGINT COMMENT '目标ID',
    target_name VARCHAR(50) COMMENT '目标名称',
    target_phone VARCHAR(20) COMMENT '目标电话',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_rule_id(rule_id),
    INDEX idx_target_type(target_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知规则目标表';

CREATE TABLE IF NOT EXISTS town_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    town_code VARCHAR(50) NOT NULL UNIQUE COMMENT '街镇编码',
    town_name VARCHAR(100) NOT NULL COMMENT '街镇名称',
    sort_order INT DEFAULT 0 COMMENT '排序',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_town_code(town_code),
    INDEX idx_sort_order(sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='街镇信息表';

ALTER TABLE school_info ADD COLUMN IF NOT EXISTS town_id BIGINT COMMENT '所属街镇ID' AFTER group_id;
ALTER TABLE school_info ADD INDEX IF NOT EXISTS idx_town(town_id);
