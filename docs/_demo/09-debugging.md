---
title: Debugging services running in Docker containers
permalink: /debugging
description: Learn how the Creek aggregate template enables easy debugging of your microservice code running in a Docker container
layout: single
---

The Gradle plugin used to run the system tests supports the debugging of the services running inside Docker containers.
This makes debugging issues with system tests so much less painful.

**Note:** Service debugging is currently only supported through the IntelliJ IDE. 
If you're using another IDE, then please consider contributing to get service debugging working for your IDE.
{: .notice--warning}

## Prerequisites

Debugging of services currently requires the [AttachMe][attachMe] IntelliJ plugin to be installed.

The `aggregate-template` repository has preconfigured the service to support debugging.

**ProTip:** Details of what configuration is required, if you want to dig into the details, can be found in the
[creek-system-test][sysTestRequirements] and [system-test Gradle plugin][pluginRequirements] docs.
{: .notice--info}

## Debugging a service

With [AttachMe][attachMe] plugin installed, the `handle-occurrence-service` can be debugged with the following steps:

1. Create and run an `AttachMe` run configuration.
   {% include figure image_path="/assets/images/creek-create-attachme-run-config.png" alt="AttacheMe run configuration" %}
2. Name the new configuration, but leave the default port at the default `7857`.
   {% include figure image_path="/assets/images/creek-attachme-run-config.png" alt="AttacheMe run configuration details" %}   
3. Place the required breakpoints in the code. For example, place one in the `TopologyBuilder`'s `extractHandles()`
   method.
   {% include figure image_path="/assets/images/creek-add-breakpoint.png" alt="Add breakpoint" %}
4. Run the system tests, specifying which service to debug:

   ```
   ./gradlew systemTest \
     --debug-service="handle-occurrence-service" \
     --verification-timeout-seconds=9999
   ```

When the system tests start the `handle-occurrence-service` Docker container, the service will attach to the debugger.
This will cause a new debug window to open and for the breakpoint to be hit:

{% include figure image_path="/assets/images/creek-breakpoint-hit.png" alt="Breakpoint hit" %}

Pretty cool, right?

**ProTip:** The `--verification-timeout-seconds` argument increases the amount of time the system tests wait 
for the expected output, allowing more time to debug the code. Learn more on this, and other options, 
in the [system test plugin][systemTestOptions] documentation.
{: .notice--info}

## Debugging a 3rd-party service

Debugging your service code is way cool. But that's not the end of it. With Creek system tests you can also debug
any services started by test extensions too. For example, if any service under test references Kafka resources,
the Creek Kafka test extension will start a Kafka broker, and Creek makes debugging the Kafka broker easy:

With [AttachMe][attachMe] plugin installed, the Kafka broker can be debugged with the following steps:

1. Open the Kafka broker code in your IDE, making sure the code matches the version of the broker.
2. Create and run an `AttachMe` run configuration.
   {% include figure image_path="/assets/images/creek-create-attachme-run-config.png" alt="AttacheMe run configuration" %}
3. Name the new configuration, but leave the default port at the default `7857`.
   {% include figure image_path="/assets/images/creek-attachme-run-config.png" alt="AttacheMe run configuration details" %}
4. Place the required breakpoints in the Kafka broker code.
5. Run the system tests, specifying `kafka-broker` as the name of the service to debug:

   ```
   ./gradlew systemTest \
     --debug-service="kafka-broker" \
     --verification-timeout-seconds=9999
   ```

   Alternatively, if your test suite starts multiple Kafka brokers as the service use multiple clusters, you can 
   debug a specific broker by using the _instance name_, rather than the _service name_:
   
   ```
   ./gradlew systemTest \
     --debug-service-instance="kafka-broker-0" \
     --verification-timeout-seconds=9999
   ```

When the system tests start the Kafka broker's Docker container, the service will attach to the debugger.
This will cause a new debug window to open and for the breakpoint to be hit.

Pretty cool, right?

[todo]: test the above and maybe add some images!

[attachMe]: https://plugins.jetbrains.com/plugin/13263-attachme
[sysTestRequirements]: https://github.com/creek-service/creek-system-test#configuring-a-service-for-debugging
[pluginRequirements]: https://github.com/creek-service/creek-system-test-gradle-plugin#dependency-management
[systemTestOptions]: https://github.com/creek-service/creek-system-test-gradle-plugin#systemtest---systemtest
[todo]: update links below to point to creekservice.org as docs migrate.
