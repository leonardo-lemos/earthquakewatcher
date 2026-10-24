package ai.aircompany.earthquakewatcher.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String EXCHANGE_TOPIC = "earthquakes.topic";
    public static final String EXCHANGE_DLX = "earthquakes.dlx";

    public static final String EMERGENCY_QUEUE = "queue.siren.emergency";
    public static final String STATISTICS_QUEUE = "queue.statistics.general";
    public static final String DLQ_QUEUE = "queue.earthquakes.dlq";

    public static final String EMERGENCY_DEAD_LETTER_ROUTING_KEY = "fail.emergency";
    public static final String STATISTICS_DEAD_LETTER_ROUTING_KEY = "fail.statistics";
    public static final String EMERGENCY_BINDING_ROUTING_KEY = "alert.critical.#";
    public static final String STATISTICS_BINDING_ROUTING_KEY = "alert.#";
    public static final String DLQ_BINDING_ROUTING_KEY = "fail.#";

    // Exchanges
    @Bean
    public TopicExchange earthquakeExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPIC).durable(true).build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE_DLX).durable(true).build();
    }

    // Queues
    @Bean
    public Queue emergencyQueue() {
        return QueueBuilder.durable(EMERGENCY_QUEUE)
                .deadLetterExchange(EXCHANGE_DLX)
                .deadLetterRoutingKey(EMERGENCY_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue statisticsQueue() {
        return QueueBuilder.durable(STATISTICS_QUEUE)
                .deadLetterExchange(EXCHANGE_DLX)
                .deadLetterRoutingKey(STATISTICS_DEAD_LETTER_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    // Bindings
    @Bean
    public Binding emergencyBinding(Queue emergencyQueue, TopicExchange earthquakeExchange) {
        // Capture only critical alerts for the emergency queue
        return BindingBuilder.bind(emergencyQueue).to(earthquakeExchange).with(EMERGENCY_BINDING_ROUTING_KEY);
    }

    @Bean
    public Binding statisticsBinding(Queue statisticsQueue, TopicExchange earthquakeExchange) {
        // Capture all alerts for the statistics queue
        return BindingBuilder.bind(statisticsQueue).to(earthquakeExchange).with(STATISTICS_BINDING_ROUTING_KEY);
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, TopicExchange deadLetterExchange) {
        // Capture all failed messages for the DLQ
        return BindingBuilder.bind(dlqQueue).to(deadLetterExchange).with(DLQ_BINDING_ROUTING_KEY);
    }

    // JSON converter for message serialization/deserialization in RabbitTemplate
    @Bean
    public JacksonJsonMessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // RabbitTemplate bean configured with the JSON message converter
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        var template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());

        return template;
    }

    // Container factory for prefetching one message at a time
    @Bean
    public SimpleRabbitListenerContainerFactory prefetchOneContainerFactory(ConnectionFactory connectionFactory) {
        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setPrefetchCount(1); // Prefetch one message at a time

        return factory;
    }
}
