package com.synapsecode.accountservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // Message converter
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Configure RabbitTemplate with message converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    // Audit configuration
    @Bean
    public Exchange auditExchange() {
        return ExchangeBuilder.topicExchange("account-audit-exchange")
                .durable(true)
                .build();
    }

    @Bean
    public Queue auditQueue() {
        return QueueBuilder.durable("account-audit-queue")
                .build();
    }

    @Bean
    public Binding auditBinding() {
        return BindingBuilder
                .bind(auditQueue())
                .to(auditExchange())
                .with("account.audit.*")
                .noargs();
    }

    // Notification configuration
    @Bean
    public Exchange notificationExchange() {
        return ExchangeBuilder.topicExchange("notification-exchange")
                .durable(true)
                .build();
    }

    @Bean
    public Queue accountNotificationQueue() {
        return QueueBuilder.durable("account-notification-queue")
                .build();
    }

    @Bean
    public Queue kycNotificationQueue() {
        return QueueBuilder.durable("kyc-notification-queue")
                .build();
    }

    @Bean
    public Binding accountNotificationBinding() {
        return BindingBuilder
                .bind(accountNotificationQueue())
                .to(notificationExchange())
                .with("notification.account.*")
                .noargs();
    }

    @Bean
    public Binding kycNotificationBinding() {
        return BindingBuilder
                .bind(kycNotificationQueue())
                .to(notificationExchange())
                .with("notification.kyc.*")
                .noargs();
    }
}