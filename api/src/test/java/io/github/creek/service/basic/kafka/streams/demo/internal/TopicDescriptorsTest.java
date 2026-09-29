/*
 * Copyright 2021-2025 Creek Contributors (https://github.com/creek-service)
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

package io.github.creek.service.basic.kafka.streams.demo.internal;

import static io.github.creek.service.basic.kafka.streams.demo.internal.TopicDescriptors.KAFKA_FORMAT;
import static io.github.creek.service.basic.kafka.streams.demo.internal.TopicDescriptors.creatableInternalTopic;
import static io.github.creek.service.basic.kafka.streams.demo.internal.TopicDescriptors.inputTopic;
import static io.github.creek.service.basic.kafka.streams.demo.internal.TopicDescriptors.internalTopic;
import static io.github.creek.service.basic.kafka.streams.demo.internal.TopicDescriptors.outputTopic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.when;

import io.github.creek.service.basic.kafka.streams.demo.api.model.TweetData;
import org.creekservice.api.kafka.metadata.schema.JsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.schema.OwnedJsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.schema.UnownedJsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.topic.CreatableKafkaTopicInternal;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicConfig;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicInput;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicInternal;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicOutput;
import org.creekservice.api.kafka.metadata.topic.OwnedKafkaTopicInput;
import org.creekservice.api.kafka.metadata.topic.OwnedKafkaTopicOutput;
import org.creekservice.api.platform.metadata.ResourceDescriptor;
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
                inputTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

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
                inputTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

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
                internalTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT);

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
                creatableInternalTopic(
                        "name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

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
                outputTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

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
                outputTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

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

    @Test
    void shouldOwnJsonSchemaOnOwnedOutputTopic() {
        // When:
        final OwnedKafkaTopicOutput<Long, TweetData> topic =
                outputTopic("name", Long.class, TweetData.class, config);

        // Then:
        assertThat(topic.resources().toList(), hasSize(1));
        assertThat(
                topic.value().resources().toList().get(0),
                is(instanceOf(OwnedJsonSchemaDescriptor.class)));
    }

    @Test
    void shouldNotOwnJsonSchemaOnUnownedInput() {
        // Given:
        final OwnedKafkaTopicOutput<Long, TweetData> output =
                outputTopic("name", Long.class, TweetData.class, config);

        // When:
        final KafkaTopicInput<Long, TweetData> input = output.toInput();

        // Then: the schema is still described, but it is no longer claimed as owned
        assertThat(input.value().resources().toList(), hasSize(1));
        final ResourceDescriptor schema = input.value().resources().toList().get(0);
        assertThat(schema, is(instanceOf(JsonSchemaDescriptor.class)));
        assertThat(schema, is(not(instanceOf(OwnedJsonSchemaDescriptor.class))));
        assertThat(schema, is(instanceOf(UnownedJsonSchemaDescriptor.class)));
    }

    @Test
    void shouldNotOwnJsonSchemaOnUnownedOutput() {
        // Given:
        final OwnedKafkaTopicInput<Long, TweetData> input =
                inputTopic("name", Long.class, TweetData.class, config);

        // When:
        final KafkaTopicOutput<Long, TweetData> output = input.toOutput();

        // Then:
        assertThat(output.value().resources().toList(), hasSize(1));
        assertThat(
                output.value().resources().toList().get(0),
                is(instanceOf(UnownedJsonSchemaDescriptor.class)));
    }

    @Test
    void shouldGiveOwnedAndUnownedSchemaTheSameResourceId() {
        // Given:
        final OwnedKafkaTopicOutput<Long, TweetData> output =
                outputTopic("name", Long.class, TweetData.class, config);

        // When:
        final KafkaTopicInput<Long, TweetData> input = output.toInput();

        // Then: ownership changes, identity does not
        final ResourceDescriptor owned = output.value().resources().toList().get(0);
        final ResourceDescriptor unowned = input.value().resources().toList().get(0);
        assertThat(unowned.id(), is(owned.id()));
    }

    @Test
    void shouldPointSchemaPartBackAtEnclosingPartDescriptor() {
        // When:
        final OwnedKafkaTopicOutput<Long, TweetData> topic =
                outputTopic("name", Long.class, TweetData.class, config);

        // Then:
        final JsonSchemaDescriptor<?> schema =
                (JsonSchemaDescriptor<?>) topic.value().resources().toList().get(0);
        assertThat(schema.part(), is(sameInstance(topic.value())));
    }

    @Test
    void shouldDescribeNoSchemaForKafkaFormattedParts() {
        // When:
        final OwnedKafkaTopicOutput<Long, String> topic =
                outputTopic("name", Long.class, KAFKA_FORMAT, String.class, KAFKA_FORMAT, config);

        // Then:
        assertThat(topic.resources().toList(), is(empty()));
    }
}
