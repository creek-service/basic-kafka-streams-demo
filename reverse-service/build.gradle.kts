import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPushImage

plugins {
    application
    id("com.bmuschko.docker-remote-api")
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
    mainModule.set("basic.kafka.streams.demo.service")
    mainClass.set("io.github.creek.service.basic.kafka.streams.demo.service.ServiceMain")
}

val buildAppImage = tasks.register<DockerBuildImage>("buildAppImage") {
    dependsOn("prepareDocker")
    buildArgs.put("APP_NAME", project.name)
    buildArgs.put("APP_VERSION", "${project.version}")
    images.add("ghcr.io/creek-service/${rootProject.name}-${project.name}:latest")
    images.add("ghcr.io/creek-service/${rootProject.name}-${project.name}:${project.version}")
}

tasks.register<Copy>("prepareDocker") {
    dependsOn("distTar")

    from(
        layout.projectDirectory.file("Dockerfile"),
        layout.buildDirectory.file("distributions/${project.name}-${project.version}.tar"),
        layout.projectDirectory.dir("include"),
    )

    into(buildAppImage.get().inputDir)
}

tasks.register<DockerPushImage>("pushAppImage") {
    dependsOn("buildAppImage")
    images.add("ghcr.io/creek-service/${rootProject.name}-${project.name}:latest")
    images.add("ghcr.io/creek-service/${rootProject.name}-${project.name}:${project.version}")
}
