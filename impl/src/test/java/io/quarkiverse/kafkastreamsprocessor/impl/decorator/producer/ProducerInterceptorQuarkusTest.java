/*-
 * #%L
 * Quarkus Kafka Streams Processor
 * %%
 * Copyright (C) 2024 Amadeus s.a.s.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.quarkiverse.kafkastreamsprocessor.impl.decorator.producer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.processor.api.ContextualProcessor;
import org.apache.kafka.streams.processor.api.Record;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufDeserializer;
import com.github.daniel.shuy.kafka.protobuf.serde.KafkaProtobufSerializer;

import io.quarkiverse.kafkastreamsprocessor.api.Processor;
import io.quarkiverse.kafkastreamsprocessor.api.decorator.producer.ProducerOnSendInterceptor;
import io.quarkiverse.kafkastreamsprocessor.api.properties.KStreamsProcessorConfig;
import io.quarkiverse.kafkastreamsprocessor.sample.message.PingMessage;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
@TestProfile(ProducerInterceptorQuarkusTest.TestProfile.class)
public class ProducerInterceptorQuarkusTest {

    @ConfigProperty(name = "kafka.bootstrap.servers")
    String bootstrapServers;
    KafkaProducer<String, PingMessage.Ping> producer;

    KafkaConsumer<String, PingMessage.Ping> consumer;

    @Inject
    KStreamsProcessorConfig kStreamsProcessorConfig;

    @BeforeEach
    public void setup() {
        producer = new KafkaProducer(KafkaTestUtils.producerProps(bootstrapServers), new StringSerializer(),
                new KafkaProtobufSerializer<>());
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(bootstrapServers, "test", "true");
        consumer = new KafkaConsumer(consumerProps, new StringDeserializer(),
                new KafkaProtobufDeserializer<>(PingMessage.Ping.parser()));
    }

    @Test
    public void producerInterceptorCalled() throws Exception {
        consumer.subscribe(List.of(kStreamsProcessorConfig.output().topic().get()));

        producer.send(new ProducerRecord<>(kStreamsProcessorConfig.input().topic().get(), 0, "key",
                PingMessage.Ping.newBuilder().setMessage("value").build()));
        producer.flush();

        ConsumerRecords<String, PingMessage.Ping> out = KafkaTestUtils.getRecords(consumer, Duration.ofSeconds(10),
                1);

        assertThat(out.count(), equalTo(1));
        ConsumerRecord<String, PingMessage.Ping> outRecord = out.iterator().next();
        assertThat(outRecord.key(), equalTo("key"));
        assertThat(outRecord.value().getMessage(), equalTo("value"));
        assertThat(new String(outRecord.headers().lastHeader("blabla").value(), StandardCharsets.UTF_8), equalTo("blibli"));
    }

    @Alternative
    @ApplicationScoped
    public static class HeaderAddingProducerInterceptor implements ProducerOnSendInterceptor {
        @Override
        public ProducerRecord<byte[], byte[]> onSend(ProducerRecord<byte[], byte[]> producerRecord) {
            producerRecord.headers().add("blabla", "blibli".getBytes(StandardCharsets.UTF_8));
            return producerRecord;
        }
    }

    @Processor
    @Alternative
    public static class MyProcessor extends ContextualProcessor<String, PingMessage.Ping, String, PingMessage.Ping> {
        @Override
        public void process(Record<String, PingMessage.Ping> record) {
            context().forward(record);
        }
    }

    public static class TestProfile implements QuarkusTestProfile {
        @Override
        public Set<Class<?>> getEnabledAlternatives() {
            return Set.of(HeaderAddingProducerInterceptor.class, MyProcessor.class);
            // return Set.of( MyProcessor.class);
        }
    }
}
