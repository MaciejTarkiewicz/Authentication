package com.tarkiewicz.integration.kafka.producer;

import io.micronaut.configuration.kafka.annotation.KafkaClient;
import io.micronaut.configuration.kafka.annotation.Topic;
import io.micronaut.context.annotation.Property;
import org.apache.kafka.clients.producer.ProducerConfig;

@KafkaClient(
        id = "logged-username-client",
        acks = KafkaClient.Acknowledge.ALL,
        properties = @Property(name = ProducerConfig.RETRIES_CONFIG, value = "3")
)
public interface LoggedUsernameClient {

    @Topic("logged-username-topic")
    void sendUsername(final String username);
}
