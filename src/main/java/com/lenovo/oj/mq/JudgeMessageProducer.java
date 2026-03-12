package com.lenovo.oj.mq;

import com.lenovo.oj.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JudgeMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void send(Long submitId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.JUDGE_EXCHANGE, RabbitMqConfig.JUDGE_ROUTING_KEY, submitId);
    }
}
