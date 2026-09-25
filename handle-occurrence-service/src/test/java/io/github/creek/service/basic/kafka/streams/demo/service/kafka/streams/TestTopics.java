package io.github.creek.service.basic.kafka.streams.demo.service.kafka.streams;

import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.creekservice.api.kafka.extension.KafkaClientsExtension;
import org.creekservice.api.kafka.extension.resource.KafkaTopic;
import org.creekservice.api.kafka.metadata.topic.KafkaTopicDescriptor;

public final class TestTopics {

    private TestTopics() {}

    @SuppressWarnings("resource")
    public static <K, V> TestInputTopic<K, V> inputTopic(
            final KafkaTopicDescriptor<K, V> topicDescriptor,
            final KafkaClientsExtension ext,
            final TopologyTestDriver testDriver) {
        final KafkaTopic<K, V> topic = ext.topic(topicDescriptor);
        return testDriver.createInputTopic(
                topicDescriptor.name(),
                topic.keySerde().serializer(),
                topic.valueSerde().serializer());
    }

    @SuppressWarnings("resource")
    public static <K, V> TestOutputTopic<K, V> outputTopic(
            final KafkaTopicDescriptor<K, V> topicDescriptor,
            final KafkaClientsExtension ext,
            final TopologyTestDriver testDriver) {
        final KafkaTopic<K, V> topic = ext.topic(topicDescriptor);
        return testDriver.createOutputTopic(
                topicDescriptor.name(),
                topic.keySerde().deserializer(),
                topic.valueSerde().deserializer());
    }
}
