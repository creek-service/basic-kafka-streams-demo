pluginManagement {
    repositories {
        gradlePluginPortal()
        // TODO(0.5.1 release): Remove this temporary Creek SNAPSHOT repository.
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}

rootProject.name = "basic-kafka-streams-demo"

include(
    "api",
    "handle-occurrence-service",
    "services",
    "system-tests"
)
