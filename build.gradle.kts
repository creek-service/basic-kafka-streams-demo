plugins {
    java
    jacoco
    `common-convention` apply false
    `module-convention` apply false
    `coverage-convention`
    `publishing-convention` apply false
    id("pl.allegro.tech.build.axion-release") version "1.21.3" // https://plugins.gradle.org/plugin/pl.allegro.tech.build.axion-release
    id("com.bmuschko.docker-remote-api") version "10.0.0" apply false
    id("org.creekservice.schema.json") version "0.5.0-SNAPSHOT" apply false
}

project.version = scmVersion.version

allprojects {
    tasks.jar {
        onlyIf { sourceSets.main.get().allSource.files.isNotEmpty() }
    }
}

subprojects {
    project.version = project.parent?.version!!

    pluginManager.apply("common-convention")
    pluginManager.apply("module-convention")

    if (!name.startsWith("test-")) {
        pluginManager.apply("jacoco")
    }

    // Only publish the API module, as this is the only module other repos should need.
    if (name == "api") {
        pluginManager.apply("publishing-convention")
    }

    val creekVersion = property("creekVersion") as String
    val junitVersion = property("junitVersion") as String

    dependencies {
        testImplementation("org.creekservice:creek-test-hamcrest:$creekVersion")
        testImplementation("org.creekservice:creek-test-util:$creekVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        testImplementation("org.junit-pioneer:junit-pioneer:${property("junitPioneerVersion")}")
        testImplementation("org.mockito:mockito-junit-jupiter:${property("mockitoVersion")}")
        testImplementation("com.google.guava:guava-testlib:${property("guavaVersion")}")
        testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:${property("log4jVersion")}")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.apache.kafka") {
                // Need a known Kafka version for module patching to work:
                useVersion(property("kafkaVersion") as String)
            }
        }
    }
}

defaultTasks("format", "static", "check")

