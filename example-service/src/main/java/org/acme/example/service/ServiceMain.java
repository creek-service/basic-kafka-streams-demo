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

package org.acme.example.service;


import org.acme.example.service.kafka.streams.TopologyBuilder;
import org.acme.example.services.ExampleServiceDescriptor;
import org.apache.kafka.streams.Topology;
import org.creekservice.api.kafka.streams.extension.KafkaStreamsExtension;
import org.creekservice.api.kafka.streams.extension.KafkaStreamsExtensionOptions;
import org.creekservice.api.service.context.CreekContext;
import org.creekservice.api.service.context.CreekServices;

/** Entry point of the service */
public final class ServiceMain {

    private ServiceMain() {}

    public static void main(final String... args) {
        final CreekContext ctx =
                CreekServices.builder(new ExampleServiceDescriptor())
                        .with(
                                KafkaStreamsExtensionOptions.builder()
                                        // Customize the Kafka streams extension...
                                        .build())
                        .build();

        final KafkaStreamsExtension ext = ctx.extension(KafkaStreamsExtension.class);
        final Topology topology = new TopologyBuilder(ext).build();
        ext.execute(topology);
    }
}
