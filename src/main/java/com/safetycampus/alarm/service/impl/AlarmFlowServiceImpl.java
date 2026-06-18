package com.safetycampus.alarm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.alarm.dto.AlarmTimelineQueryDTO;
import com.safetycampus.alarm.dto.AlarmTimelineSummaryVO;
import com.safetycampus.alarm.dto.AlarmTimelineVO;
import com.safetycampus.alarm.entity.AlarmFlow;
import com.safetycampus.alarm.mapper.AlarmFlowMapper;
import com.safetycampus.alarm.service.AlarmFlowService;
import com.safetycampus.common.enums.PartyTypeEnum;
import com.safetycampus.supervise.enums.FlowTypeEnum;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AlarmFlowServiceImpl extends ServiceImpl<AlarmFlowMapper, AlarmFlow> implements AlarmFlowService {

    private static final Set<Integer> KEY_NODE_TYPES = new HashSet<>(Arrays.asList(
            FlowTypeEnum.SUPERVISE_CREATE.getCode(),
            FlowTypeEnum.HANDLE_FEEDBACK.getCode(),
            FlowTypeEnum.REMIND_TIMEOUT.getCode(),
            FlowTypeEnum.REMIND_MANUAL.getCode(),
            FlowTypeEnum.ALARM_CLOSE.getCode()
    ));

    @Override
    public List<AlarmFlow> getFlowByAlarmId(Long alarmId) {
        LambdaQueryWrapper<AlarmFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AlarmFlow::getAlarmId, alarmId);
        wrapper.orderByAsc(AlarmFlow::getCreatedAt);
        return list(wrapper);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, String remark) {
        addFlowRecord(alarmId, flowType, null, null, null, null, null, null, remark, null);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                              String operatorRole, String remark) {
        addFlowRecord(alarmId, flowType, operatorId, operatorName, operatorRole, null, null, null, remark, null);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                              String operatorRole, String remark, String attachUrl) {
        addFlowRecord(alarmId, flowType, operatorId, operatorName, operatorRole, null, null, null, remark, attachUrl);
    }

    @Override
    public void addFlowRecord(Long alarmId, Integer flowType, Long operatorId, String operatorName,
                              String operatorRole, Integer partyType, Long partyId, String partyName,
                              String remark, String attachUrl) {
        AlarmFlow flow = new AlarmFlow();
        flow.setAlarmId(alarmId);
        flow.setFlowType(flowType);
        flow.setOperatorId(operatorId);
        flow.setOperatorName(operatorName);
        flow.setOperatorRole(operatorRole);
        flow.setPartyType(partyType);
        flow.setPartyId(partyId);
        flow.setPartyName(partyName);
        flow.setRemark(remark);
        flow.setAttachUrl(attachUrl);
        flow.setCreatedAt(LocalDateTime.now());
        save(flow);
    }

    @Override
    public List<AlarmTimelineVO> getAlarmTimeline(AlarmTimelineQueryDTO queryDTO) {
        List<AlarmFlow> flowList = baseMapper.selectTimelineByQuery(queryDTO);
        if (flowList.isEmpty()) {
            return Collections.emptyList();
        }

        List<AlarmTimelineVO> timelineList = new ArrayList<>();
        LocalDateTime previousTime = null;

        for (AlarmFlow flow : flowList) {
            AlarmTimelineVO vo = new AlarmTimelineVO();
            vo.setId(flow.getId());
            vo.setFlowType(flow.getFlowType());
            vo.setFlowTypeName(FlowTypeEnum.getDescByCode(flow.getFlowType()));
            vo.setPartyType(flow.getPartyType());
            vo.setPartyTypeName(PartyTypeEnum.getDescByCode(flow.getPartyType()));
            vo.setPartyName(flow.getPartyName());
            vo.setOperatorId(flow.getOperatorId());
            vo.setOperatorName(flow.getOperatorName());
            vo.setOperatorRole(flow.getOperatorRole());
            vo.setRemark(flow.getRemark());
            vo.setAttachUrl(flow.getAttachUrl());
            vo.setCreatedAt(flow.getCreatedAt());
            vo.setTimestamp(flow.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            vo.setIsKeyNode(KEY_NODE_TYPES.contains(flow.getFlowType()));
            vo.setStatusIcon(getStatusIcon(flow.getFlowType()));

            if (previousTime != null) {
                int durationSeconds = (int) Duration.between(previousTime, flow.getCreatedAt()).getSeconds();
                vo.setDurationSeconds(durationSeconds);
                vo.setDurationText(formatDuration(durationSeconds));
            } else {
                vo.setDurationSeconds(0);
                vo.setDurationText("开始");
            }

            timelineList.add(vo);
            previousTime = flow.getCreatedAt();
        }

        return timelineList;
    }

    @Override
    public AlarmTimelineSummaryVO getTimelineSummary(Long alarmId) {
        AlarmTimelineQueryDTO queryDTO = new AlarmTimelineQueryDTO();
        queryDTO.setAlarmId(alarmId);
        List<AlarmFlow> flowList = baseMapper.selectTimelineByQuery(queryDTO);

        AlarmTimelineSummaryVO summary = new AlarmTimelineSummaryVO();
        if (flowList.isEmpty()) {
            summary.setTotalDurationSeconds(0);
            summary.setTotalDurationText("0秒");
            summary.setPartyDurationMap(new HashMap<>());
            summary.setPartyDurationTextMap(new HashMap<>());
            summary.setKeyNodeCount(0);
            summary.setTotalNodeCount(0);
            return summary;
        }

        flowList.sort(Comparator.comparing(AlarmFlow::getCreatedAt));

        LocalDateTime firstTime = flowList.get(0).getCreatedAt();
        LocalDateTime lastTime = flowList.get(flowList.size() - 1).getCreatedAt();
        int totalDuration = (int) Duration.between(firstTime, lastTime).getSeconds();
        summary.setTotalDurationSeconds(totalDuration);
        summary.setTotalDurationText(formatDuration(totalDuration));

        Map<Integer, Integer> partyDurationMap = new HashMap<>();
        Map<String, Integer> partyDurationTextMap = new HashMap<>();
        int keyNodeCount = 0;

        LocalDateTime previousTime = null;
        for (AlarmFlow flow : flowList) {
            if (KEY_NODE_TYPES.contains(flow.getFlowType())) {
                keyNodeCount++;
            }

            if (previousTime != null && flow.getPartyType() != null) {
                int duration = (int) Duration.between(previousTime, flow.getCreatedAt()).getSeconds();
                partyDurationMap.merge(flow.getPartyType(), duration, Integer::sum);
            }

            previousTime = flow.getCreatedAt();
        }

        for (Map.Entry<Integer, Integer> entry : partyDurationMap.entrySet()) {
            String partyName = PartyTypeEnum.getDescByCode(entry.getKey());
            if (partyName != null) {
                partyDurationTextMap.put(partyName, entry.getValue());
            }
        }

        summary.setPartyDurationMap(partyDurationMap);
        summary.setPartyDurationTextMap(partyDurationTextMap);
        summary.setKeyNodeCount(keyNodeCount);
        summary.setTotalNodeCount(flowList.size());

        return summary;
    }

    private String getStatusIcon(Integer flowType) {
        if (flowType == null) {
            return "circle";
        }
        FlowTypeEnum typeEnum = FlowTypeEnum.values()[0];
        for (FlowTypeEnum e : FlowTypeEnum.values()) {
            if (e.getCode().equals(flowType)) {
                typeEnum = e;
                break;
            }
        }
        switch (typeEnum) {
            case ALARM_RECEIVE:
                return "bell";
            case SUPERVISE_CREATE:
                return "flag";
            case HANDLE_FEEDBACK:
                return "check-circle";
            case ALARM_TRANSFER:
                return "arrow-right";
            case ALARM_CLOSE:
                return "lock";
            case REMIND_TIMEOUT:
            case REMIND_MANUAL:
                return "alert";
            case ALARM_MERGE:
                return "merge";
            case ALARM_ESCALATE:
                return "arrow-up";
            default:
                return "circle";
        }
    }

    private String formatDuration(int seconds) {
        if (seconds < 0) {
            return "0秒";
        }
        if (seconds < 60) {
            return seconds + "秒";
        }
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        if (minutes < 60) {
            return minutes + "分钟" + (remainingSeconds > 0 ? remainingSeconds + "秒" : "");
        }
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (hours < 24) {
            return hours + "小时" + (remainingMinutes > 0 ? remainingMinutes + "分钟" : "");
        }
        int days = hours / 24;
        int remainingHours = hours % 24;
        return days + "天" + (remainingHours > 0 ? remainingHours + "小时" : "");
    }
}
