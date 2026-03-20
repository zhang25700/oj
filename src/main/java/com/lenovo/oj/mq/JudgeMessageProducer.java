package com.lenovo.oj.mq;

import com.lenovo.oj.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
/**
 * 判题消息生产者。
 *
 * 主服务在提交记录入库后，通过这个组件把 submitId 发送到 RabbitMQ，
 * 从而把“接收提交请求”和“执行判题”两件事解耦。
 */
public class JudgeMessageProducer {

    private final RabbitTemplate rabbitTemplate;

    // 队列里只传 submitId，实际判题所需的代码和题目信息统一回库查询，避免消息体膨胀。
    public void send(Long submitId) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.JUDGE_EXCHANGE, RabbitMqConfig.JUDGE_ROUTING_KEY, submitId);
    }
}
