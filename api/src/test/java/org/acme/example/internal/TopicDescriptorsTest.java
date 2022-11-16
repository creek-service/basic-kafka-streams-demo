/*
 * Copyright 2021-2022 Creek Contributors (https://github.com/creek-service)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.example.internal;

import static org.acme.example.internal.TopicDescriptors.KAFKA_FORMAT;
import static org.acme.example.internal.TopicDescriptors.creatableInternalTopic;
import static org.acme.example.internal.TopicDescriptors.inputTopic;
import static org.acme.example.internal.TopicDescriptors.internalTopic;
import static org.acme.example.internal.TopicDescriptors.outputTopic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

import org.creekservice.api.kafka.metadata.CreatableKafkaTopicInternal;
import org.creekservice.api.kafka.metadata.KafkaTopicConfig;
import org.creekservice.api.kafka.metadata.KafkaTopicInput;
import org.creekservice.api.kafka.metadata.KafkaTopicInternal;
import org.creekservice.api.kafka.metadata.KafkaTopicOutput;
import org.creekservice.api.kafka.metadata.OwnedKafkaTopicInput;
import org.creekservice.api.kafka.metadata.OwnedKafkaTopicOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TopicDescriptorsTest {

    private static final KafkaTopicConfig CONFIG = TopicConfigBuilder.withPartitions(1).build();

    @Mock private TopicConfigBuilder config;

    @BeforeEach
    void setUp() {
        when(config.build()).thenReturn(CONFIG);
    }

    @Test
    void shouldCreateInputTopic() {
        // When:
        final OwnedKafkaTopicInput<Long, String> topic =
                inputTopic("name", Long.class, String.class, config);

        // Then:
        assertThat(topic.id().toString(), is("kafka-topic://default/name"));
        assertThat(topic.name(), is("name"));
        assertThat(topic.key().format(), is(KAFKA_FORMAT));
        assertThat(topic.key().type(), is(Long.class));
        assertThat(topic.value().format(), is(KAFKA_FORMAT));
        assertThat(topic.value().type(), is(String.class));
        assertThat(topic.config(), is(sameInstance(CONFIG)));
    }

    @Test
    void shouldConvertInputTopicToOutput() {
        // Given:
        final OwnedKafkaTopicInput<Long, String> input =
                inputTopic("name", Long.class, String.class, config);

        // When:
        final KafkaTopicOutput<Long, String> output = input.toOutput();

        // Then:
        assertThat(output.id().toString(), is("kafka-topic://default/name"));
        assertThat(output.name(), is("name"));
        assertThat(output.key().format(), is(KAFKA_FORMAT));
        assertThat(output.key().type(), is(Long.class));
        assertThat(output.value().format(), is(KAFKA_FORMAT));
        assertThat(output.value().type(), is(String.class));
    }

    @Test
    void shouldCreateInternalTopic() {
        // When:
        final KafkaTopicInternal<Long, String> topic =
                internalTopic("name", Long.class, String.class);

        // Then:
        assertThat(topic.name(), is("name"));
        assertThat(topic.key().format(), is(KAFKA_FORMAT));
        assertThat(topic.key().type(), is(Long.class));
        assertThat(topic.value().format(), is(KAFKA_FORMAT));
        assertThat(topic.value().type(), is(String.class));
    }

    @Test
    void shouldCreateCreatableInternalTopic() {
        // When:
        final CreatableKafkaTopicInternal<Long, String> topic =
                creatableInternalTopic("name", Long.class, String.class, config);

        // Then:
        assertThat(topic.name(), is("name"));
        assertThat(topic.key().format(), is(KAFKA_FORMAT));
        assertThat(topic.key().type(), is(Long.class));
        assertThat(topic.value().format(), is(KAFKA_FORMAT));
        assertThat(topic.value().type(), is(String.class));
        assertThat(topic.config(), is(sameInstance(CONFIG)));
    }

    @Test
    void shouldCreateOutputTopic() {
        // When:
        final OwnedKafkaTopicOutput<Long, String> topic =
                outputTopic("name", Long.class, String.class, config);

        // Then:
        assertThat(topic.id().toString(), is("kafka-topic://default/name"));
        assertThat(topic.name(), is("name"));
        assertThat(topic.key().format(), is(KAFKA_FORMAT));
        assertThat(topic.key().type(), is(Long.class));
        assertThat(topic.value().format(), is(KAFKA_FORMAT));
        assertThat(topic.value().type(), is(String.class));
        assertThat(topic.config(), is(sameInstance(CONFIG)));
    }

    @Test
    void shouldConvertOutputTopicToInput() {
        // Given:
        final OwnedKafkaTopicOutput<Long, String> output =
                outputTopic("name", Long.class, String.class, config);

        // When:
        final KafkaTopicInput<Long, String> input = output.toInput();

        // Then:
        assertThat(input.id().toString(), is("kafka-topic://default/name"));
        assertThat(input.name(), is("name"));
        assertThat(input.key().format(), is(KAFKA_FORMAT));
        assertThat(input.key().type(), is(Long.class));
        assertThat(input.value().format(), is(KAFKA_FORMAT));
        assertThat(input.value().type(), is(String.class));
    }
}
