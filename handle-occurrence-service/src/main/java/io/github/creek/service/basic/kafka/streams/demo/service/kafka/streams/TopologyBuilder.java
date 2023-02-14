/*
 * Copyright 2022-2023 Creek Contributors (https://github.com/creek-service)
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

import static io.github.creek.service.basic.kafka.streams.demo.services.HandleOccurrenceServiceDescriptor.TweetHandleUsageTopic;
import static io.github.creek.service.basic.kafka.streams.demo.services.HandleOccurrenceServiceDescriptor.TweetTextTopic;
import static java.util.Objects.requireNonNull;
import static org.creekservice.api.kafka.metadata.KafkaTopicDescriptor.DEFAULT_CLUSTER_NAME;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
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

    // begin-snippet: build-method
    public Topology build() {
        final StreamsBuilder builder = new StreamsBuilder();

        // Pass a topic descriptor to the Kafka Streams extension to
        // obtain a typed `KafkaTopic` instance, which provides access to serde:
        final KafkaTopic<Long, String> input = ext.topic(TweetTextTopic);
        final KafkaTopic<String, Integer> output = ext.topic(TweetHandleUsageTopic);

        // Build a simple topology:
        // Consume input topic:
        builder.stream(
                        input.name(),
                        Consumed.with(input.keySerde(), input.valueSerde())
                                .withName(name.name("ingest-" + input.name())))
                // extract any Twitter handles in the tweet text:
                .flatMap(this::extractHandles, name.named("extract-handles"))
                // Finally, produce to output:
                .to(
                        output.name(),
                        Produced.with(output.keySerde(), output.valueSerde())
                                .withName(name.name("egress-" + output.name())));

        return builder.build(ext.properties(DEFAULT_CLUSTER_NAME));
    }
    // end-snippet

    // begin-snippet: extract-method
    private static final Pattern TWEET_HANDLE = Pattern.compile("(?<handle>@[a-zA-Z0-9_]*)");

    private Iterable<KeyValue<String, Integer>> extractHandles(
            final long tweetId, final String tweetText) {
        final Map<String, Integer> counts = new HashMap<>();
        final Matcher matcher = TWEET_HANDLE.matcher(tweetText);
        while (matcher.find()) {
            final String handle = matcher.group("handle");
            counts.compute(handle, (h, count) -> count == null ? 1 : count + 1);
        }

        return counts.entrySet().stream()
                .map(e -> KeyValue.pair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
    // end-snippet
}
