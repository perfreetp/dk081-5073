package com.safetycampus.command.service;

import com.safetycampus.command.dto.CommandNoteDTO;
import com.safetycampus.command.dto.CommandRemindDTO;
import com.safetycampus.command.dto.CommandTransferDTO;
import com.safetycampus.command.vo.CommandDashboardVO;

import java.util.List;

public interface CommandService {

    CommandDashboardVO getCommandDashboard(Long alarmId);

    CommandDashboardVO.AlarmBriefVO getAlarmBrief(Long alarmId);

    CommandDashboardVO.SchoolDetailVO getSchoolDetail(Long schoolId);

    CommandDashboardVO.PoliceStationVO getNearbyPoliceStation(Long schoolId);

    List<CommandDashboardVO.AlarmHistoryVO> getAlarmHistory(Long schoolId, int limit);

    CommandDashboardVO.RiskPortraitVO getRiskPortrait(Long schoolId);

    CommandDashboardVO.NotifyLinkVO getNotifyLink(Long alarmId);

    CommandDashboardVO.SuperviseProgressVO getSuperviseProgress(Long alarmId);

    List<CommandDashboardVO.SupplementNoteVO> getSupplementNotes(Long alarmId);

    boolean remindAlarm(CommandRemindDTO dto);

    boolean transferAlarm(CommandTransferDTO dto);

    boolean addNote(CommandNoteDTO dto);
}
