package org.example.mateproduction.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final String TOPIC_NAME = "exchange";
    private static final String ROUTING_KEY = "notification_routing_key";


    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_NAME);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
