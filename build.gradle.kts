plugins {
    java
    jacoco
    `common-convention` apply false
    `module-convention` apply false
    `coverage-convention`
    `publishing-convention` apply false
    id("pl.allegro.tech.build.axion-release") version "1.19.1" // https://plugins.gradle.org/plugin/pl.allegro.tech.build.axion-release
    id("com.bmuschko.docker-remote-api") version "9.4.0" apply false
}

project.version = scmVersion.version

allprojects {
    tasks.jar {
        onlyIf { sourceSets.main.get().allSource.files.isNotEmpty() }
    }
}

subprojects {
    project.version = project.parent?.version!!

    apply(plugin = "common-convention")
    apply(plugin = "module-convention")

    if (!name.startsWith("test-")) {
        apply(plugin = "jacoco")
    }

    // Only publish the API module, as this is the only module other repos should need.
    if (name == "api") {
        apply(plugin = "publishing-convention")
    }

    extra.apply {
        set("creekVersion", "0.4.1")            // https://mvnrepository.com/artifact/org.creekservice
        set("kafkaVersion", "4.0.0")            // https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
        set("spotBugsVersion", "4.4.2")         // https://mvnrepository.com/artifact/com.github.spotbugs/spotbugs-annotations
        set("guavaVersion", "33.4.8-jre")         // https://mvnrepository.com/artifact/com.google.guava/guava
        set("log4jVersion", "2.25.1")           // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core

        set("junitVersion", "5.13.4")            // https://mvnrepository.com/artifact/org.junit.jupiter/junit-jupiter-api
        set("junitPioneerVersion", "2.3.0")     // https://mvnrepository.com/artifact/org.junit-pioneer/junit-pioneer
        set("mockitoVersion", "5.18.0")          // https://mvnrepository.com/artifact/org.mockito/mockito-junit-jupiter
    }

    val creekVersion : String by extra
    val guavaVersion : String by extra
    val log4jVersion : String by extra
    val kafkaVersion : String by extra
    val junitVersion: String by extra
    val junitPioneerVersion: String by extra
    val mockitoVersion: String by extra

    dependencies {
        testImplementation("org.creekservice:creek-test-hamcrest:$creekVersion")
        testImplementation("org.creekservice:creek-test-util:$creekVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        testImplementation("org.junit-pioneer:junit-pioneer:$junitPioneerVersion")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")
        testImplementation("com.google.guava:guava-testlib:$guavaVersion")
        testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    }

    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.apache.kafka") {
                // Need a known Kafka version for module patching to work:
                useVersion(kafkaVersion)
            }
        }
    }
}

defaultTasks("format", "static", "check")
