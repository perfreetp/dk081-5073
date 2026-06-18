package com.safetycampus.notifyrule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.safetycampus.notifyrule.entity.NotifyRuleTarget;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface NotifyRuleTargetMapper extends BaseMapper<NotifyRuleTarget> {

    @Select("SELECT * FROM notify_rule_target WHERE rule_id = #{ruleId} AND deleted = 0 ORDER BY sort_order ASC")
    List<NotifyRuleTarget> selectByRuleId(@Param("ruleId") Long ruleId);

    @Select("<script>" +
            "SELECT * FROM notify_rule_target WHERE rule_id IN " +
            "<foreach item='ruleId' collection='ruleIds' open='(' separator=',' close='>' #{ruleId}</foreach> " +
            "AND deleted = 0 ORDER BY sort_order ASC" +
            "</script>")
    List<NotifyRuleTarget> selectByRuleIds(@Param("ruleIds") List<Long> ruleIds);
}
