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

package io.github.creek.service.basic.kafka.streams.demo.api.model;

import static java.util.Objects.requireNonNull;

import org.creekservice.api.base.annotation.schema.GeneratesSchema;

@GeneratesSchema
public record TweetData(long id, String text) {

    public TweetData {
        requireNonNull(text, "text");
        if (text.isEmpty()) {
            throw new IllegalArgumentException("text cannot be empty");
        }
    }
}

// Todo: Can the model be moved to services?
//   Or, idealy, have the demo show output topics from the aggregate?
// Todo: Annotate text param with min length,.

// Todo: Consider adding a timestamp field? Maybe removing the Id field?
