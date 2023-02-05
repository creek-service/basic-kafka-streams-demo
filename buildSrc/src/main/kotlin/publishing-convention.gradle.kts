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

/**
 * Standard configuration for Creek aggregates library publishing
 *
 * <p>Version: 1.2
 *
 * <p> Apply this plugin only to subprojects if in multi-module setup.
 *
 * <p> Use `creek-plugin-publishing-convention` for Gradle plugins.
 */

plugins {
    java
    `maven-publish`
}

java {
    withSourcesJar()
}

publishing {
    repositories {
        // ChangeMe: will publish to GitHub package:
        // update to publish jars to your artefact store, as required
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/creek-service/${rootProject.name}")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifactId = "${rootProject.name}-${project.name}"
        }
    }
}
