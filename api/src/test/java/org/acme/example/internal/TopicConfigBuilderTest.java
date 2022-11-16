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

package org.acme.example.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;

import java.time.Duration;
import java.util.Map;
import org.apache.kafka.common.config.TopicConfig;
import org.creekservice.api.kafka.metadata.KafkaTopicConfig;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

class TopicConfigBuilderTest {

    @Test
    void shouldSetPartitions() {
        MatcherAssert.assertThat(TopicConfigBuilder.withPartitions(4).build().partitions(), is(4));
    }

    @Test
    void shouldThrowOnZeroPartitions() {
        // When:
        final Exception e =
                assertThrows(
                        IllegalArgumentException.class, () -> TopicConfigBuilder.withPartitions(0));

        // Then:
        assertThat(e.getMessage(), is("partition count must be positive, but was 0"));
    }

    @Test
    void shouldThrowOnNegativePartitions() {
        // When:
        final Exception e =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> TopicConfigBuilder.withPartitions(-1));

        // Then:
        assertThat(e.getMessage(), is("partition count must be positive, but was -1"));
    }

    @Test
    void shouldThrowOnCrazyHighPartitions() {
        // When:
        final Exception e =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> TopicConfigBuilder.withPartitions(10_001));

        // Then:
        assertThat(
                e.getMessage(), is("partition count should be less than 10,000, but was 10,001"));
    }

    @Test
    void shouldAllowCrazyHighPartitions() {
        // When:
        final TopicConfigBuilder builder = TopicConfigBuilder.builder(10_001, true);

        // Then:
        assertThat(builder.build().partitions(), is(10_001));
    }

    @Test
    void shouldTurnOnKeyCompaction() {
        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withKeyCompaction().build();

        // Then:
        assertThat(
                config.config(),
                hasEntry(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT));
    }

    @Test
    void shouldTurnOnKeyCompactionAndDeletion() {
        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withKeyCompactionAndDeletion().build();

        // Then:
        assertThat(
                config.config(),
                hasEntry(
                        TopicConfig.CLEANUP_POLICY_CONFIG,
                        TopicConfig.CLEANUP_POLICY_COMPACT
                                + ","
                                + TopicConfig.CLEANUP_POLICY_DELETE));
    }

    @Test
    void shouldSetRetentionTime() {
        // Given:
        final Duration duration = Duration.ofMillis(1234);

        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withRetentionTime(duration).build();

        // Then:
        assertThat(
                config.config(),
                hasEntry(TopicConfig.RETENTION_MS_CONFIG, "" + duration.toMillis()));
    }

    @Test
    void shouldSetInfiniteRetention() {
        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withInfiniteRetention().build();

        // Then:
        assertThat(config.config(), hasEntry(TopicConfig.RETENTION_MS_CONFIG, "-1"));
    }

    @Test
    void shouldSetSegmentSize() {
        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withSegmentSize(50 * 1024).build();

        // Then:
        assertThat(config.config(), hasEntry(TopicConfig.SEGMENT_BYTES_CONFIG, "51200"));
    }

    @Test
    void shouldSetConfigs() {
        // When:
        final KafkaTopicConfig config =
                TopicConfigBuilder.withPartitions(4).withConfigs(Map.of("a", "b")).build();

        // Then:
        assertThat(config.config(), hasEntry("a", "b"));
    }
}
