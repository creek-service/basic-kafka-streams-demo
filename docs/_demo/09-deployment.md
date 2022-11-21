---
title: Deployment
permalink: /deployment
layout: single
---

Deploying the service to an environment is outside the scope of this tutorial, and the steps will vary depending on
the type of environment. However, below is information you may file useful when it comes deployment.

## Building Docker images

The `aggregate-template` repository adds the necessary tasks to build service's Docker image using the 
most excellent [com.bmuschko.docker-remote-api][dockerPlugin] Gradle plugin.
Run the following to build the docker image:

```
./gradlew buildAppImage
```

This will build a docker container named `ghcr.io/creekservice/basic-kafka-streams-demo-reverse-service`.

**ProTip:** The Docker image name is defined both in the service's descriptor and the service's `buildAppImage`
Gradle task. These must be kept in sync if system tests are to work.
{: .notice--info}

## Environment variables

The service requires, as a minimum, the `KAFKA_BOOTSTRAP_SERVERS` environment variable set to the location of the Kafka cluster to use. 
This maps to the [`bootstrap.servers`][bootstrapServersDocs] Kafka client config.

**ProTip:** More information about the environment variables supported by the Kafka Streams extension in the
[extension's documentation][kafkaExtEnvVars].
{: .notice--info}

[dockerPlugin]: https://plugins.gradle.org/plugin/com.bmuschko.docker-remote-api
[bootstrapServersDocs]: https://kafka.apache.org/documentation/#producerconfigs_bootstrap.servers
[kafkaExtEnvVars]: https://github.com/creek-service/creek-kafka/tree/main/streams-extension#system-environment-variables
[todo]: update links above once documentation migrates