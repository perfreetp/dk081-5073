package com.safetycampus.command.mapper;

import com.safetycampus.command.vo.CommandDashboardVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CommandMapper {

    List<CommandDashboardVO.AlarmHistoryVO> selectAlarmHistoryBySchoolId(
            @Param("schoolId") Long schoolId,
            @Param("limit") int limit,
            @Param("excludeAlarmId") Long excludeAlarmId);

    List<Map<String, Object>> selectNotifyStatusSummary(@Param("alarmId") Long alarmId);

    List<CommandDashboardVO.NotifyUserVO> selectNotifyDetailList(
            @Param("alarmId") Long alarmId,
            @Param("status") Integer status);

    List<CommandDashboardVO.PoliceStationVO> selectPoliceStationByTownId(@Param("townId") Long townId);

    List<CommandDashboardVO.SupplementNoteVO> selectSupplementNotes(@Param("alarmId") Long alarmId);
}
