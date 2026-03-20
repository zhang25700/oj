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
/**
 * 判题消息消费者。
 *
 * 监听判题队列，收到 submitId 后异步触发正式判题。
 */
public class JudgeMessageConsumer {

    private final JudgeService judgeService;

    // 消费者线程和 Web 请求线程分离，提交接口返回后由这里异步完成真正的判题。
    @RabbitListener(queues = RabbitMqConfig.JUDGE_QUEUE)
    public void receive(Long submitId) {
        log.info("receive judge task, submitId={}", submitId);
        judgeService.doJudge(submitId);
    }
}
