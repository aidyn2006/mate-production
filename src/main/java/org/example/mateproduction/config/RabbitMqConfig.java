package org.example.mateproduction.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Data
public class RabbitMqConfig {

    private static final String EXCHANGE_NAME = "notification_exchange";
    private static final String ROUTING_KEY = "notification_routing_key";
    private static final String QUEUE_NAME = "notification_queue";

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(QUEUE_NAME, true); // durable queue
    }

    @Bean
    public Binding binding(Queue notificationQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(notificationQueue).to(topicExchange).with(ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}
