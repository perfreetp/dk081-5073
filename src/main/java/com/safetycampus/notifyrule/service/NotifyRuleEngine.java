package com.safetycampus.notifyrule.service;

import com.safetycampus.alarm.entity.HolidayConfig;
import com.safetycampus.alarm.mapper.HolidayConfigMapper;
import com.safetycampus.notifyrule.entity.NotifyRule;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;
import com.safetycampus.notifyrule.enums.RuleTypeEnum;
import com.safetycampus.notifyrule.mapper.NotifyRuleMapper;
import com.safetycampus.notifyrule.mapper.NotifyRuleTargetMapper;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NotifyRuleEngine {

    @Resource
    private NotifyRuleMapper notifyRuleMapper;

    @Resource
    private NotifyRuleTargetMapper notifyRuleTargetMapper;

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private HolidayConfigMapper holidayConfigMapper;

    private final List<NotifyRule> ruleCache = new ArrayList<>();

    private final Map<Long, List<NotifyRuleTarget>> targetCache = new ConcurrentHashMap<>();

    private volatile long lastRefreshTime = 0;

    private static final long REFRESH_INTERVAL = 5 * 60 * 1000;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    public synchronized void refreshCache() {
        log.info("开始刷新通知规则缓存");
        try {
            List<NotifyRule> rules = notifyRuleMapper.selectAllEnabledRules();
            ruleCache.clear();
            ruleCache.addAll(rules);

            if (!rules.isEmpty()) {
                List<Long> ruleIds = rules.stream().map(NotifyRule::getId).collect(Collectors.toList());
                List<NotifyRuleTarget> allTargets = notifyRuleTargetMapper.selectByRuleIds(ruleIds);
                targetCache.clear();
                Map<Long, List<NotifyRuleTarget>> grouped = allTargets.stream()
                        .collect(Collectors.groupingBy(NotifyRuleTarget::getRuleId));
                targetCache.putAll(grouped);
            }

            lastRefreshTime = System.currentTimeMillis();
            log.info("通知规则缓存刷新完成，共加载{}条规则", ruleCache.size());
        } catch (Exception e) {
            log.error("刷新通知规则缓存失败", e);
        }
    }

    private void checkRefresh() {
        if (System.currentTimeMillis() - lastRefreshTime > REFRESH_INTERVAL) {
            refreshCache();
        }
    }

    public List<NotifyRule> matchRules(Long schoolId, Integer alarmLevel, Integer alarmType) {
        checkRefresh();

        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            log.warn("学校不存在，schoolId: {}", schoolId);
            return Collections.emptyList();
        }

        boolean isHoliday = checkIsHoliday();
        boolean isNight = checkIsNight();
        Integer schoolType = school.getSchoolType();

        List<NotifyRule> matchedRules = new ArrayList<>();

        for (NotifyRule rule : ruleCache) {
            if (matchRule(rule, isHoliday, isNight, schoolType, alarmLevel)) {
                matchedRules.add(rule);
            }
        }

        sortRulesByPriority(matchedRules);

        log.debug("规则匹配完成 - schoolId: {}, alarmLevel: {}, isHoliday: {}, isNight: {}, 匹配规则数: {}",
                schoolId, alarmLevel, isHoliday, isNight, matchedRules.size());

        return matchedRules;
    }

    public List<NotifyRule> matchRules(Long schoolId, Integer alarmLevel, Integer alarmType,
                                       Boolean isHoliday, Boolean isNight) {
        checkRefresh();

        SchoolInfo school = schoolInfoMapper.selectById(schoolId);
        if (school == null) {
            log.warn("学校不存在，schoolId: {}", schoolId);
            return Collections.emptyList();
        }

        boolean holiday = isHoliday != null ? isHoliday : checkIsHoliday();
        boolean night = isNight != null ? isNight : checkIsNight();
        Integer schoolType = school.getSchoolType();

        List<NotifyRule> matchedRules = new ArrayList<>();

        for (NotifyRule rule : ruleCache) {
            if (matchRule(rule, holiday, night, schoolType, alarmLevel)) {
                matchedRules.add(rule);
            }
        }

        sortRulesByPriority(matchedRules);

        return matchedRules;
    }

    public List<NotifyRuleTarget> getTargetsByRules(List<NotifyRule> rules) {
        if (rules == null || rules.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> uniqueKeys = new HashSet<>();
        List<NotifyRuleTarget> result = new ArrayList<>();

        for (NotifyRule rule : rules) {
            List<NotifyRuleTarget> targets = targetCache.get(rule.getId());
            if (targets != null) {
                for (NotifyRuleTarget target : targets) {
                    String key = target.getTargetType() + "_" +
                            (target.getTargetId() != null ? target.getTargetId() : "null") + "_" +
                            (target.getTargetPhone() != null ? target.getTargetPhone() : "null");
                    if (!uniqueKeys.contains(key)) {
                        uniqueKeys.add(key);
                        result.add(target);
                    }
                }
            }
        }

        return result;
    }

    public List<NotifyRuleTarget> matchAndGetTargets(Long schoolId, Integer alarmLevel, Integer alarmType) {
        List<NotifyRule> matchedRules = matchRules(schoolId, alarmLevel, alarmType);
        return getTargetsByRules(matchedRules);
    }

    private boolean matchRule(NotifyRule rule, boolean isHoliday, boolean isNight,
                              Integer schoolType, Integer alarmLevel) {
        if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
            return false;
        }

        if (rule.getHolidayMode() != null && rule.getHolidayMode() == 1 && !isHoliday) {
            return false;
        }

        if (rule.getNightMode() != null && rule.getNightMode() == 1 && !isNight) {
            return false;
        }

        if (rule.getSchoolTypes() != null && !rule.getSchoolTypes().isEmpty()) {
            List<Integer> allowedTypes = Arrays.stream(rule.getSchoolTypes().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (!allowedTypes.contains(schoolType)) {
                return false;
            }
        }

        if (rule.getAlarmLevels() != null && !rule.getAlarmLevels().isEmpty()) {
            List<Integer> allowedLevels = Arrays.stream(rule.getAlarmLevels().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            if (!allowedLevels.contains(alarmLevel)) {
                return false;
            }
        }

        return true;
    }

    private void sortRulesByPriority(List<NotifyRule> rules) {
        rules.sort((r1, r2) -> {
            int typePriority1 = getTypePriority(r1.getRuleType());
            int typePriority2 = getTypePriority(r2.getRuleType());
            if (typePriority1 != typePriority2) {
                return Integer.compare(typePriority1, typePriority2);
            }
            int p1 = r1.getPriority() != null ? r1.getPriority() : 0;
            int p2 = r2.getPriority() != null ? r2.getPriority() : 0;
            return Integer.compare(p2, p1);
        });
    }

    private int getTypePriority(Integer ruleType) {
        if (ruleType == null) {
            return 4;
        }
        if (RuleTypeEnum.HOLIDAY.getCode().equals(ruleType)) {
            return 1;
        }
        if (RuleTypeEnum.NIGHT.getCode().equals(ruleType)) {
            return 2;
        }
        if (RuleTypeEnum.CUSTOM.getCode().equals(ruleType)) {
            return 3;
        }
        if (RuleTypeEnum.DEFAULT.getCode().equals(ruleType)) {
            return 4;
        }
        return 4;
    }

    private boolean checkIsHoliday() {
        LocalDate today = LocalDate.now();
        HolidayConfig config = holidayConfigMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<HolidayConfig>()
                        .eq(HolidayConfig::getHolidayDate, today)
        );
        return config != null;
    }

    private boolean checkIsNight() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        return hour >= 22 || hour < 6;
    }

    public List<Integer> getNotifyChannelsByRules(List<NotifyRule> rules) {
        Set<Integer> channels = new LinkedHashSet<>();
        for (NotifyRule rule : rules) {
            if (rule.getNotifyChannels() != null && !rule.getNotifyChannels().isEmpty()) {
                Arrays.stream(rule.getNotifyChannels().split(","))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .forEach(channels::add);
            }
        }
        return new ArrayList<>(channels);
    }

    public String getNotifyTemplateByRules(List<NotifyRule> rules) {
        for (NotifyRule rule : rules) {
            if (rule.getNotifyTemplate() != null && !rule.getNotifyTemplate().isEmpty()) {
                return rule.getNotifyTemplate();
            }
        }
        return null;
    }
}
