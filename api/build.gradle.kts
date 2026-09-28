plugins {
    `java-library`
    id("org.creekservice.schema.json")
}

dependencies {
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.4.2")
    api("org.creekservice:creek-kafka-metadata:${property("creekVersion")}")
    api("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonVersion")}")
    implementation("org.creekservice:creek-base-annotation:${property("creekVersion")}")

    jsonSchemaGenerator("org.creekservice:creek-json-schema-generator:${property("creekVersion")}")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.

    testImplementation("org.apache.kafka:kafka-clients:${property("kafkaVersion")}")
}

creek.schema.json {
    typeScanning.moduleWhiteList(moduleName)
    subTypeScanning.moduleWhiteList(moduleName)
}