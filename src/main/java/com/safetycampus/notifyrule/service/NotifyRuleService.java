package com.safetycampus.notifyrule.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.notifyrule.dto.NotifyRuleDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleQueryDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestResultVO;
import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;

import java.util.List;

public interface NotifyRuleService extends IService<NotifyRule> {

    void createRule(NotifyRuleDTO dto);

    void updateRule(NotifyRuleDTO dto);

    void toggleRule(Long id, Boolean enabled);

    void deleteRule(Long id);

    NotifyRule getRuleDetail(Long id);

    IPage<NotifyRule> selectPage(NotifyRuleQueryDTO queryDTO);

    NotifyRuleTestResultVO testMatchRule(NotifyRuleTestDTO dto);

    List<NotifyRuleTarget> matchAndGetTargets(Long schoolId, Integer alarmLevel, Integer alarmType);
}
