package com.lenovo.oj.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * RabbitMQ 判题队列配置。
 *
 * 当前使用 DirectExchange：
 * - 交换机负责接收主服务发出的判题消息
 * - 队列负责缓存等待执行的判题任务
 * - routingKey 负责把消息精确路由到判题队列
 */
public class RabbitMqConfig {

    public static final String JUDGE_EXCHANGE = "oj.judge.exchange";
    public static final String JUDGE_QUEUE = "oj.judge.queue";
    public static final String JUDGE_ROUTING_KEY = "oj.judge.submit";

    @Bean
    public DirectExchange judgeExchange() {
        // durable=true 表示 RabbitMQ 重启后交换机仍然存在。
        return new DirectExchange(JUDGE_EXCHANGE, true, false);
    }

    @Bean
    public Queue judgeQueue() {
        // 持久化队列用于承载异步判题任务，避免消息在服务重启时丢失。
        return new Queue(JUDGE_QUEUE, true);
    }

    @Bean
    public Binding judgeBinding(DirectExchange judgeExchange, Queue judgeQueue) {
        // 把 routingKey 精确绑定到判题队列，提交服务按这个 key 投递消息。
        return BindingBuilder.bind(judgeQueue).to(judgeExchange).with(JUDGE_ROUTING_KEY);
    }
}
