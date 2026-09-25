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

import static java.util.Objects.requireNonNull;
import static org.creekservice.api.kafka.metadata.SerializationFormat.serializationFormat;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;
import org.creekservice.api.kafka.metadata.SerializationFormat;
import org.creekservice.api.kafka.metadata.schema.JsonSchemaDescriptor;
import org.creekservice.api.kafka.metadata.schema.OwnedJsonSchemaDescriptor;
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
import org.creekservice.api.platform.metadata.ResourceDescriptor;

/**
 * Helper for creating topic descriptors.
 *
 * <p>Wondering where the builds are for {@link
 * org.creekservice.api.kafka.metadata.topic.KafkaTopicInput} or {@link
 * org.creekservice.api.kafka.metadata.topic.KafkaTopicOutput}? These should only be created by
 * calling {@link OwnedKafkaTopicInput#toOutput()} and {@link OwnedKafkaTopicOutput#toInput()} on an
 * owned topic descriptor, respectively.
 */
@SuppressWarnings("unused") // What is unused today may be used tomorrow...
public final class TopicDescriptors {

    public static final SerializationFormat KAFKA_FORMAT = serializationFormat("kafka");
    public static final SerializationFormat JSON_FORMAT = JsonSchemaKafkaSerde.format();

    private TopicDescriptors() {}

    /**
     * Create an input Kafka topic descriptor.
     *
     * <p>Looking for a version that returns {@link
     * org.creekservice.api.kafka.metadata.KafkaTopicInput}? Get one of those by calling {@link
     * OwnedKafkaTopicOutput#toInput()} on the topic descriptor defined in the upstream component.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value.
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
        return inputTopic(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, config);
    }

    // Todo: could have these methods determine key/value formats by seeing if they are supported by
    //   Kafka format and using JSON if not.

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
     * Create a Kafka topic descriptor for a topic that is implicitly created.
     *
     * <p>Most internal topics, e.g. Kafka Streams changelog and repartition topics, are implicitly
     * created, and this is the method to use to build a descriptor for them.
     *
     * <p>For an internal topic that you want Creek to create, use {@link #creatableInternalTopic}.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the internal topic descriptor.
     */
    public static <K, V> KafkaTopicInternal<K, V> internalTopic(
            final String topicName, final Class<K> keyType, final Class<V> valueType) {
        return new InternalTopicDescriptor<>(topicName, keyType, valueType);
    }

    /**
     * Create a Kafka topic descriptor for a topic that is implicitly created.
     *
     * <p>Most internal topics, e.g. Kafka Streams changelog and repartition topics, are implicitly
     * created For such topics use {@link #internalTopic}
     *
     * <p>For an internal topic that you want Creek to create, use this method.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value.
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
        return new CreatableInternalTopicDescriptor<>(topicName, keyType, valueType, config);
    }

    /**
     * Create an output Kafka topic descriptor.
     *
     * <p>Looking for a version that returns {@link
     * org.creekservice.api.kafka.metadata.KafkaTopicOutput}? Get one of those by calling {@link
     * OwnedKafkaTopicInput#toOutput()} on the topic descriptor defined in the downstream component.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value.
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
        return outputTopic(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, config);
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

    /**
     * Create an input Kafka topic descriptor with JSON value.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value (JSON).
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the input topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicInput<K, V> inputTopicWithJsonValue(
            final String topicName,
            final Class<K> keyType,
            final Class<V> valueType,
            final TopicConfigBuilder config) {
        return inputTopic(topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT, config);
    }

    /**
     * Create an output Kafka topic descriptor with JSON value.
     *
     * @param topicName the name of the topic
     * @param keyType the type serialized into the Kafka record key.
     * @param valueType the type serialized into the Kafka record value (JSON).
     * @param config the config of the topic.
     * @param <K> the type serialized into the Kafka record key.
     * @param <V> the type serialized into the Kafka record value.
     * @return the output topic descriptor.
     */
    public static <K, V> OwnedKafkaTopicOutput<K, V> outputTopicWithJsonValue(
            final String topicName,
            final Class<K> keyType,
            final Class<V> valueType,
            final TopicConfigBuilder config) {
        return outputTopic(topicName, keyType, KAFKA_FORMAT, valueType, JSON_FORMAT, config);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private static final class KeyValueDescriptor<T> implements PartDescriptor<T> {

        private final Part part;
        private final Class<T> type;
        private final SerializationFormat format;
        private final KafkaTopicDescriptor<?, ?> topic;
        private final Optional<JsonSchemaDescriptor<T>> schema;

        KeyValueDescriptor(
                final Part part,
                final Class<T> type,
                final SerializationFormat format,
                final KafkaTopicDescriptor<?, ?> topic,
                final String schemaRegistryName) {
            this.part = requireNonNull(part, "part");
            this.type = requireNonNull(type, "type");
            this.format = requireNonNull(format, "format");
            this.topic = requireNonNull(topic, "topic");
            this.schema =
                    // Todo: Invert.
                    format.equals(JsonSchemaKafkaSerde.format())
                            ? Optional.of(
                                    // Todo: won't always be owned, right?
                                    new OwnedJsonSchema<>(part, type, schemaRegistryName, topic))
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
            return topic;
        }

        @Override
        public Stream<? extends ResourceDescriptor> resources() {
            return schema.stream();
        }

        private static final class OwnedJsonSchema<T> implements OwnedJsonSchemaDescriptor<T> {
            private final Part part;
            private final Class<T> type;
            private final String schemaRegistryName;
            private final KafkaTopicDescriptor<?, ?> topic;

            // Todo: Accept PartDescriptor, not part and topic.
            private OwnedJsonSchema(
                    final Part part,
                    final Class<T> type,
                    final String schemaRegistryName,
                    final KafkaTopicDescriptor<?, ?> topic) {
                this.part = part;
                this.type = type;
                this.schemaRegistryName = schemaRegistryName;
                this.topic = topic;
            }

            @Override
            public String schemaRegistryName() {
                return schemaRegistryName;
            }

            @Override
            public PartDescriptor<T> part() {
                return new PartDescriptor<T>() {
                    @Override
                    public Part name() {
                        return part;
                    }

                    @Override
                    public SerializationFormat format() {
                        return JsonSchemaKafkaSerde.format();
                    }

                    @Override
                    public Class<T> type() {
                        return type;
                    }

                    @Override
                    public KafkaTopicDescriptor<?, ?> topic() {
                        return topic;
                    }

                    // Todo: why return self?  Badly structured?
                    @Override
                    public Stream<? extends ResourceDescriptor> resources() {
                        return Stream.of(OwnedJsonSchema.this);
                    }
                };
            }
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private abstract static class TopicDescriptor<K, V> implements KafkaTopicDescriptor<K, V> {

        private static final String DEFAULT_SCHEMA_REGISTRY_NAME = "default";

        private final String topicName;
        private final PartDescriptor<K> key;
        private final PartDescriptor<V> value;
        private final Optional<KafkaTopicConfig> config;

        TopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final SerializationFormat keyFormat,
                final Class<V> valueType,
                final SerializationFormat valueFormat,
                final Optional<TopicConfigBuilder> config) {
            this.topicName = requireNonNull(topicName, "topicName");
            this.key =
                    new KeyValueDescriptor<>(
                            Part.key, keyType, keyFormat, this, DEFAULT_SCHEMA_REGISTRY_NAME);
            this.value =
                    new KeyValueDescriptor<>(
                            Part.value, valueType, valueFormat, this, DEFAULT_SCHEMA_REGISTRY_NAME);
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
    }

    private static final class OutputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements OwnedKafkaTopicOutput<K, V> {

        OutputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final Class<V> valueType,
                final TopicConfigBuilder config) {
            super(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, Optional.of(config));
        }

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
            return new KafkaTopicInput<>() {
                @Override
                public URI id() {
                    return OutputTopicDescriptor.this.id();
                }

                @Override
                public String name() {
                    return OutputTopicDescriptor.this.name();
                }

                @Override
                public PartDescriptor<K> key() {
                    return OutputTopicDescriptor.this.key();
                }

                @Override
                public PartDescriptor<V> value() {
                    return OutputTopicDescriptor.this.value();
                }
            };
        }
    }

    private static final class InputTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements OwnedKafkaTopicInput<K, V> {

        InputTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final Class<V> valueType,
                final TopicConfigBuilder config) {
            super(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, Optional.of(config));
        }

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
            return new KafkaTopicOutput<>() {
                @Override
                public URI id() {
                    return InputTopicDescriptor.this.id();
                }

                @Override
                public String name() {
                    return InputTopicDescriptor.this.name();
                }

                @Override
                public PartDescriptor<K> key() {
                    return InputTopicDescriptor.this.key();
                }

                @Override
                public PartDescriptor<V> value() {
                    return InputTopicDescriptor.this.value();
                }
            };
        }
    }

    private static final class InternalTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements KafkaTopicInternal<K, V> {

        InternalTopicDescriptor(
                final String topicName, final Class<K> keyType, final Class<V> valueType) {
            super(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, Optional.empty());
        }
    }

    private static final class CreatableInternalTopicDescriptor<K, V> extends TopicDescriptor<K, V>
            implements CreatableKafkaTopicInternal<K, V> {

        CreatableInternalTopicDescriptor(
                final String topicName,
                final Class<K> keyType,
                final Class<V> valueType,
                final TopicConfigBuilder config) {
            super(topicName, keyType, KAFKA_FORMAT, valueType, KAFKA_FORMAT, Optional.of(config));
        }
    }
}
