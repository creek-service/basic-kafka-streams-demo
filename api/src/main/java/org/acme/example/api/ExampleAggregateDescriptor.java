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

package org.acme.example.api;

import static org.acme.example.internal.TopicDescriptors.outputTopic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.acme.example.internal.TopicConfigBuilder;
import org.creekservice.api.kafka.metadata.OwnedKafkaTopicOutput;
import org.creekservice.api.platform.metadata.AggregateDescriptor;
import org.creekservice.api.platform.metadata.ComponentInput;
import org.creekservice.api.platform.metadata.ComponentOutput;

public final class ExampleAggregateDescriptor implements AggregateDescriptor {

    private static final List<ComponentInput> INPUTS = new ArrayList<>();
    private static final List<ComponentOutput> OUTPUTS = new ArrayList<>();
    // formatting:off init:remove
    private static final String AGGREGATE_PREFIX = "example.";                  // init:remove
                                                                                // init:remove
    public static final OwnedKafkaTopicOutput<Long, String> OutputTopic =       // init:remove
            register(                                                           // init:remove
                    outputTopic(                                                // init:remove
                            AGGREGATE_PREFIX + "output",                        // init:remove
                            Long.class,                                         // init:remove
                            String.class,                                       // init:remove
                            TopicConfigBuilder.withPartitions(2)));             // init:remove
    // formatting:on init:remove

    public ExampleAggregateDescriptor() {}

    @Override
    public Collection<ComponentInput> inputs() {
        return List.copyOf(INPUTS);
    }

    @Override
    public Collection<ComponentOutput> outputs() {
        return List.copyOf(OUTPUTS);
    }

    // Uncomment if needed
    // private static <T extends ComponentInput> T register(final T input) {
    //     INPUTS.add(input);
    //     return input;
    // }

    private static <T extends ComponentOutput> T register(final T output) {
        OUTPUTS.add(output);
        return output;
    }
}
