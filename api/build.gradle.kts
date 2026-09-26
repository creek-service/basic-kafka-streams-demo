plugins {
    `java-library`
    id("org.creekservice.schema.json")
}

val kafkaVersion: String by extra
val creekVersion : String by extra
val jacksonVersion : String by extra

dependencies {
    api("org.creekservice:creek-kafka-metadata:$creekVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("org.creekservice:creek-base-annotation:$creekVersion")

    jsonSchemaGenerator("org.creekservice:creek-json-schema-generator:$creekVersion")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.

    testImplementation("org.apache.kafka:kafka-clients:$kafkaVersion")
}

creek.schema.json {
    typeScanning.moduleWhiteList(moduleName)
    subTypeScanning.moduleWhiteList(moduleName)
}