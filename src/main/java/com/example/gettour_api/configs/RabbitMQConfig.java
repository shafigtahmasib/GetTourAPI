package com.example.gettour_api.configs;

import com.example.gettour_api.utils.Config;
import lombok.Data;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
public class RabbitMQConfig {

    public static final String clientQueue = "usermsg_queue";
    public static final String clientRoutingKey = "usermsg_routingKey";
    public static final String agentQueue = "agentmsg_queue";
    public static final String AGENT_ROUTING_KEY = "agentmsg_routingKey";
    public static final String exchange = "exchange";

    @Bean
    public Queue queue1(){
        return new Queue(clientQueue);
    }

    @Bean
    public Queue queue2(){
        return new Queue(agentQueue);
    }

    @Bean
    public TopicExchange exchange1(){
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding binding1(@Qualifier("queue1")Queue queue, @Qualifier("exchange1") TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(clientRoutingKey);
    }

    @Bean
    public Binding binding2(@Qualifier("queue2")Queue queue, TopicExchange topicExchange){
        return BindingBuilder.bind(queue).to(topicExchange).with(AGENT_ROUTING_KEY);
    }

    @Bean
    public MessageConverter converter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}