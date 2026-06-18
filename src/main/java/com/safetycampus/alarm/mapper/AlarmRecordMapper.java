package com.safetycampus.alarm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.safetycampus.alarm.dto.AlarmQueryDTO;
import com.safetycampus.alarm.entity.AlarmRecord;
import org.apache.ibatis.annotations.Param;

public interface AlarmRecordMapper extends BaseMapper<AlarmRecord> {

    IPage<AlarmRecord> selectPageByCondition(IPage<AlarmRecord> page, @Param("query") AlarmQueryDTO queryDTO);
}
