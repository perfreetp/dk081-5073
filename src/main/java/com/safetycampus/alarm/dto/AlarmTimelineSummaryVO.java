package com.safetycampus.alarm.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AlarmTimelineSummaryVO {

    private Integer totalDurationSeconds;

    private String totalDurationText;

    private Map<Integer, Integer> partyDurationMap;

    private Map<String, Integer> partyDurationTextMap;

    private Integer keyNodeCount;

    private Integer totalNodeCount;
}
