pluginManagement { // Todo: SNAPSHOT
    repositories {
        mavenLocal()
        gradlePluginPortal()

        // Public, unauthenticated repo Creek publishes SNAPSHOTs of every library & plugin to on every push to main:
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
