package com.safetycampus.notify.channel;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VoiceCallChannel {

    public boolean call(String phone, String name, String content) {
        log.info("【语音呼叫模拟】发起呼叫 - 手机号: {}, 姓名: {}, 内容: {}", phone, name, content);
        return true;
    }
}
