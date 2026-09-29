/*
 * Copyright 2021-2026 Creek Contributors (https://github.com/creek-service)
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

import static java.util.Objects.requireNonNull;
import static org.creekservice.api.kafka.metadata.SerializationFormat.serializationFormat;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Optional;
import java.util.stream.Stream;
import org.creekservice.api.kafka.metadata.SerializationFormat;
import org.creekservice.api.kafka.metadata.schema.JsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.schema.OwnedJsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.schema.UnownedJsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.serde.JsonSchemaKafkaSerde;
import org.creekservice.api.kafka.metadata.topic.CreatableKafkaTopicInternal;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicConfig;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicDescriptor;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicDescriptor.PartDescriptor;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicDescriptor.PartDescriptor.Part;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicInput;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicInternal;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicOutput;
import org.creekservice.api.kafka.metadata.topic.OwnedKafkaTopicInput;
import org.creekservice.api.kafka.metadata.topic.OwnedKafkaTopicOutput;
import org.creekservice.api.platform.metadata.OwnedResource;
import org.creekservice.api.platform.metadata.ResourceDescriptor;

/**
 * Helper for creating topic descriptors.
 *
 * <p>Wondering where the builds are for {@link
 * org.creekservice.api.kafka.metadata.topic.KafkaTopicInput} or {@link
 * org.creekservice.api.kafka.metadata.topic.KafkaTopicOutput}? These should only be created by
 * calling {@link OwnedKafkaTopicInput#toOutput()} and {@link OwnedKafkaTopicOutput#toInput()} on an
 * owned topic descriptor, respectively.
 *
 * <p>By default, the methods below give topics a schema-validated JSON value and a Kafka-native key
 * - see the {@code org.creekservice.schema.json} Gradle plugin applied in this module's {@code
 * build.gradle.kts}. For anything else, use the overload that takes explicit key/value {@link
 * SerializationFormat}s, e.g. passing {@link #KAFKA_FORMAT} for the value.
 */
@SuppressWarnings("unused") // What is unused today may be used tomorrow...
public final class TopicDescriptors {

    public static final SerializationFormat KAFKA_FORMAT = serializationFormat("kafka");

    // Remove if not using JSON:
    public static final SerializationFormat JSON_FORMAT = JsonSchemaKafkaSerde.format();

    private TopicDescriptors() {}

    /**
     * Create an input Kafka topic descriptor, with a JSON value and Kafka-native key.
     *
     * <p>Looking for a version that returns {@link
     * org.creekservice.api.kafka.metadata.topic.KafkaTopicInput}? Get one of those by calling
     * {@link OwnedKafkaTopicOutput#toInput()} on the topic descriptor defined in the upstream
     * component.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value. Must be annotated with
     *     {@code @GeneratesSchema} so its JSON schema can be generated.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the input topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicInput<K, V> inputTopic(
            final String topicName,
            final Class<K> keyType,
            final Class<V> valueType,
            final TopicConfigBuilder config) {
        return inputTopic(topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT, config);
    }

    /**
     * Create an input Kafka topic descriptor with custom serialization formats.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param keyFormat the serialization format for the key.
     * @param valueType the type serialized into the Kafka record value.
     * @param valueFormat the serialization format for the value.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the input topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicInput<K, V> inputTopic(
            final String topicName,
            final Class<K> keyType,
            final SerializationFormat keyFormat,
            final Class<V> valueType,
            final SerializationFormat valueFormat,
            final TopicConfigBuilder config) {
        return new InputTopicDescriptor<>(
                topicName, keyType, keyFormat, valueType, valueFormat, config);
    }

    /**
     * Create a Kafka topic descriptor for a topic that is implicitly created, with a JSON value and
     * Kafka-native key.
     *
     * <p>Most internal topics, e.g. Kafka Streams changelog and repartition topics, are implicitly
     * created, and this is the method to use to build a descriptor for them.
     *
     * <p>For an internal topic that you want Creek to create, use {@link #creatableInternalTopic}.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value. Must be annotated with
     *     {@code @GeneratesSchema} so its JSON schema can be generated.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the internal topic descriptor.
     */
    public static <K, V> KafkaTopicInternal<K, V> internalTopic(
            final String topicName, final Class<K> keyType, final Class<V> valueType) {
        return internalTopic(topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT);
    }

    /**
     * Create a Kafka topic descriptor for a topic that is implicitly created, with custom
     * serialization formats.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param keyFormat the serialization format for the key.
     * @param valueType the type serialized into the Kafka record value.
     * @param valueFormat the serialization format for the value.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the internal topic descriptor.
     */
    public static <K, V> KafkaTopicInternal<K, V> internalTopic(
            final String topicName,
            final Class<K> keyType,
            final SerializationFormat keyFormat,
            final Class<V> valueType,
            final SerializationFormat valueFormat) {
        return new InternalTopicDescriptor<>(topicName, keyType, keyFormat, valueType, valueFormat);
    }

    /**
     * Create a Kafka topic descriptor for a topic Creek should create, with a JSON value and
     * Kafka-native key.
     *
     * <p>For an internal topic that is implicitly created, e.g. a Kafka Streams changelog or
     * repartition topic, use {@link #internalTopic}.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value. Must be annotated with
     *     {@code @GeneratesSchema} so its JSON schema can be generated.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the internal topic descriptor.
     */
    public static <K, V> CreatableKafkaTopicInternal<K, V> creatableInternalTopic(
            final String topicName,
            final Class<K> keyType,
            final Class<V> valueType,
            final TopicConfigBuilder config) {
        return creatableInternalTopic(
                topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT, config);
    }

    /**
     * Create a Kafka topic descriptor for a topic Creek should create, with custom serialization
     * formats.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param keyFormat the serialization format for the key.
     * @param valueType the type serialized into the Kafka record value.
     * @param valueFormat the serialization format for the value.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the internal topic descriptor.
     */
    public static <K, V> CreatableKafkaTopicInternal<K, V> creatableInternalTopic(
            final String topicName,
            final Class<K> keyType,
            final SerializationFormat keyFormat,
            final Class<V> valueType,
            final SerializationFormat valueFormat,
            final TopicConfigBuilder config) {
        return new CreatableInternalTopicDescriptor<>(
                topicName, keyType, keyFormat, valueType, valueFormat, config);
    }

    /**
     * Create an output Kafka topic descriptor, with a JSON value and Kafka-native key.
     *
     * <p>Looking for a version that returns {@link
     * org.creekservice.api.kafka.metadata.topic.KafkaTopicOutput}? Get one of those by calling
     * {@link OwnedKafkaTopicInput#toOutput()} on the topic descriptor defined in the downstream
     * component.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value. Must be annotated with
     *     {@code @GeneratesSchema} so its JSON schema can be generated.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the output topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicOutput<K, V> outputTopic(
            final String topicName,
            final Class<K> keyType,
            final Class<V> valueType,
            final TopicConfigBuilder config) {
        return outputTopic(topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT, config);
    }

    /**
     * Create an output Kafka topic descriptor with custom serialization formats.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param keyFormat the serialization format for the key.
     * @param valueType the type serialized into the Kafka record value.
     * @param valueFormat the serialization format for the value.
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the output topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicOutput<K, V> outputTopic(
            final String topicName,
            final Class<K> keyType,
            final SerializationFormat keyFormat,
            final Class<V> valueType,
            final SerializationFormat valueFormat,
            final TopicConfigBuilder config) {
        return new OutputTopicDescriptor<>(
                topicName, keyType, keyFormat, valueType, valueFormat, config);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private abstract static class TopicDescriptor<K, V> implements KafkaTopicDescriptor<K, V> {

        private static final String DEFAULT_SCHEMA_REGISTRY_NAME = "default";

        private final String topicName;
        private final PartDescriptor<K> key;
        private final PartDescriptor<V> value;
        private final Optional<KafkaTopicConfig> config;

        @SuppressFBWarnings(
                value = "CT_CONSTRUCTOR_THROW",
                justification = "Private implementation types")
        TopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat,
                final Optional<TopicConfigBuilder> config) {
            this.topicName = requireNonNull(topicName, "topicName");
            this.key = new KeyValueDescriptor<>(Part.key, keyType, keyFormat);
            this.value = new KeyValueDescriptor<>(Part.value, valueType, valueFormat);
            this.config = requireNonNull(config, "config").map(TopicConfigBuilder::build);
        }

        public String name() {
            return topicName;
        }

        public PartDescriptor<K> key() {
            return key;
        }

        public PartDescriptor<V> value() {
            return value;
        }

        public KafkaTopicConfig config() {
            return config.orElseThrow();
        }

        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final class KeyValueDescriptor<T> implements PartDescriptor<T> {

            private final Part part;
            private final Class<T> type;
            private final SerializationFormat format;
            private final Optional<? extends JsonSchemaDescriptor<T>> schema;

            KeyValueDescriptor(
                    final Part part, final Class<T> type, final SerializationFormat format) {
                this.part = requireNonNull(part, "part");
                this.type = requireNonNull(type, "type");
                this.format = requireNonNull(format, "format");
                this.schema =
                        JsonSchemaKafkaSerde.format().equals(format)
                                ? Optional.of(
                                        topic() instanceof OwnedResource
                                                ? new OwnedJsonSchema(DEFAULT_SCHEMA_REGISTRY_NAME)
                                                : new UnownedJsonSchema(
                                                        DEFAULT_SCHEMA_REGISTRY_NAME))
                                : Optional.empty();
            }

            @Override
            public Part name() {
                return part;
            }

            @Override
            public SerializationFormat format() {
                return format;
            }

            @Override
            public Class<T> type() {
                return type;
            }

            @Override
            public KafkaTopicDescriptor<?, ?> topic() {
                return TopicDescriptor.this;
            }

            @Override
            public Stream<? extends ResourceDescriptor> resources() {
                return schema.stream();
            }

            private abstract class BaseJsonSchema implements JsonSchemaDescriptor<T> {

                private final String schemaRegistryName;

                @SuppressFBWarnings(
                        value = "CT_CONSTRUCTOR_THROW",
                        justification = "Private implementation types")
                private BaseJsonSchema(final String schemaRegistryName) {
                    this.schemaRegistryName =
                            requireNonNull(schemaRegistryName, "schemaRegistryName");
                }

                @Override
                public String schemaRegistryName() {
                    return schemaRegistryName;
                }

                @Override
                public PartDescriptor<T> part() {
                    return KeyValueDescriptor.this;
                }
            }

            private final class OwnedJsonSchema extends BaseJsonSchema
                    implements OwnedJsonSchemaDescriptor<T> {
                private OwnedJsonSchema(final String schemaRegistryName) {
                    super(schemaRegistryName);
                }
            }

            private final class UnownedJsonSchema extends BaseJsonSchema
                    implements UnownedJsonSchemaDescriptor<T> {
                private UnownedJsonSchema(final String schemaRegistryName) {
                    super(schemaRegistryName);
                }
            }
        }
    }

    private static final class OutputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements OwnedKafkaTopicOutput<K, V> {

        OutputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat,
                final TopicConfigBuilder config) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.of(config));
        }

        @Override
        public KafkaTopicInput<K, V> toInput() {
            return new UnownedInputTopicDescriptor<>(
                    name(), key().type(), key().format(), value().type(), value().format());
        }
    }

    private static final class InputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements OwnedKafkaTopicInput<K, V> {

        InputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat,
                final TopicConfigBuilder config) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.of(config));
        }

        @Override
        public KafkaTopicOutput<K, V> toOutput() {
            return new UnownedOutputTopicDescriptor<>(
                    name(), key().type(), key().format(), value().type(), value().format());
        }
    }

    private static final class UnownedInputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements KafkaTopicInput<K, V> {

        UnownedInputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.empty());
        }
    }

    private static final class UnownedOutputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements KafkaTopicOutput<K, V> {

        UnownedOutputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.empty());
        }
    }

    private static final class InternalTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements KafkaTopicInternal<K, V> {

        InternalTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.empty());
        }
    }

    private static final class CreatableInternalTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements CreatableKafkaTopicInternal<K, V> {

        CreatableInternalTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat,
                final TopicConfigBuilder config) {
            super(topicName, keyType, keyFormat, valueType, valueFormat, Optional.of(config));
        }
    }
}
