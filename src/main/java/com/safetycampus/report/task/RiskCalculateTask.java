package com.safetycampus.report.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.safetycampus.report.service.SchoolRiskService;
import com.safetycampus.school.entity.SchoolInfo;
import com.safetycampus.school.mapper.SchoolInfoMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

@Slf4j
@Component
public class RiskCalculateTask {

    @Resource
    private SchoolInfoMapper schoolInfoMapper;

    @Resource
    private SchoolRiskService schoolRiskService;

    @Scheduled(cron = "0 0 1 1 * ?")
    @Transactional(rollbackFor = Exception.class)
    public void calculateMonthlyRisk() {
        log.info("开始执行月度风险画像计算定时任务");

        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        String statMonth = lastMonth.toString();

        LambdaQueryWrapper<SchoolInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SchoolInfo::getStatus, 1);
        List<SchoolInfo> schools = schoolInfoMapper.selectList(wrapper);

        int successCount = 0;
        int failCount = 0;

        for (SchoolInfo school : schools) {
            try {
                schoolRiskService.generateRiskPortrait(school.getId(), statMonth);
                successCount++;
            } catch (Exception e) {
                log.error("计算学校[{}]风险画像失败", school.getSchoolName(), e);
                failCount++;
            }
        }

        log.info("月度风险画像计算定时任务执行完成，统计月份：{}，成功：{}所，失败：{}所",
                statMonth, successCount, failCount);
    }
}
