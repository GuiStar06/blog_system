package com.guistar.config;

import com.guistar.entity.utils.Const;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean("mailQueue")
    public Queue mailQueue(){
        return QueueBuilder.durable(Const.MAIL_QUEUE).build();
    }
}
