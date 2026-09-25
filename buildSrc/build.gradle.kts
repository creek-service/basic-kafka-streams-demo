/*
 * Copyright 2023-2025 Creek Contributors (https://github.com/creek-service)
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

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()

    // Todo: SNAPSHOT
    maven {
        url = uri("https://maven.pkg.github.com/creek-service/*")
        credentials {
            username = "Creek-Bot-Token"
            password = "\u0067hp_LtyvXrQZen3WlKenUhv21Mg6NG38jn0AO2YH"
        }
    }
}

val jvmTargetVer = JavaLanguageVersion.of(17)

java {
    toolchain.languageVersion.set(jvmTargetVer)
}

kotlin {
    jvmToolchain {
        languageVersion.set(jvmTargetVer)
    }
}


dependencies {
    implementation("com.github.spotbugs.snom:spotbugs-gradle-plugin:6.5.5")                // https://plugins.gradle.org/plugin/com.github.spotbugs
    implementation("com.diffplug.spotless:spotless-plugin-gradle:8.6.0")                   // https://plugins.gradle.org/plugin/com.diffplug.spotless
    implementation("org.javamodularity:moduleplugin:2.0.1")                                // https://plugins.gradle.org/plugin/org.javamodularity.moduleplugin
    implementation("io.github.gradle-nexus:publish-plugin:2.0.0")                           // https://plugins.gradle.org/plugin/io.github.gradle-nexus.publish-plugin
    // Todo: SNAPSHOT
    implementation("org.creekservice:creek-system-test-gradle-plugin:0.4.5-SNAPSHOT")                // https://plugins.gradle.org/plugin/org.creekservice.system.test
}