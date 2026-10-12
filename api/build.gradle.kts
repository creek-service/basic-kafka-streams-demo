// begin-snippet: plugins
plugins {
    `java-library`
}
// end-snippet

// begin-snippet: dependencies
dependencies {
    api("org.creekservice:creek-kafka-metadata:${property("creekVersion")}")

    compileOnlyApi("com.github.spotbugs:spotbugs-annotations:${property("spotBugsVersion")}")
    compileOnlyApi("org.creekservice:creek-base-annotation:${property("creekVersion")}")
    compileOnlyApi("com.fasterxml.jackson.core:jackson-annotations:${property("jacksonAnnotationsVersion")}")
    compileOnlyApi("io.swagger.core.v3:swagger-annotations:${property("swaggerAnnotationsVersion")}")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.

    testImplementation("org.apache.kafka:kafka-clients:${property("kafkaVersion")}")
}
// end-snippet
