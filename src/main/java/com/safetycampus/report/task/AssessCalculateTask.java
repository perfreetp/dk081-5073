package com.safetycampus.report.task;

import com.safetycampus.report.service.AssessRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@Component
public class AssessCalculateTask {

    @Resource
    private AssessRecordService assessRecordService;

    @Scheduled(cron = "0 0 1 2 1,4,7,10 ?")
    @Transactional(rollbackFor = Exception.class)
    public void calculateQuarterAssess() {
        log.info("开始执行季度考核计算定时任务");

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        int quarter;
        if (month == 1) {
            quarter = 4;
            year = year - 1;
        } else if (month == 4) {
            quarter = 1;
        } else if (month == 7) {
            quarter = 2;
        } else {
            quarter = 3;
        }

        String statQuarter = year + "-Q" + quarter;

        try {
            boolean result = assessRecordService.generateRank(statQuarter);
            log.info("季度考核计算定时任务执行完成，季度：{}，结果：{}", statQuarter, result);
        } catch (Exception e) {
            log.error("季度考核计算定时任务执行失败，季度：{}", statQuarter, e);
        }
    }
}
