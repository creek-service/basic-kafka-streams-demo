/*
 * Copyright 2022 Creek Contributors (https://github.com/creek-service)
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

package io.github.creek.service.basic.kafka.streams.demo.service.kafka.streams;

// begin-snippet: all
import static io.github.creek.service.basic.kafka.streams.demo.services.ReverseServiceDescriptor.InputTopic;
import static io.github.creek.service.basic.kafka.streams.demo.services.ReverseServiceDescriptor.OutputTopic;
import static java.util.Objects.requireNonNull;
import static org.creekservice.api.kafka.metadata.KafkaTopicDescriptor.DEFAULT_CLUSTER_NAME;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.creekservice.api.kafka.extension.resource.KafkaTopic;
import org.creekservice.api.kafka.streams.extension.KafkaStreamsExtension;
import org.creekservice.api.kafka.streams.extension.util.Name;

public final class TopologyBuilder {

    private final KafkaStreamsExtension ext;
    private final Name name = Name.root();

    public TopologyBuilder(final KafkaStreamsExtension ext) {
        this.ext = requireNonNull(ext, "ext");
    }

    public Topology build() {
        final StreamsBuilder builder = new StreamsBuilder();

        // Pass a topic descriptor to the Kafka Streams extension
        // to obtain a `KafkaTopic` instance, which provides access
        // to serde:
        final KafkaTopic<String, Long> input = ext.topic(InputTopic);
        final KafkaTopic<Long, String> output = ext.topic(OutputTopic);

        // Build a simple topology:
        // Consume input topic:
        builder.stream(
                        input.name(),
                        Consumed.with(input.keySerde(), input.valueSerde())
                                .withName(name.name("ingest-" + input.name())))
                // Transform each record:
                .map(this::switchKeyAndValue, name.named("switch"))
                // Finally, produce to output:
                .to(
                        output.name(),
                        Produced.with(output.keySerde(), output.valueSerde())
                                .withName(name.name("egress-" + output.name())));

        return builder.build(ext.properties(DEFAULT_CLUSTER_NAME));
    }

    private KeyValue<Long, String> switchKeyAndValue(final String key, final Long value) {
        // Swap that key and value over:
        return new KeyValue<>(value, key);
    }
}
// end-snippet
