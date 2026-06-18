package com.safetycampus.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.safetycampus.report.dto.AssessQueryDTO;
import com.safetycampus.report.entity.AssessRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AssessRecordMapper extends BaseMapper<AssessRecord> {

    IPage<AssessRecord> selectPageByCondition(IPage<AssessRecord> page, @Param("query") AssessQueryDTO queryDTO);

    List<AssessRecord> selectQuarterRank(@Param("statQuarter") String statQuarter);
}
