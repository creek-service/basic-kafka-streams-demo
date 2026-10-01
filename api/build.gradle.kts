plugins {
    `java-library`
}

dependencies {
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.10.4")
    api("org.creekservice:creek-kafka-metadata:${property("creekVersion")}")

    // To avoid dependency hell downstream, avoid adding any more dependencies except Creek metadata jars and test dependencies.

    testImplementation("org.apache.kafka:kafka-clients:${property("kafkaVersion")}")
}