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

import static org.apache.kafka.streams.KeyValue.pair;
import static org.creekservice.api.kafka.metadata.KafkaTopicDescriptor.DEFAULT_CLUSTER_NAME;
import static org.creekservice.api.kafka.streams.test.TestTopics.inputTopic;
import static org.creekservice.api.kafka.streams.test.TestTopics.outputTopic;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import io.github.creek.service.basic.kafka.streams.demo.services.ExampleServiceDescriptor;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.creekservice.api.kafka.streams.extension.KafkaStreamsExtension;
import org.creekservice.api.kafka.streams.test.TestKafkaStreamsExtensionOptions;
import org.creekservice.api.service.context.CreekContext;
import org.creekservice.api.service.context.CreekServices;
import org.creekservice.api.test.util.TestPaths;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TopologyBuilderTest {

    private static CreekContext ctx;

    private TopologyTestDriver testDriver;
    private Topology topology;

    @BeforeAll
    public static void classSetup() {
        ctx =
                CreekServices.builder(new ExampleServiceDescriptor())
                        .with(TestKafkaStreamsExtensionOptions.defaults())
                        .build();
    }

    @BeforeEach
    public void setUp() {
        final KafkaStreamsExtension ext = ctx.extension(KafkaStreamsExtension.class);

        topology = new TopologyBuilder(ext).build();
        testDriver = new TopologyTestDriver(topology, ext.properties(DEFAULT_CLUSTER_NAME));
    }

    @AfterEach
    public void tearDown() {
        testDriver.close();
    }


    /**
     * A test that intentionally fails when ever the topology changes.
     *
     * <p>This is to make it less likely that unintentional changes to the topology are committed
     * and that thought is given to any intentional changes to ensure they won't break any deployed
     * instances.
     *
     * <p>Care must be taken when changing a deployed topology to ensure either:
     *
     * <ol>
     *   <li>Changes are backwards compatible and won't leave data stranded in unused topics, or
     *   <li>The existing topology is drained before the new topology is deployed
     * </ol>
     *
     * <p>Option #1 allows for the simplest deployment, but is not always possible or desirable.
     */
    @Test
    void shouldNotChangeTheTopologyUnintentionally() {
        // Given:
        final String expectedTopology =
                TestPaths.readString(
                        TestPaths.moduleRoot("example-service")
                                .resolve("src/test/resources/kafka/streams/expected_topology.txt"));

        // When:
        final String currentTopology = topology.describe().toString();

        // Then:
        assertThat(currentTopology.trim(), is(expectedTopology.trim()));
    }
}
