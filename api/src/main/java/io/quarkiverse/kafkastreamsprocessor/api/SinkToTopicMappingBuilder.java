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
package io.quarkiverse.kafkastreamsprocessor.api;

import java.util.Map;

/**
 * Object to inject to get access to the resolved mapping between sink and topic for a multi output processor, using the
 * conventions set up by the framework based on config properties like:
 *
 * <pre>
 * kafkastreamsprocessor.output.sinks.pong.topic=pong-events
 * kafkastreamsprocessor.output.sinks.pang.topic=pang-events
 * </pre>
 * <p>
 * Where:
 * </p>
 * <ul>
 * <li>pong and pang are the sinks</li>
 * <li>pong-events and pang-events the Kafka topics</li>
 * </ul>
 */
public interface SinkToTopicMappingBuilder {
    /**
     * Default sink name created by KafkaStreams if no sink is configured manually
     */
    String DEFAULT_SINK_NAME = "emitter-channel";

    /**
     * Looks at the configuration and extracts from it the mapping from the sink to the Kafka topic.
     * <p>
     * This method is exposed so you can do any kind of technical postprocessing based on the Kafka topic and the sink
     * names.
     * </p>
     *
     * @return a map with keys the sink names and values the corresponding Kafka topic name
     */
    Map<String, String> sinkToTopicMapping();
}
