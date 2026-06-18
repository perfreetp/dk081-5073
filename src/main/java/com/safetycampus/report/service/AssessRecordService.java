package com.safetycampus.report.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.safetycampus.report.dto.AssessQueryDTO;
import com.safetycampus.report.entity.AssessRecord;

import java.util.List;

public interface AssessRecordService extends IService<AssessRecord> {

    AssessRecord calculateQuarterAssess(Long schoolId, String statQuarter);

    boolean generateRank(String statQuarter);

    List<AssessRecord> getQuarterRank(String statQuarter);

    IPage<AssessRecord> selectPage(AssessQueryDTO queryDTO);

    AssessRecord getDetail(Long id);
}
