package com.safetycampus.common.enums;

public class AlarmDict {

    public static final String ALARM_NO_PREFIX = "ALARM";

    public static final Integer ALARM_STATUS_PENDING = 1;
    public static final Integer ALARM_STATUS_PROCESSING = 2;
    public static final Integer ALARM_STATUS_SUPERVISED = 3;
    public static final Integer ALARM_STATUS_HANDLED = 4;
    public static final Integer ALARM_STATUS_CLOSED = 5;

    public static final Integer FLOW_TYPE_CREATE = 1;
    public static final Integer FLOW_TYPE_DISPATCH = 2;
    public static final Integer FLOW_TYPE_SUPERVISE = 3;
    public static final Integer FLOW_TYPE_RESPONSE = 4;
    public static final Integer FLOW_TYPE_HANDLE = 5;
    public static final Integer FLOW_TYPE_REMIND = 6;
    public static final Integer FLOW_TYPE_CLOSE = 7;

    public static final Integer REMIND_TYPE_FIRST_RESPONSE = 1;
    public static final Integer REMIND_TYPE_HANDLE_TIMEOUT = 2;

    public static final Integer REMIND_STATUS_PENDING = 1;
    public static final Integer REMIND_STATUS_COMPLETED = 2;

    public static final Integer NOTIFY_TYPE_SMS = 1;
    public static final Integer NOTIFY_TYPE_APP = 2;
    public static final Integer NOTIFY_TYPE_CALL = 3;
    public static final Integer NOTIFY_TYPE_EMAIL = 4;

    public static final Integer NOTIFY_STATUS_PENDING = 0;
    public static final Integer NOTIFY_STATUS_SENT = 1;
    public static final Integer NOTIFY_STATUS_FAILED = 2;

    public static final Integer UNIT_TYPE_EDUCATION = 1;
    public static final Integer UNIT_TYPE_SCHOOL = 2;
    public static final Integer UNIT_TYPE_POLICE = 3;
    public static final Integer UNIT_TYPE_HOSPITAL = 4;
    public static final Integer UNIT_TYPE_FIRE = 5;
    public static final Integer UNIT_TYPE_OTHER = 6;

    public static final Integer DUTY_TYPE_WORKDAY = 1;
    public static final Integer DUTY_TYPE_WEEKEND = 2;
    public static final Integer DUTY_TYPE_HOLIDAY = 3;

    public static final Integer SHIFT_TYPE_DAY = 1;
    public static final Integer SHIFT_TYPE_NIGHT = 2;

    public static final Integer HOLIDAY_TYPE_LEGAL = 1;
    public static final Integer HOLIDAY_TYPE_ADJUSTED = 2;
    public static final Integer HOLIDAY_TYPE_WEEKEND = 3;

    public static final Integer STRATEGY_TYPE_NORMAL = 1;
    public static final Integer STRATEGY_TYPE_ENHANCED = 2;
    public static final Integer STRATEGY_TYPE_STRICT = 3;

    public static final Integer DEPT_TYPE_EDUCATION = 1;
    public static final Integer DEPT_TYPE_SCHOOL = 2;
    public static final Integer DEPT_TYPE_POLICE = 3;

    public static final Integer USER_STATUS_DISABLED = 0;
    public static final Integer USER_STATUS_ENABLED = 1;

    public static final Integer ESCALATED_NO = 0;
    public static final Integer ESCALATED_YES = 1;

    public static final Integer HOLIDAY_NO = 0;
    public static final Integer HOLIDAY_YES = 1;

    public static final Integer DELETED_NO = 0;
    public static final Integer DELETED_YES = 1;

    public static final Integer STANDBY_NO = 0;
    public static final Integer STANDBY_YES = 1;

    public static final Integer DUTY_NO = 0;
    public static final Integer DUTY_YES = 1;

    public static final String REDIS_KEY_ALARM_DUPLICATE = "alarm:duplicate:";
    public static final String REDIS_KEY_ALARM_NO_SEQUENCE = "alarm:no:sequence:";
    public static final String REDIS_KEY_ONLINE_USER = "alarm:online:user:";
    public static final String REDIS_KEY_DUTY_TODAY = "alarm:duty:today:";

    public static final Integer SCHOOL_TYPE_KINDERGARTEN = 1;
    public static final Integer SCHOOL_TYPE_PRIMARY = 2;
    public static final Integer SCHOOL_TYPE_JUNIOR = 3;
    public static final Integer SCHOOL_TYPE_SENIOR = 4;
    public static final Integer SCHOOL_TYPE_VOCATIONAL = 5;
    public static final Integer SCHOOL_TYPE_UNIVERSITY = 6;

    public static final Integer SCHOOL_LEVEL_KEY = 1;
    public static final Integer SCHOOL_LEVEL_FOCUS = 2;
    public static final Integer SCHOOL_LEVEL_NORMAL = 3;

    public static final Integer DEVICE_TYPE_PILLAR = 1;
    public static final Integer DEVICE_TYPE_BOX = 2;
    public static final Integer DEVICE_TYPE_APP = 3;

    public static final Integer DEVICE_STATUS_OFFLINE = 0;
    public static final Integer DEVICE_STATUS_ONLINE = 1;
    public static final Integer DEVICE_STATUS_FAULT = 2;

    public static final Integer GROUP_TYPE_BY_TYPE = 1;
    public static final Integer GROUP_TYPE_BY_AREA = 2;
    public static final Integer GROUP_TYPE_CUSTOM = 3;

    public static final Integer RISK_LEVEL_HIGH = 1;
    public static final Integer RISK_LEVEL_MEDIUM = 2;
    public static final Integer RISK_LEVEL_LOW = 3;
}
