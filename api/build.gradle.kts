// begin-snippet: plugins
plugins {
    `java-library`
    id("org.creekservice.schema.json")
}
// end-snippet

// begin-snippet: dependencies
dependencies {
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.4.2")
    compileOnlyApi("io.swagger.core.v3:swagger-annotations:${property("swaggerAnnotationsVersion")}")
    api("org.creekservice:creek-kafka-metadata:${property("creekVersion")}")
    api("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonVersion")}")
    implementation("org.creekservice:creek-base-annotation:${property("creekVersion")}")

    jsonSchemaGenerator("org.creekservice:creek-json-schema-generator:${property("creekVersion")}")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.

    testCompileOnly("io.swagger.core.v3:swagger-annotations:${property("swaggerAnnotationsVersion")}")
    testImplementation("org.apache.kafka:kafka-clients:${property("kafkaVersion")}")
}
// end-snippet

// begin-snippet: schema-plugin
creek.schema.json {
    typeScanning.moduleWhiteList(moduleName)
    subTypeScanning.moduleWhiteList(moduleName)
}
// end-snippet
