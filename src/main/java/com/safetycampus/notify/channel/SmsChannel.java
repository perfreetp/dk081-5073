package com.safetycampus.notify.channel;

public interface SmsChannel {

    boolean send(String phone, String signName, String templateCode, String templateParam);
}
