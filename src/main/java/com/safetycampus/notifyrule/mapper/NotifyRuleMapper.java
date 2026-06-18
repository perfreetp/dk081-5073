package com.safetycampus.notifyrule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.notifyrule.entity.NotifyRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotifyRuleMapper extends BaseMapper<NotifyRule> {

    @Select("SELECT * FROM notify_rule WHERE is_enabled = 1 AND deleted = 0 ORDER BY priority DESC")
    List<NotifyRule> selectAllEnabledRules();

    List<NotifyRule> selectPageByCondition(@Param("query") com.safetycampus.notifyrule.dto.NotifyRuleQueryDTO query);

    Long countByCondition(@Param("query") com.safetycampus.notifyrule.dto.NotifyRuleQueryDTO query);
}
