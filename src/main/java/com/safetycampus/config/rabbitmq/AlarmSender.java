package com.safetycampus.config.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlarmSender {

    private final RabbitTemplate rabbitTemplate;

    public AlarmSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAlarm(Object message) {
        log.info("发送报警接收消息: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_ALARM_DIRECT,
                RabbitMqConfig.ROUTING_KEY_ALARM_RECEIVE,
                message
        );
    }

    public void sendNotify(Object message) {
        log.info("发送通知消息: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_ALARM_DIRECT,
                RabbitMqConfig.ROUTING_KEY_ALARM_NOTIFY,
                message
        );
    }

    public void sendEscalate(Object message) {
        log.info("发送警情上推消息: {}", message);
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE_ALARM_DIRECT,
                RabbitMqConfig.ROUTING_KEY_ALARM_ESCALATE,
                message
        );
    }
}
