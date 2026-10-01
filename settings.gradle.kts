pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()

        // Todo: SNAPSHOT
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
