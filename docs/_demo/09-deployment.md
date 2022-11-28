---
title: Deployment
permalink: /deployment
layout: single
---

Deploying the service to an environment is outside the scope of this tutorial, and the steps will vary depending on
the type of environment. However, below is information you may file useful when it comes deployment.

## Building Docker images

The repository comes preconfigured with the necessary tasks to build service's Docker image using the 
most excellent [com.bmuschko.docker-remote-api][dockerPlugin] Gradle plugin, 
with versioning handled by the similarly excellent [pl.allegro.tech.build.axion-release][releasePlugin]. 
Run the following to build the docker image:

```
./gradlew buildAppImage
```

This will build a docker container named `ghcr.io/creek-service/basic-kafka-streams-demo-reverse-service`.

**ProTip:** The Docker image name is defined both in the service's descriptor and the service's `buildAppImage`
Gradle task. These must be kept in sync if system tests are to work.
{: .notice--info}

## Pushing Docker images

The repository comes preconfigured with the necessary tasks to push each service's image to [GitHub's Container Registry][ghContainers].
The following Gradle command publishes the images:

```
./gradlew pushAppImage
```

This command runs are part of the [CI GitHub workflow][buildYml].

## Environment variables

The service requires, as a minimum, the `KAFKA_BOOTSTRAP_SERVERS` environment variable set to the location of the Kafka cluster to use. 
This maps to the [`bootstrap.servers`][bootstrapServersDocs] Kafka client config.

**ProTip:** More information about the environment variables supported by the Kafka Streams extension in the
[extension's documentation][kafkaExtEnvVars].
{: .notice--info}

[dockerPlugin]: https://plugins.gradle.org/plugin/com.bmuschko.docker-remote-api
[releasePlugin]: https://plugins.gradle.org/plugin/pl.allegro.tech.build.axion-release
[ghContainers]: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-container-registry
[buildYml]: https://github.com/creek-service/aggregate-template/blob/main/.github/workflows/build.yml
[bootstrapServersDocs]: https://kafka.apache.org/documentation/#producerconfigs_bootstrap.servers
[kafkaExtEnvVars]: https://github.com/creek-service/creek-kafka/tree/main/streams-extension#system-environment-variables
[todo]: update links above once documentation migrates