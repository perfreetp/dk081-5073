package com.safetycampus.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_ALARM_DIRECT = "alarm.direct.exchange";

    public static final String QUEUE_ALARM_RECEIVE = "alarm.receive.queue";
    public static final String QUEUE_ALARM_NOTIFY = "alarm.notify.queue";
    public static final String QUEUE_ALARM_ESCALATE = "alarm.escalate.queue";

    public static final String ROUTING_KEY_ALARM_RECEIVE = "alarm.receive";
    public static final String ROUTING_KEY_ALARM_NOTIFY = "alarm.notify";
    public static final String ROUTING_KEY_ALARM_ESCALATE = "alarm.escalate";

    @Bean
    public DirectExchange alarmDirectExchange() {
        return ExchangeBuilder.directExchange(EXCHANGE_ALARM_DIRECT)
                .durable(true)
                .build();
    }

    @Bean
    public Queue alarmReceiveQueue() {
        return QueueBuilder.durable(QUEUE_ALARM_RECEIVE).build();
    }

    @Bean
    public Queue alarmNotifyQueue() {
        return QueueBuilder.durable(QUEUE_ALARM_NOTIFY).build();
    }

    @Bean
    public Queue alarmEscalateQueue() {
        return QueueBuilder.durable(QUEUE_ALARM_ESCALATE).build();
    }

    @Bean
    public Binding alarmReceiveBinding(Queue alarmReceiveQueue, DirectExchange alarmDirectExchange) {
        return BindingBuilder.bind(alarmReceiveQueue)
                .to(alarmDirectExchange)
                .with(ROUTING_KEY_ALARM_RECEIVE);
    }

    @Bean
    public Binding alarmNotifyBinding(Queue alarmNotifyQueue, DirectExchange alarmDirectExchange) {
        return BindingBuilder.bind(alarmNotifyQueue)
                .to(alarmDirectExchange)
                .with(ROUTING_KEY_ALARM_NOTIFY);
    }

    @Bean
    public Binding alarmEscalateBinding(Queue alarmEscalateQueue, DirectExchange alarmDirectExchange) {
        return BindingBuilder.bind(alarmEscalateQueue)
                .to(alarmDirectExchange)
                .with(ROUTING_KEY_ALARM_ESCALATE);
    }
}
