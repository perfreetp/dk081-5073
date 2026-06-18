package com.safetycampus.notify.channel.impl;

import com.safetycampus.notify.channel.SmsChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AliyunSmsChannel implements SmsChannel {

    @Override
    public boolean send(String phone, String signName, String templateCode, String templateParam) {
        log.info("【阿里云短信模拟】发送短信 - 手机号: {}, 签名: {}, 模板: {}, 参数: {}",
                phone, signName, templateCode, templateParam);
        return true;
    }
}
