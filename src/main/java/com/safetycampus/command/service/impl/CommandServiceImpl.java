package com.safetycampus.command.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.entity.AlarmRecord;
import com.safetycampus.alarm.enums.AlarmLevelEnum;
import com.safetycampus.alarm.enums.AlarmStatusEnum;
import com.safetycampus.alarm.enums.AlarmTypeEnum;
import com.safetycampus.alarm.mapper.AlarmRecordMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.command.dto.CommandNoteDTO;
import com.safetycampus.command.dto.CommandRemindDTO;
import com.safetycampus.command.dto.CommandTransferDTO;
import com.safetycampus.command.entity.AlarmSupplementNote;
import com.safetycampus.command.mapper.AlarmSupplementNoteMapper;
import com.safetycampus.command.mapper.CommandMapper;
import com.safetycampus.command.service.CommandService;
import com.safetycampus.command.vo.CommandDashboardVO;
import com.safetycampus.common.context.LoginUser;
import com.safetycampus.common.context.UserContext;
import com.safetycampus.common.enums.PartyTypeEnum;
import com.safetycampus.common.enums.RoleTypeEnum;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.notify.entity.PoliceStation;
import com.safetycampus.notify.mapper.PoliceStationMapper;
import com.safetycampus.report.entity.SchoolRisk;
import com.safetycampus.report.enums.RiskLevelEnum;
import com.safetycampus.report.mapper.SchoolRiskMapper;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.enums.SchoolTypeEnum;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import com.safetycampus.supervise.enums.FlowTypeEnum;
import com.safetycampus.supervise.service.SuperviseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommandServiceImpl implements CommandService {

    @Resource
    private AlarmRecordMapper alarmRecordMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private PoliceStationMapper policeStationMapper;

    @Resource
    private SchoolRiskMapper schoolRiskMapper;

    @Resource
    private CommandMapper commandMapper;

    @Resource
    private AlarmSupplementNoteMapper alarmSupplementNoteMapper;

    @Resource
    private AlarmFlowService alarmFlowService;

    @Resource
    private SuperviseService superviseService;

    @Override
    public CommandDashboardVO getCommandDashboard(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(alarmId);
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        CommandDashboardVO vo = new CommandDashboardVO();

        vo.setAlarmBrief(getAlarmBrief(alarmId));
        vo.setSchoolDetail(getSchoolDetail(alarm.getSchoolId()));
        vo.setNearbyPoliceStation(getNearbyPoliceStation(alarm.getSchoolId()));
        vo.setAlarmHistoryList(getAlarmHistory(alarm.getSchoolId(), 10));
        vo.setRiskPortrait(getRiskPortrait(alarm.getSchoolId()));
        vo.setNotifyLink(getNotifyLink(alarmId));
        vo.setSuperviseProgress(getSuperviseProgress(alarmId));
        vo.setSupplementNotes(getSupplementNotes(alarmId));

        return vo;
    }

    @Override
    public CommandDashboardVO.AlarmBriefVO getAlarmBrief(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(alarmId);
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        CommandDashboardVO.AlarmBriefVO vo = new CommandDashboardVO.AlarmBriefVO();
        vo.setId(alarm.getId());
        vo.setAlarmNo(alarm.getAlarmNo());
        vo.setAlarmType(alarm.getAlarmType());
        vo.setAlarmTypeName(AlarmTypeEnum.getDescByCode(alarm.getAlarmType()));
        vo.setAlarmLevel(alarm.getAlarmLevel());
        vo.setAlarmLevelName(AlarmLevelEnum.getDescByCode(alarm.getAlarmLevel()));
        vo.setAlarmTitle(alarm.getAlarmTitle());
        vo.setLocation(alarm.getLocation());
        vo.setLongitude(alarm.getLongitude());
        vo.setLatitude(alarm.getLatitude());
        vo.setCreatedAt(alarm.getCreatedAt());
        vo.setStatus(alarm.getStatus());
        vo.setStatusName(AlarmStatusEnum.getDescByCode(alarm.getStatus()));

        return vo;
    }

    @Override
    public CommandDashboardVO.SchoolDetailVO getSchoolDetail(Long schoolId) {
        if (schoolId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "schoolId");
        }

        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        CommandDashboardVO.SchoolDetailVO vo = new CommandDashboardVO.SchoolDetailVO();
        vo.setId(school.getId());
        vo.setSchoolName(school.getSchoolName());
        vo.setSchoolType(school.getSchoolType());
        vo.setSchoolTypeName(SchoolTypeEnum.getDescByCode(school.getSchoolType()));
        vo.setSchoolLevel(school.getSchoolLevel());
        vo.setSchoolLevelName(getSchoolLevelName(school.getSchoolLevel()));
        vo.setAddress(school.getAddress());
        vo.setLongitude(school.getLongitude());
        vo.setLatitude(school.getLatitude());
        vo.setPrincipal(school.getPrincipal());
        vo.setSecurityLeader(school.getSecurityLeader());
        vo.setContactPhone(school.getSecurityPhone() != null ? school.getSecurityPhone() : school.getPrincipalPhone());
        vo.setStudentCount(school.getStudentCount());
        vo.setDeviceCount(school.getDeviceCount());

        return vo;
    }

    @Override
    public CommandDashboardVO.PoliceStationVO getNearbyPoliceStation(Long schoolId) {
        if (schoolId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "schoolId");
        }

        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        CommandDashboardVO.PoliceStationVO result = null;

        if (school.getPoliceStationId() != null) {
            PoliceStation station = policeStationMapper.selectById(school.getPoliceStationId());
            if (station != null) {
                result = convertToPoliceStationVO(station);
                result.setDistanceKm(calculateDistanceKm(
                        school.getLongitude(), school.getLatitude(),
                        station.getLongitude(), station.getLatitude()
                ));
                return result;
            }
        }

        if (school.getTownId() != null) {
            List<CommandDashboardVO.PoliceStationVO> stations = commandMapper.selectPoliceStationByTownId(school.getTownId());
            if (stations != null && !stations.isEmpty()) {
                PoliceStation station = policeStationMapper.selectById(stations.get(0).getId());
                if (station != null) {
                    result = convertToPoliceStationVO(station);
                    result.setDistanceKm(calculateDistanceKm(
                            school.getLongitude(), school.getLatitude(),
                            station.getLongitude(), station.getLatitude()
                    ));
                    return result;
                }
            }
        }

        LambdaQueryWrapper<PoliceStation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PoliceStation::getStatus, 1)
                .last("LIMIT 1");
        PoliceStation station = policeStationMapper.selectOne(wrapper);
        if (station != null) {
            result = convertToPoliceStationVO(station);
            result.setDistanceKm(calculateDistanceKm(
                    school.getLongitude(), school.getLatitude(),
                    station.getLongitude(), station.getLatitude()
            ));
        }

        return result;
    }

    @Override
    public List<CommandDashboardVO.AlarmHistoryVO> getAlarmHistory(Long schoolId, int limit) {
        if (schoolId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "schoolId");
        }
        if (limit <= 0) {
            limit = 10;
        }
        return commandMapper.selectAlarmHistoryBySchoolId(schoolId, limit, null);
    }

    @Override
    public CommandDashboardVO.RiskPortraitVO getRiskPortrait(Long schoolId) {
        if (schoolId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "schoolId");
        }

        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        String statMonth = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        LambdaQueryWrapper<SchoolRisk> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolRisk::getSchoolId, schoolId)
                .eq(SchoolRisk::getStatMonth, statMonth)
                .orderByDesc(SchoolRisk::getCreatedAt)
                .last("LIMIT 1");
        SchoolRisk risk = schoolRiskMapper.selectOne(wrapper);

        CommandDashboardVO.RiskPortraitVO vo = new CommandDashboardVO.RiskPortraitVO();
        vo.setStatMonth(statMonth);

        if (risk != null) {
            vo.setTotalAlarms(risk.getTotalAlarms());
            vo.setCriticalAlarms(risk.getCriticalAlarms());
            vo.setAvgResponseTime(risk.getAvgResponseTime());
            vo.setRiskScore(risk.getRiskScore());
            vo.setRiskLevel(risk.getRiskLevel());
            vo.setRiskLevelName(RiskLevelEnum.getDescByCode(risk.getRiskLevel()));
        } else {
            vo.setTotalAlarms(0);
            vo.setCriticalAlarms(0);
            vo.setAvgResponseTime(0);
            vo.setRiskScore(BigDecimal.ZERO);
            vo.setRiskLevel(RiskLevelEnum.LOW.getCode());
            vo.setRiskLevelName(RiskLevelEnum.LOW.getDesc());
        }

        vo.setMainRiskPoints(generateRiskPoints(vo.getTotalAlarms(), vo.getCriticalAlarms(), school.getSchoolLevel()));

        return vo;
    }

    @Override
    public CommandDashboardVO.NotifyLinkVO getNotifyLink(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        CommandDashboardVO.NotifyLinkVO vo = new CommandDashboardVO.NotifyLinkVO();

        List<Map<String, Object>> statusSummary = commandMapper.selectNotifyStatusSummary(alarmId);
        List<CommandDashboardVO.ChannelStatusVO> channelList = new ArrayList<>();
        int totalTarget = 0;
        int totalSent = 0;

        if (statusSummary != null && !statusSummary.isEmpty()) {
            for (Map<String, Object> row : statusSummary) {
                CommandDashboardVO.ChannelStatusVO channel = new CommandDashboardVO.ChannelStatusVO();
                Integer notifyType = row.get("notifyType") != null
                        ? ((Number) row.get("notifyType")).intValue() : null;
                channel.setChannelType(notifyType);
                channel.setChannelName(getNotifyTypeName(notifyType));
                int total = row.get("total") != null ? ((Number) row.get("total")).intValue() : 0;
                int sent = row.get("sent") != null ? ((Number) row.get("sent")).intValue() : 0;
                int failed = row.get("failed") != null ? ((Number) row.get("failed")).intValue() : 0;
                channel.setTotal(total);
                channel.setSent(sent);
                channel.setFailed(failed);
                channelList.add(channel);
                totalTarget += total;
                totalSent += sent;
            }
        }

        vo.setTargetTotal(totalTarget);
        vo.setSentCount(totalSent);
        vo.setChannelStatusList(channelList);

        vo.setReadList(commandMapper.selectNotifyDetailList(alarmId, 1));
        vo.setFailedList(commandMapper.selectNotifyDetailList(alarmId, 2));
        vo.setUnreadList(commandMapper.selectNotifyDetailList(alarmId, 0));

        List<CommandDashboardVO.NotifyUserVO> allSent = commandMapper.selectNotifyDetailList(alarmId, 1);
        List<CommandDashboardVO.NotifyUserVO> read = new ArrayList<>();
        List<CommandDashboardVO.NotifyUserVO> unread = new ArrayList<>();
        if (allSent != null) {
            for (CommandDashboardVO.NotifyUserVO u : allSent) {
                if (u.getReadAt() != null) {
                    read.add(u);
                } else {
                    unread.add(u);
                }
            }
        }
        vo.setReadList(read);
        vo.setUnreadList(unread);

        return vo;
    }

    @Override
    public CommandDashboardVO.SuperviseProgressVO getSuperviseProgress(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(alarmId);
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        CommandDashboardVO.SuperviseProgressVO vo = new CommandDashboardVO.SuperviseProgressVO();
        vo.setCurrentStatus(alarm.getStatus());
        vo.setCurrentStatusName(AlarmStatusEnum.getDescByCode(alarm.getStatus()));
        vo.setHandlerId(alarm.getHandlerId());
        vo.setFeedbackTime(alarm.getHandledAt());
        vo.setSupervisorId(alarm.getSupervisorId());

        List<AlarmFlow> flowList = alarmFlowService.getFlowByAlarmId(alarmId);
        List<CommandDashboardVO.FlowStepVO> steps = new ArrayList<>();
        LocalDateTime superviseTime = null;
        String supervisorName = null;
        String handlerName = null;

        if (flowList != null) {
            for (AlarmFlow flow : flowList) {
                CommandDashboardVO.FlowStepVO step = new CommandDashboardVO.FlowStepVO();
                step.setId(flow.getId());
                step.setFlowType(flow.getFlowType());
                step.setFlowTypeName(FlowTypeEnum.getDescByCode(flow.getFlowType()));
                step.setOperatorId(flow.getOperatorId());
                step.setOperatorName(flow.getOperatorName());
                step.setOperatorRole(flow.getOperatorRole());
                step.setPartyType(flow.getPartyType());
                step.setPartyName(flow.getPartyName());
                step.setDurationSeconds(flow.getDurationSeconds());
                step.setRemark(flow.getRemark());
                step.setAttachUrl(flow.getAttachUrl());
                step.setCreatedAt(flow.getCreatedAt());
                steps.add(step);

                if (FlowTypeEnum.SUPERVISE_CREATE.getCode().equals(flow.getFlowType())) {
                    superviseTime = flow.getCreatedAt();
                    supervisorName = flow.getOperatorName();
                }
                if (FlowTypeEnum.HANDLE_FEEDBACK.getCode().equals(flow.getFlowType())
                        && handlerName == null) {
                    handlerName = flow.getOperatorName();
                }
            }
        }

        vo.setSuperviseTime(superviseTime);
        vo.setSupervisorName(supervisorName);
        vo.setHandlerName(handlerName);
        vo.setFlowSteps(steps);

        return vo;
    }

    @Override
    public List<CommandDashboardVO.SupplementNoteVO> getSupplementNotes(Long alarmId) {
        if (alarmId == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }
        return commandMapper.selectSupplementNotes(alarmId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean remindAlarm(CommandRemindDTO dto) {
        if (dto == null || dto.getAlarmId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(dto.getAlarmId());
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(alarm.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_ALREADY_CLOSED);
        }
        if (AlarmStatusEnum.HANDLED.getCode().equals(alarm.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_ALREADY_HANDLED);
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long operatorId = loginUser != null ? loginUser.getUserId() : null;
        String operatorName = loginUser != null ? loginUser.getRealName() : null;
        String operatorRole = loginUser != null && loginUser.getRoleType() != null
                ? loginUser.getRoleType().getDesc() : RoleTypeEnum.EDUCATION_BUREAU.getDesc();
        Integer partyType = PartyTypeEnum.EDUCATION_BUREAU.getCode();
        Long partyId = loginUser != null ? loginUser.getUserId() : null;
        String partyName = PartyTypeEnum.EDUCATION_BUREAU.getDesc();

        String remark = "催办：" + (dto.getRemindContent() != null ? dto.getRemindContent() : "请尽快处置警情");

        alarmFlowService.addFlowRecord(dto.getAlarmId(), FlowTypeEnum.REMIND_MANUAL.getCode(),
                operatorId, operatorName, operatorRole,
                partyType, partyId, partyName, remark, null);

        superviseService.manualRemind(dto.getAlarmId());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferAlarm(CommandTransferDTO dto) {
        if (dto == null) {
            throw BusinessException.of(ResultCode.REQUEST_BODY_EMPTY);
        }
        if (dto.getAlarmId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }
        if (dto.getTargetSchoolId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "targetSchoolId");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(dto.getAlarmId());
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }
        if (AlarmStatusEnum.CLOSED.getCode().equals(alarm.getStatus())) {
            throw BusinessException.of(ResultCode.ALARM_STATUS_NOT_ALLOWED, "已关闭的警情不能转派");
        }

        SchoolInfo targetSchool = schoolInfoMapper.selectById(dto.getTargetSchoolId());
        if (targetSchool == null) {
            throw BusinessException.of(ResultCode.SCHOOL_NOT_FOUND);
        }

        com.safetycampus.supervise.dto.AlarmTransferDTO transferDTO = new com.safetycampus.supervise.dto.AlarmTransferDTO();
        transferDTO.setAlarmId(dto.getAlarmId());
        transferDTO.setTargetSchoolId(dto.getTargetSchoolId());
        transferDTO.setTargetSchoolName(targetSchool.getSchoolName());
        transferDTO.setTransferReason(dto.getTransferReason());
        LoginUser loginUser = UserContext.getLoginUser();
        if (loginUser != null) {
            transferDTO.setOperatorId(loginUser.getUserId());
            transferDTO.setOperatorName(loginUser.getRealName());
        }

        return superviseService.transferAlarm(transferDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addNote(CommandNoteDTO dto) {
        if (dto == null) {
            throw BusinessException.of(ResultCode.REQUEST_BODY_EMPTY);
        }
        if (dto.getAlarmId() == null) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "alarmId");
        }
        if (dto.getNoteContent() == null || dto.getNoteContent().trim().isEmpty()) {
            throw BusinessException.of(ResultCode.PARAM_MISSING, "noteContent");
        }

        AlarmRecord alarm = alarmRecordMapper.selectById(dto.getAlarmId());
        if (alarm == null) {
            throw BusinessException.of(ResultCode.ALARM_NOT_FOUND);
        }

        LoginUser loginUser = UserContext.getLoginUser();
        Long operatorId = loginUser != null ? loginUser.getUserId() : null;
        String operatorName = loginUser != null ? loginUser.getRealName() : null;
        String operatorRole = loginUser != null && loginUser.getRoleType() != null
                ? loginUser.getRoleType().getDesc() : "系统";

        AlarmSupplementNote note = new AlarmSupplementNote();
        note.setAlarmId(dto.getAlarmId());
        note.setOperatorId(operatorId);
        note.setOperatorName(operatorName);
        note.setOperatorRole(operatorRole);
        note.setNoteContent(dto.getNoteContent());
        note.setAttachUrl(dto.getAttachUrl());
        note.setIsImportant(dto.getIsImportant() != null ? dto.getIsImportant() : 0);
        note.setCreatedAt(LocalDateTime.now());
        alarmSupplementNoteMapper.insert(note);

        Integer partyType = PartyTypeEnum.SYSTEM.getCode();
        Long partyId = null;
        String partyName = PartyTypeEnum.SYSTEM.getDesc();
        if (loginUser != null) {
            if (RoleTypeEnum.EDUCATION_BUREAU.equals(loginUser.getRoleType())) {
                partyType = PartyTypeEnum.EDUCATION_BUREAU.getCode();
                partyId = loginUser.getUserId();
                partyName = PartyTypeEnum.EDUCATION_BUREAU.getDesc();
            } else if (RoleTypeEnum.SCHOOL_SECURITY.equals(loginUser.getRoleType())) {
                partyType = PartyTypeEnum.SCHOOL.getCode();
                partyId = loginUser.getSchoolId() != null ? loginUser.getSchoolId() : alarm.getSchoolId();
                SchoolInfo school = schoolInfoMapper.selectById(partyId);
                partyName = school != null ? school.getSchoolName() : PartyTypeEnum.SCHOOL.getDesc();
            }
        }

        String remark = (note.getIsImportant() != null && note.getIsImportant() == 1 ? "[重要] " : "")
                + "补充说明：" + dto.getNoteContent();

        alarmFlowService.addFlowRecord(dto.getAlarmId(), 10,
                operatorId, operatorName, operatorRole,
                partyType, partyId, partyName, remark, dto.getAttachUrl());

        return true;
    }

    private CommandDashboardVO.PoliceStationVO convertToPoliceStationVO(PoliceStation station) {
        CommandDashboardVO.PoliceStationVO vo = new CommandDashboardVO.PoliceStationVO();
        vo.setId(station.getId());
        vo.setStationName(station.getStationName());
        vo.setLiaison(station.getLiaison());
        vo.setLiaisonPhone(station.getLiaisonPhone());
        vo.setDutyPhone(station.getDutyPhone());
        vo.setAddress(station.getAddress());
        return vo;
    }

    private BigDecimal calculateDistanceKm(BigDecimal lon1, BigDecimal lat1, BigDecimal lon2, BigDecimal lat2) {
        if (lon1 == null || lat1 == null || lon2 == null || lat2 == null) {
            return null;
        }
        double earthRadius = 6371.0;
        double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
        double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    private String getSchoolLevelName(Integer level) {
        if (level == null) return "未知";
        switch (level) {
            case 1: return "重点";
            case 2: return "关注";
            case 3: return "普通";
            default: return "未知";
        }
    }

    private List<String> generateRiskPoints(Integer totalAlarms, Integer criticalAlarms, Integer schoolLevel) {
        List<String> points = new ArrayList<>();
        totalAlarms = totalAlarms != null ? totalAlarms : 0;
        criticalAlarms = criticalAlarms != null ? criticalAlarms : 0;

        if (totalAlarms >= 10) {
            points.add("本月报警频次偏高，需加强日常巡查");
        } else if (totalAlarms >= 5) {
            points.add("本月报警处于中等水平，建议关注重点区域");
        } else if (totalAlarms > 0) {
            points.add("本月报警频次较低，整体防控情况良好");
        } else {
            points.add("本月暂无报警记录，安全状况良好");
        }

        if (criticalAlarms >= 3) {
            points.add("重大警情数量较多，需提高应急响应能力");
        } else if (criticalAlarms > 0) {
            points.add("存在重大警情，需重点关注处置流程");
        }

        if (schoolLevel != null && schoolLevel == 1) {
            points.add("学校为重点监管等级，需保持高度警惕");
        } else if (schoolLevel != null && schoolLevel == 2) {
            points.add("学校为关注等级，建议定期开展安全演练");
        }

        if (points.isEmpty()) {
            points.add("学校整体安全状况良好");
        }

        return points;
    }

    private String getNotifyTypeName(Integer type) {
        if (type == null) return "未知";
        switch (type) {
            case 1: return "短信";
            case 2: return "APP推送";
            case 3: return "电话";
            case 4: return "邮件";
            default: return "未知";
        }
    }
}
