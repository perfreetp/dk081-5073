package com.safetycampus.notifyrule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.safetycampus.common.exception.BusinessException;
import com.safetycampus.common.result.ResultCode;
import com.safetycampus.notifyrule.dto.NotifyRuleDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleQueryDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestDTO;
import com.safetycampus.notifyrule.dto.NotifyRuleTestResultVO;
import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;
import com.safetycampus.notifyrule.mapper.NotifyRuleMapper;
import com.safetycampus.notifyrule.mapper.NotifyRuleTargetMapper;
import com.safetycampus.notifyrule.service.NotifyRuleEngine;
import com.safetycampus.notifyrule.service.NotifyRuleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class NotifyRuleServiceImpl extends ServiceImpl<NotifyRuleMapper, NotifyRule> implements NotifyRuleService {

    @Resource
    private NotifyRuleMapper notifyRuleMapper;

    @Resource
    private NotifyRuleTargetMapper notifyRuleTargetMapper;

    @Resource
    private NotifyRuleEngine notifyRuleEngine;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createRule(NotifyRuleDTO dto) {
        if (dto.getRuleName() != null) {
            LambdaQueryWrapper<NotifyRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NotifyRule::getRuleName, dto.getRuleName());
            Long count = this.count(wrapper);
            if (count > 0) {
                throw BusinessException.of(ResultCode.RULE_NAME_EXISTS);
            }
        }
        NotifyRule rule = new NotifyRule();
        BeanUtils.copyProperties(dto, rule);
        if (rule.getPriority() == null) {
            rule.setPriority(0);
        }
        if (rule.getIsEnabled() == null) {
            rule.setIsEnabled(1);
        }
        if (rule.getHolidayMode() == null) {
            rule.setHolidayMode(0);
        }
        if (rule.getNightMode() == null) {
            rule.setNightMode(0);
        }
        boolean saved = this.save(rule);
        if (!saved) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        if (dto.getTargets() != null && !dto.getTargets().isEmpty()) {
            for (NotifyRuleTarget target : dto.getTargets()) {
                target.setRuleId(rule.getId());
                if (target.getSortOrder() == null) {
                    target.setSortOrder(0);
                }
                notifyRuleTargetMapper.insert(target);
            }
        }
        notifyRuleEngine.refreshCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRule(NotifyRuleDTO dto) {
        NotifyRule existing = this.getById(dto.getId());
        if (existing == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        if (dto.getRuleName() != null && !dto.getRuleName().equals(existing.getRuleName())) {
            LambdaQueryWrapper<NotifyRule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(NotifyRule::getRuleName, dto.getRuleName());
            wrapper.ne(NotifyRule::getId, dto.getId());
            Long count = this.count(wrapper);
            if (count > 0) {
                throw BusinessException.of(ResultCode.RULE_NAME_EXISTS);
            }
        }
        NotifyRule rule = new NotifyRule();
        BeanUtils.copyProperties(dto, rule);
        boolean updated = this.updateById(rule);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        if (dto.getTargets() != null) {
            notifyRuleTargetMapper.delete(
                    new LambdaQueryWrapper<NotifyRuleTarget>()
                            .eq(NotifyRuleTarget::getRuleId, dto.getId())
            );
            for (NotifyRuleTarget target : dto.getTargets()) {
                target.setRuleId(dto.getId());
                if (target.getSortOrder() == null) {
                    target.setSortOrder(0);
                }
                notifyRuleTargetMapper.insert(target);
            }
        }
        notifyRuleEngine.refreshCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void toggleRule(Long id, Boolean enabled) {
        NotifyRule rule = this.getById(id);
        if (rule == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        if (enabled && rule.getIsEnabled() == 1) {
            throw BusinessException.of(ResultCode.RULE_ALREADY_ENABLED);
        }
        rule.setIsEnabled(enabled ? 1 : 0);
        boolean updated = this.updateById(rule);
        if (!updated) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        notifyRuleEngine.refreshCache();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRule(Long id) {
        NotifyRule rule = this.getById(id);
        if (rule == null) {
            throw BusinessException.of(ResultCode.PARAM_VALIDATE_ERROR);
        }
        boolean removed = this.removeById(id);
        if (!removed) {
            throw BusinessException.of(ResultCode.DATABASE_ERROR);
        }
        notifyRuleTargetMapper.delete(
                new LambdaQueryWrapper<NotifyRuleTarget>()
                        .eq(NotifyRuleTarget::getRuleId, id)
        );
        notifyRuleEngine.refreshCache();
    }

    @Override
    public NotifyRule getRuleDetail(Long id) {
        return this.getById(id);
    }

    @Override
    public IPage<NotifyRule> selectPage(NotifyRuleQueryDTO queryDTO) {
        List<NotifyRule> rules = notifyRuleMapper.selectPageByCondition(queryDTO);
        Long total = notifyRuleMapper.countByCondition(queryDTO);
        IPage<NotifyRule> page = queryDTO.buildPage();
        page.setRecords(rules);
        page.setTotal(total != null ? total : 0);
        return page;
    }

    @Override
    public NotifyRuleTestResultVO testMatchRule(NotifyRuleTestDTO dto) {
        List<NotifyRule> matchedRules = notifyRuleEngine.matchRules(
                dto.getSchoolId(),
                dto.getAlarmLevel(),
                dto.getAlarmType(),
                dto.getIsHoliday(),
                dto.getIsNight()
        );

        List<NotifyRuleTarget> targets = notifyRuleEngine.getTargetsByRules(matchedRules);
        List<Integer> channels = notifyRuleEngine.getNotifyChannelsByRules(matchedRules);
        String template = notifyRuleEngine.getNotifyTemplateByRules(matchedRules);

        NotifyRuleTestResultVO vo = new NotifyRuleTestResultVO();
        vo.setMatchedRules(matchedRules);
        vo.setTargets(targets);
        vo.setNotifyChannels(channels);
        vo.setNotifyTemplate(template);
        return vo;
    }

    @Override
    public List<NotifyRuleTarget> matchAndGetTargets(Long schoolId, Integer alarmLevel, Integer alarmType) {
        return notifyRuleEngine.matchAndGetTargets(schoolId, alarmLevel, alarmType);
    }
}
