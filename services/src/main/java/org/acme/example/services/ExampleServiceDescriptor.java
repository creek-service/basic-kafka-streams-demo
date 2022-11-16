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

package org.acme.example.services;

import static org.acme.example.internal.TopicDescriptors.inputTopic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.acme.example.api.ExampleAggregateDescriptor;
import org.acme.example.internal.TopicConfigBuilder;
import org.creekservice.api.kafka.metadata.OwnedKafkaTopicInput;
import org.creekservice.api.kafka.metadata.OwnedKafkaTopicOutput;
import org.creekservice.api.platform.metadata.ComponentInput;
import org.creekservice.api.platform.metadata.ComponentInternal;
import org.creekservice.api.platform.metadata.ComponentOutput;
import org.creekservice.api.platform.metadata.ServiceDescriptor;

public final class ExampleServiceDescriptor implements ServiceDescriptor {

    private static final List<ComponentInput> INPUTS = new ArrayList<>();
    private static final List<ComponentInternal> INTERNALS = new ArrayList<>();
    private static final List<ComponentOutput> OUTPUTS = new ArrayList<>();
    // formatting:off init:remove
    public static final OwnedKafkaTopicInput<String, Long> InputTopic =             // init:remove
            register(                                                               // init:remove
                    inputTopic(                                                     // init:remove
                            "input",                                                // init:remove
                            String.class,                                           // init:remove
                            Long.class,                                             // init:remove
                            TopicConfigBuilder.withPartitions(3)));                 // init:remove
                                                                                    // init:remove
    public static final OwnedKafkaTopicOutput<Long, String> OutputTopic =           // init:remove
            register(ExampleAggregateDescriptor.OutputTopic);                       // init:remove
    // formatting:on  init:remove

    public ExampleServiceDescriptor() {}

    @Override
    public String dockerImage() {
        return "ghcr.io/creekservice/aggregate-template-example-service";
    }

    @Override
    public Collection<ComponentInput> inputs() {
        return List.copyOf(INPUTS);
    }

    @Override
    public Collection<ComponentInternal> internals() {
        return List.copyOf(INTERNALS);
    }

    @Override
    public Collection<ComponentOutput> outputs() {
        return List.copyOf(OUTPUTS);
    }

    private static <T extends ComponentInput> T register(final T input) {
        INPUTS.add(input);
        return input;
    }

    // Uncomment if needed:
    // private static <T extends ComponentInternal> T register(final T internal) {
    //     INTERNALS.add(internal);
    //     return internal;
    // }

    private static <T extends ComponentOutput> T register(final T output) {
        OUTPUTS.add(output);
        return output;
    }
}
