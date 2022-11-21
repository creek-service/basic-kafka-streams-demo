---
title: Debugging services
permalink: /debugging
layout: single
---

The Gradle plugin used to run the system tests supports the debugging of the services running inside Docker containers.
This makes debugging issues with system tests so much less painful.


**Note:** Service debugging is currently only supported through the IntelliJ IDE. 
If you're using another IDE, then please consider contributing to get service debugging working with your IDE.
{: .notice--warning}

## Prerequisites

Debugging of services currently requires the [AttachMe][attachMe] IntelliJ plugin to be installed.

The `aggregate-template` repository has preconfigured the service to support debugging.
Details of what's required can be found in the [creek-system-test][[sysTestRequirements]] and 
[system-test Gradle plugin][pluginRequirements] docs.

## Debugging a service

With _AttachMe_ installed, the `reverse-service` can be debugged with the following steps.

1. Create and run an `AttachMe` run configuration.
   <figure>
     <img src="{{ '/assets/images/creek-create-attachme-run-config.png' | relative_url }}" alt="AttacheMe run configuration">
   </figure>
2. Name the new configuration, but leave the default port at the default `7857`.
   <figure>
     <img src="{{ '/assets/images/creek-attachme-run-config.png' | relative_url }}" alt="AttacheMe run configuration">
   </figure>
3. Place the required breakpoints in the code. For example, place one in the `TopologyBuilder`'s `switchKeyAndValue()`
   method.
   <figure>
     <img src="{{ '/assets/images/creek-add-breakpoint.png' | relative_url }}" alt="AttacheMe run configuration">
   </figure>
4. Run the system tests, specifying which service to debug.

   ```
   ./gradlew systemTest \
     --debug-service="reverse-service" \
     --verification-timeout-seconds=9999
   ```

When the system tests start the `reverse-service` Docker container, the service will attach to the debugger.
This will cause a new debug window to open and for the breakpoint to be hit:

<figure>
  <img src="{{ '/assets/images/creek-breakpoint-hit.png' | relative_url }}" alt="AttacheMe run configuration">
</figure>

**ProTip:** The `--verification-timeout-seconds` argument increases the amount of time the system tests wait 
for the expected output, allowing more time to debug the code. Learn more on this, and other options, 
in the [system test plugin][systemTestOptions] documentation.
{: .notice--info}

[attachMe]: https://plugins.jetbrains.com/plugin/13263-attachme
[sysTestRequirements]: https://github.com/creek-service/creek-system-test#configuring-a-service-for-debugging
[pluginRequirements]: https://github.com/creek-service/creek-system-test-gradle-plugin#dependency-management
[system test plugin]: https://github.com/creek-service/creek-system-test-gradle-plugin#systemtest---systemtest
[todo]: update links below to point to creekservice.org as docs migrate.