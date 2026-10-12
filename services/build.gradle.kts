/*
 * Copyright 2022-2026 Creek Contributors (https://github.com/creek-service)
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

// begin-snippet: plugins
plugins {
    `java-library`
    id("org.creekservice.schema.json")
}
// end-snippet

// begin-snippet: dependencies
dependencies {
    api(project(":api"))
    api("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonAnnotationsVersion")}")
    implementation("org.creekservice:creek-base-annotation:${property("creekVersion")}")
    compileOnlyApi("io.swagger.core.v3:swagger-annotations:${property("swaggerAnnotationsVersion")}")
    jsonSchemaGenerator("org.creekservice:creek-json-schema-generator:${property("creekVersion")}")
    testCompileOnly("io.swagger.core.v3:swagger-annotations:${property("swaggerAnnotationsVersion")}")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.
}
// end-snippet

// begin-snippet: schema-plugin
creek.schema.json {
    typeScanning.moduleWhiteList(moduleName)
    subTypeScanning.moduleWhiteList(moduleName)
}
// end-snippet
