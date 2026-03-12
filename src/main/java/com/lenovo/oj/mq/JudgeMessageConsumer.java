package com.lenovo.oj.mq;

import com.lenovo.oj.config.RabbitMqConfig;
import com.lenovo.oj.service.JudgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JudgeMessageConsumer {

    private final JudgeService judgeService;

    @RabbitListener(queues = RabbitMqConfig.JUDGE_QUEUE)
    public void receive(Long submitId) {
        log.info("receive judge task, submitId={}", submitId);
        judgeService.doJudge(submitId);
    }
}
