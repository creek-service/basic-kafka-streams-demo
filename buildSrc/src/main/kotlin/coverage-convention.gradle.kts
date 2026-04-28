/*
 * Copyright 2022-2025 Creek Contributors (https://github.com/creek-service)
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

/**
 * Standard coverage configuration of Creek aggregates, utilising Jacoco and Codecov.
 *
 * <p>Version:
 * <ul>
 * <li> 1.5: Removed wiring of system test execution data, as its handled by the plugin</li>
 * </ul>
 *
 *
 * <p>Apply to the root project only
 */

plugins {
    java
    jacoco
}

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "java")

    tasks.withType<JacocoReport>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}

tasks.register("coverage") {
    group = "creek"
    description = "generate coverage report"
    dependsOn("test", "jacocoTestReport")
}
