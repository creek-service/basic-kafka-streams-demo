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
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage

plugins {
    id("com.bmuschko.docker-remote-api")
    id("org.creekservice.system.test")
}

val creekVersion : String by extra

dependencies {
    // Make the `services` module, which contains the handle-occurrence-service's descriptor, available to the system tests:
    systemTestComponent(project(":services"))

    // Install the creek-kafka system-test extension:
    systemTestExtension("org.creekservice:creek-kafka-test-extension:$creekVersion")
}

tasks.systemTest {
    // Make the systemTest task be dependent on the output of all Docker image build tasks:
    rootProject.allprojects.flatMap {
        it.tasks.withType(DockerBuildImage::class)
    }.forEach {
        inputs.files(it)
    }
}
