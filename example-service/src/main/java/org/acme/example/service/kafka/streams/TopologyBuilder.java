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

package org.acme.example.service.kafka.streams;

import static java.util.Objects.requireNonNull;
import static org.creekservice.api.kafka.metadata.KafkaTopicDescriptor.DEFAULT_CLUSTER_NAME;

import org.acme.example.services.ExampleServiceDescriptor;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KeyValueMapper;
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

        // formatting:off init:remove
        final KafkaTopic<String, Long> inputTopic = ext                             // init:remove
                .topic(ExampleServiceDescriptor.InputTopic);                        // init:remove
        final KafkaTopic<Long, String> outputTopic =                                // init:remove
                ext.topic(ExampleServiceDescriptor.OutputTopic);                    // init:remove
                                                                                    // init:remove
        builder.stream(                                                             // init:remove
                        inputTopic.name(),                                          // init:remove
                        Consumed.with(inputTopic.keySerde(),                        // init:remove
                                        inputTopic.valueSerde())                    // init:remove
                                .withName(name.name("ingest-"                // init:remove
                                        + inputTopic.name())))                      // init:remove
                .map(switchKeyAndValue(), name.named("switch"))              // init:remove
                .to(                                                                // init:remove
                        outputTopic.name(),                                         // init:remove
                        Produced.with(outputTopic.keySerde(),                       // init:remove
                                        outputTopic.valueSerde())                   // init:remove
                                .withName(name.name("egress-"                // init:remove
                                        + outputTopic.name())));                    // init:remove
        // formatting:on  init:remove

        return builder.build(ext.properties(DEFAULT_CLUSTER_NAME));
    }
    // formatting:off  init:remove
    private KeyValueMapper<String, Long, KeyValue<Long, String>>                    // init:remove
        switchKeyAndValue() {                                                       // init:remove
          return (key, value) -> new KeyValue<>(value, key);                        // init:remove
    }                                                                               // init:remove
    // formatting:on   init:remove
}
