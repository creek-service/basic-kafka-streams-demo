import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import java.nio.file.Paths

plugins {
    application
    id("com.bmuschko.docker-remote-api") version "8.1.0"
}

val creekVersion : String by extra
val kafkaVersion : String by extra
val log4jVersion : String by extra

dependencies {
    implementation(project(":services"))
    implementation("org.creekservice:creek-service-context:$creekVersion")
    implementation("org.creekservice:creek-kafka-streams-extension:$creekVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:$log4jVersion")

    testImplementation("org.creekservice:creek-kafka-streams-test:$creekVersion")
}

// Patch Kafka Streams test jar into main Kafka Streams module to avoid split packages:
modularity.patchModule("kafka.streams", "kafka-streams-test-utils-$kafkaVersion.jar")

application {
    mainModule.set("example.mod.service")
    mainClass.set("org.acme.example.service.ServiceMain")
}

val buildAppImage = tasks.create("buildAppImage", DockerBuildImage::class) {
    dependsOn("prepareDocker")
    buildArgs.put("APP_NAME", project.name)
    buildArgs.put("APP_VERSION", "${project.version}")
    images.add("ghcr.io/creekservice/${rootProject.name}-${project.name}:latest")
}

tasks.register<Copy>("prepareDocker") {
    dependsOn("distTar")

    from(
        layout.projectDirectory.file("Dockerfile"),
        layout.buildDirectory.file("distributions/${project.name}-${project.version}.tar"),
        layout.projectDirectory.dir("include"),
    )

    // Include the AttachMe agent files if present in user's home directory:
    from (Paths.get(System.getProperty("user.home")).resolve(".attachme")) {
        into("agent")
    }

    // Ensure the agent dir exists even if the agent is not installed
    from (layout.projectDirectory.file(".ensureAgent")) {
        into("agent")
    }

    into(buildAppImage.inputDir)
}