package com.safetycampus.notify.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppPushChannel {

    public boolean push(String target, String title, String content) {
        log.info("【APP推送模拟】推送消息 - 目标: {}, 标题: {}, 内容: {}", target, title, content);
        return true;
    }
}
